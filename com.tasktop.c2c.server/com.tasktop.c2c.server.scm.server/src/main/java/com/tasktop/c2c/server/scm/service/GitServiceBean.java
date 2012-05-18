/*******************************************************************************
S * Copyright (c) 2010, 2012 Tasktop Technologies
 * Copyright (c) 2010, 2011 SpringSource, a division of VMware
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.scm.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.identity.Gravatar;
import com.tasktop.c2c.server.common.service.query.QueryUtil;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.DiffEntry.ChangeType;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.domain.ScmType;

@Component
public class GitServiceBean implements GitService, InitializingBean {

	private static final String GIT_DIR_SUFFIX = ".git";

	@Value("${git.root}")
	String basePath;

	@Autowired
	private ScmServiceConfiguration profileServiceConfiguration;

	@Value("${git.startThreads}")
	private boolean startThreads = true;

	private List<Commit> getLog(Repository repository, Region region) {
		List<Commit> result = new ArrayList<Commit>();

		for (RevCommit revCommit : getAllCommits(repository)) {
			Commit commit = createCommit(revCommit);
			commit.setRepository(repository.getDirectory().getName());
			result.add(commit);
		}

		return result;
	}

	/**
	 * @param revCommit
	 * @return
	 */
	private Commit createCommit(RevCommit revCommit) {
		Commit commit = new Commit(revCommit.getName(), fromPersonIdent(revCommit.getAuthorIdent()), revCommit
				.getAuthorIdent().getWhen(), revCommit.getFullMessage());
		commit.setParents(new ArrayList<String>(revCommit.getParentCount()));
		for (ObjectId parentId : revCommit.getParents()) {
			commit.getParents().add(parentId.getName());
		}
		if (revCommit.getCommitterIdent() != null && !revCommit.getAuthorIdent().equals(revCommit.getCommitterIdent())) {
			commit.setCommitter(fromPersonIdent(revCommit.getCommitterIdent()));
			commit.setCommitDate(revCommit.getCommitterIdent().getWhen());
		}

		return commit;
	}

	private Profile fromPersonIdent(PersonIdent person) {
		Profile result = new Profile();
		result.setEmail(person.getEmailAddress());
		result.setUsername(person.getEmailAddress());
		result.setGravatarHash(Gravatar.computeHash(person.getEmailAddress()));
		int firstSpace = person.getName().indexOf(" ");
		String firstName = firstSpace == -1 ? "" : person.getName().substring(0, firstSpace);
		String lastName = firstSpace == -1 ? person.getName() : person.getName().substring(firstSpace + 1);
		result.setFirstName(firstName);
		result.setLastName(lastName);

		return result;
	}

	private static final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;
	private static final TimeZone tz = TimeZone.getTimeZone("PST"); // FIXME

	private List<ScmSummary> createEmptySummaries(int numDays) {
		List<ScmSummary> result = new ArrayList<ScmSummary>(numDays);

		Calendar cal = Calendar.getInstance(tz);
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Long now = cal.getTimeInMillis();
		for (int i = numDays - 1; i >= 0; i--) {
			ScmSummary summary = new ScmSummary();

			summary.setDate(new Date(now - i * MILLISECONDS_PER_DAY));
			summary.setAmount(0l);
			result.add(summary);
		}
		return result;
	}

	private void addCommitsToSummary(Repository repo, List<ScmSummary> summary) {
		Date firstDate = summary.get(0).getDate();

		for (RevCommit revCommit : getAllCommits(repo)) {
			Date commitDate = revCommit.getAuthorIdent().getWhen();
			if (commitDate.before(firstDate)) {
				continue;
			}
			for (int i = 0; i < summary.size(); i++) {
				if (i == summary.size() - 1) {
					summary.get(i).setAmount(summary.get(i).getAmount() + 1);
				} else if (summary.get(i).getDate().before(commitDate)
						&& commitDate.before(summary.get(i + 1).getDate())) {
					summary.get(i).setAmount(summary.get(i).getAmount() + 1);
					break;
				}
			}

		}
	}

	private File getTenantBaseDir() {
		return new File(basePath, TenancyUtil.getCurrentTenantProjectIdentifer());
	}

	private File getTenantHostedBaseDir() {
		return new File(getTenantBaseDir(), GitConstants.HOSTED_GIT_DIR);
	}

	private File getTenantMirroredBaseDir() {
		return new File(getTenantBaseDir(), GitConstants.MIRRORED_GIT_DIR);
	}

	private List<RevCommit> getAllCommits(Repository repository) {
		List<RevCommit> result = new ArrayList<RevCommit>();

		try {
			RevWalk revWal = new RevWalk(repository);
			Set<ObjectId> objectsSeen = new HashSet<ObjectId>();

			Map<String, Ref> refs = repository.getAllRefs();
			for (Entry<String, Ref> entry : refs.entrySet()) {
				if (entry.getValue().getName().startsWith(Constants.R_HEADS)) {
					revWal.markStart(revWal.parseCommit(entry.getValue().getObjectId()));
				}
			}
			for (RevCommit revCommit : revWal) {
				if (objectsSeen.contains(revCommit.getId())) {
					continue;
				}
				result.add(revCommit);

			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	private List<Repository> getAllRepositories() {
		List<Repository> result = new ArrayList<Repository>();

		File hostedDir = getTenantHostedBaseDir();
		if (hostedDir.exists()) {
			for (File repoDir : hostedDir.listFiles()) {
				try {
					result.add(getHostedRepository(repoDir.getName()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		File mirroredDir = getTenantMirroredBaseDir();
		if (mirroredDir.exists()) {
			for (File repoDir : mirroredDir.listFiles()) {
				try {
					result.add(getMirroredRepository(repoDir.getName()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return result;
	}

	private Repository getHostedRepository(String name) throws IOException {
		File repoDir = new File(getTenantHostedBaseDir(), name);
		FileKey key = FileKey.exact(repoDir, FS.DETECTED);
		Repository repo = RepositoryCache.open(key, true);
		return repo;
	}

	private Repository getMirroredRepository(String name) throws IOException {
		return new FileRepository(new File(getTenantMirroredBaseDir(), name));
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	// FIXME region is not really respected but...
	public List<Commit> getLog(Region region) {
		List<Commit> result = new ArrayList<Commit>();

		for (Repository repo : getAllRepositories()) {
			result.addAll(getLog(repo, region));
			repo.close();
		}

		Collections.sort(result, new Comparator<Commit>() {

			@Override
			public int compare(Commit o1, Commit o2) {
				return o2.getDate().compareTo(o1.getDate());
			}
		});

		QueryUtil.applyRegionToList(result, region);

		return result;
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public List<Commit> getLog(String repoName, Region region) throws EntityNotFoundException {
		try {
			Repository repo;
			repo = findRepositoryByName(repoName);
			List<Commit> result = getLog(repo, region);
			repo.close();

			Collections.sort(result, new Comparator<Commit>() {

				@Override
				public int compare(Commit o1, Commit o2) {
					return o2.getDate().compareTo(o1.getDate());
				}
			});

			QueryUtil.applyRegionToList(result, region);

			return result;

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public List<Commit> getLogForBranch(String repoName, String branchName, Region region)
			throws EntityNotFoundException {
		if (region == null) {
			region = new Region(0, 100);
		}
		try {
			Repository repo;
			repo = findRepositoryByName(repoName);

			RevWalk revWal = new RevWalk(repo);

			Ref branchRef = repo.getRef(branchName);
			if (branchRef == null) {
				throw new EntityNotFoundException();
			}
			revWal.markStart(revWal.parseCommit(branchRef.getObjectId()));

			Iterator<RevCommit> iterator = revWal.iterator();

			int current = 0;
			// Skip the first
			while (current < region.getOffset() && iterator.hasNext()) {
				current++;
				iterator.next();
			}
			int page = 0;

			List<Commit> result = new ArrayList<Commit>();
			while (iterator.hasNext() && page < region.getSize()) {
				RevCommit commit = iterator.next();
				result.add(createCommit(commit));
				page++;
			}

			return result;

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Repository findRepositoryByName(String repositoryName) throws IOException, URISyntaxException,
			EntityNotFoundException {
		for (ScmRepository repo : getHostedRepositories()) {
			if (repo.getName().equals(repositoryName)) {
				return getHostedRepository(repositoryName);
			}
		}
		// FIXME potential that an external repo is shadowned by the internal one
		for (ScmRepository repo : getExternalRepositories()) {
			if (repo.getName().equals(repositoryName)) {
				return getMirroredRepository(repositoryName);
			}
		}
		throw new EntityNotFoundException();
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public List<ScmSummary> getScmSummary(int numDays) {
		List<ScmSummary> result = createEmptySummaries(numDays);

		for (Repository repo : getAllRepositories()) {
			addCommitsToSummary(repo, result);
			repo.close();
		}
		return result;
	}

	@Secured({ Role.Admin })
	@Override
	public void createEmptyRepository(String name) {
		File hostedDir = getTenantHostedBaseDir();
		File gitDir = new File(hostedDir, name);
		gitDir.mkdirs();
		try {
			FileRepository repo = new FileRepository(gitDir);
			repo.create();
			repo.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ScmRepository> listRepositories() {
		List<ScmRepository> result = new ArrayList<ScmRepository>();
		try {
			result.addAll(getHostedRepositories());
			result.addAll(getExternalRepositories());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}

	private List<ScmRepository> getHostedRepositories() throws IOException {
		List<ScmRepository> result = new ArrayList<ScmRepository>();

		File hostedDir = getTenantHostedBaseDir();
		if (hostedDir != null && hostedDir.exists()) {

			for (String repoName : hostedDir.list()) {
				ScmRepository repo = new ScmRepository();
				repo.setName(repoName);
				repo.setScmLocation(ScmLocation.CODE2CLOUD);
				repo.setType(ScmType.GIT);
				repo.setUrl(profileServiceConfiguration.getHostedScmUrlPrefix(TenancyUtil
						.getCurrentTenantProjectIdentifer()) + repo.getName()); // FIXME
				repo.setAlternateUrl(computeSshUrl(repo));
				result.add(repo);

				Git git = Git.open(new File(hostedDir, repoName));

				setBranches(repo, git);
			}
		}

		return result;
	}

	/**
	 * @param repo
	 * @param git
	 */
	private void setBranches(ScmRepository repo, Git git) {
		repo.setBranches(new ArrayList<String>());
		for (Ref ref : git.branchList().call()) {
			String refName = ref.getName();
			if (refName.startsWith(Constants.R_HEADS)) {
				refName = refName.substring(Constants.R_HEADS.length());
			}
			repo.getBranches().add(refName);
		}
	}

	private String computeSshUrl(ScmRepository repo) {
		String sshUriPath = getSshUriPath(repo);
		int sshPort = profileServiceConfiguration.getPublicSshPort();
		String webHost = profileServiceConfiguration.getWebHost();
		if (sshUriPath != null) {
			String url = "ssh://" + webHost;
			if (sshPort != 22) {
				url += ":" + sshPort;
			}
			url += sshUriPath;
			return url;
		}
		return null;
	}

	/**
	 * Get an SSH URI path for this SCM repository for repositories that are hosted at Code2Cloud. The URI path is the
	 * portion that follows the hostname and port in the URL.
	 * 
	 * @return the URI path, or null if it has none
	 */
	private String getSshUriPath(ScmRepository repo) {
		String url = repo.getUrl();
		if (repo.getType() == ScmType.GIT && repo.getScmLocation() == ScmLocation.CODE2CLOUD && url != null) {
			int lastIndex = url.lastIndexOf('/');
			if (lastIndex != -1 && lastIndex < url.length() - 1) {
				return '/' + TenancyUtil.getCurrentTenantProjectIdentifer() + '/' + url.substring(lastIndex + 1);
			}
		}
		return null;
	}

	private List<ScmRepository> getExternalRepositories() throws IOException, URISyntaxException {
		List<ScmRepository> result = new ArrayList<ScmRepository>();

		File hostedDir = getTenantMirroredBaseDir();
		if (hostedDir != null && hostedDir.exists()) {
			for (String repoName : hostedDir.list()) {
				ScmRepository repo = new ScmRepository();
				repo.setName(repoName);
				repo.setScmLocation(ScmLocation.EXTERNAL);
				repo.setType(ScmType.GIT);

				Git git = Git.open(new File(hostedDir, repoName));
				RemoteConfig config = new RemoteConfig(git.getRepository().getConfig(), Constants.DEFAULT_REMOTE_NAME);
				repo.setUrl(config.getURIs().get(0).toString());

				setBranches(repo, git);

				result.add(repo);
			}
		}

		return result;
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public Map<Profile, Integer> getNumCommitsByAuthor(int numDays) {
		Map<Profile, Integer> result = new HashMap<Profile, Integer>();
		Map<String, Profile> profilesByName = new HashMap<String, Profile>();
		for (Repository repo : getAllRepositories()) {
			addCommitsToCommitsByAuthor(result, profilesByName, repo, numDays);
			repo.close();
		}

		return result;
	}

	private void addCommitsToCommitsByAuthor(Map<Profile, Integer> commitsByAuthor,
			Map<String, Profile> profilesByName, Repository repository, int numDays) {

		Date firstDay = new Date(System.currentTimeMillis() - MILLISECONDS_PER_DAY * numDays);

		for (RevCommit c : getAllCommits(repository)) {
			if (c.getAuthorIdent().getWhen().before(firstDay)) {
				continue;
			}

			Profile p = profilesByName.get(c.getAuthorIdent().getName());
			if (p == null) {
				p = fromPersonIdent(c.getAuthorIdent());
				p.setId((long) profilesByName.size()); // wrong id but need for eq/hash
				profilesByName.put(c.getAuthorIdent().getName(), p);
			}

			Integer count = commitsByAuthor.get(p);
			if (count == null) {
				count = 0;
			}
			count++;
			commitsByAuthor.put(p, count);

		}
	}

	static String getRepoDirNameFromExternalUrl(String url) {
		String path;
		try {
			path = new URI(url).getPath();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		int i = path.lastIndexOf("/");
		if (i == -1) {
			return path;
		}
		return path.substring(i + 1);
	}

	@Secured({ Role.Admin })
	@Override
	public void addExternalRepository(String url) {
		try {
			String repoName = getRepoDirNameFromExternalUrl(url);
			File dir = new File(getTenantMirroredBaseDir(), repoName);
			Git git = Git.init().setBare(true).setDirectory(dir).call();
			RemoteConfig config = new RemoteConfig(git.getRepository().getConfig(), Constants.DEFAULT_REMOTE_NAME);
			config.addURI(new URIish(url));
			config.update(git.getRepository().getConfig());
			git.getRepository().getConfig().save();
		} catch (JGitInternalException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Secured({ Role.Admin })
	@Override
	public void removeExternalRepository(String url) {
		String repoName = getRepoDirNameFromExternalUrl(url);
		File dir = new File(getTenantMirroredBaseDir(), repoName);
		try {
			FileUtils.delete(dir, FileUtils.RECURSIVE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (startThreads) {
			new FetchWorkerThread(this).start();
		}
	}

	@Override
	public void removeInternalRepository(String name) {
		File hostedDir = getTenantHostedBaseDir();
		File dir = new File(hostedDir, name);
		try {
			FileUtils.delete(dir, FileUtils.RECURSIVE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Commit getCommitWithDiff(String repositoryName, String commitId) throws EntityNotFoundException {
		try {
			Repository jgitRepo = findRepositoryByName(repositoryName);
			return getCommitInternal(repositoryName, jgitRepo, commitId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Commit getCommitInternal(String repositoryName, Repository repo, String commitId) throws IOException,
			GitAPIException, EntityNotFoundException {

		ObjectId thisC = repo.resolve(commitId);

		if (thisC == null) {
			throw new EntityNotFoundException();
		}

		RevWalk walk = new RevWalk(repo);
		RevCommit theCommit = walk.parseCommit(thisC);

		Commit result = createCommit(theCommit);
		result.setRepository(repositoryName);

		// FIXME how to handle merges? there can be real diffs there
		// FIXME line count not working
		if (theCommit.getParentCount() == 1) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			DiffFormatter diffFormatter = new DiffFormatter(output);
			diffFormatter.setRepository(repo);
			diffFormatter.setPathFilter(TreeFilter.ALL);

			RevTree tree = walk.parseTree(thisC);
			CanonicalTreeParser thisTreeParser = new CanonicalTreeParser();
			thisTreeParser.reset(repo.newObjectReader(), tree);

			ObjectId parentC = theCommit.getParent(0);
			RevTree parentTree = walk.parseTree(parentC);
			CanonicalTreeParser parentTreeParser = new CanonicalTreeParser();
			parentTreeParser.reset(repo.newObjectReader(), parentTree);

			List<DiffEntry> diffEntries = diffFormatter.scan(parentTreeParser, thisTreeParser);
			diffFormatter.format(diffEntries);
			diffFormatter.flush();

			result.setChanges(new ArrayList<com.tasktop.c2c.server.scm.domain.DiffEntry>());
			for (DiffEntry de : diffEntries) {
				com.tasktop.c2c.server.scm.domain.DiffEntry publicDiffEntry = copy(de);
				result.getChanges().add(publicDiffEntry);
				FileHeader header = diffFormatter.toFileHeader(de);
				int linesAdded = 0;
				int linesRemoved = 0;
				for (HunkHeader edit : header.getHunks()) {
					linesAdded += edit.getOldImage().getLinesAdded();
					linesRemoved += edit.getOldImage().getLinesDeleted();
				}
				publicDiffEntry.setLinesAdded(linesAdded);
				publicDiffEntry.setLinesRemoved(linesRemoved);
			}

			String theDiff = new String(output.toByteArray());
			result.setDiffText(theDiff);
		}

		return result;
	}

	private com.tasktop.c2c.server.scm.domain.DiffEntry copy(DiffEntry target) {
		com.tasktop.c2c.server.scm.domain.DiffEntry result = new com.tasktop.c2c.server.scm.domain.DiffEntry();
		switch (target.getChangeType()) {
		case ADD:
			result.setChangeType(ChangeType.ADD);
			break;
		case COPY:
			result.setChangeType(ChangeType.COPY);
			break;
		case DELETE:
			result.setChangeType(ChangeType.DELETE);
			break;
		case MODIFY:
			result.setChangeType(ChangeType.MODIFY);
			break;
		case RENAME:
			result.setChangeType(ChangeType.RENAME);
			break;
		}
		result.setNewPath(target.getNewPath());
		result.setOldPath(target.getOldPath());
		return result;
	}
}
