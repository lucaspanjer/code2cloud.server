/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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
import java.util.TreeSet;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.query.QueryUtil;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.scm.domain.Blame;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.domain.ScmType;
import com.tasktop.c2c.server.scm.domain.Trees;

@Component
public class GitServiceBean implements GitService, InitializingBean {

	@Autowired
	private ScmServiceConfiguration profileServiceConfiguration;

	@Autowired
	private JgitRepositoryProvider repositoryProvider;

	@Value("${git.startThreads}")
	private boolean startThreads = true;

	private List<Commit> getLog(Repository repository, Region region, Set<ObjectId> visited) {
		List<Commit> result = new ArrayList<Commit>();

		for (RevCommit revCommit : getAllCommits(repository, region, visited)) {
			Commit commit = GitDomain.createCommit(revCommit);
			commit.setRepository(repository.getDirectory().getName());
			result.add(commit);
		}

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

	public interface CommitVisitor {
		void visit(RevCommit commit);
	}

	private void visitAllCommitsAfter(Date maxDate, CommitVisitor visitor) {
		Set<ObjectId> visited = new java.util.HashSet<ObjectId>();

		for (Repository repo : repositoryProvider.getAllRepositories()) {
			visitAllCommitsAfter(repo, maxDate, visitor, visited);
			repo.close();
		}
	}

	private void visitAllCommitsAfter(Repository repository, Date maxDate, CommitVisitor visitor, Set<ObjectId> visited) {
		try {

			for (Entry<String, Ref> entry : repository.getAllRefs().entrySet()) {
				if (entry.getValue().getName().startsWith(Constants.R_HEADS)) {
					RevWalk revWal = new RevWalk(repository);
					revWal.markStart(revWal.parseCommit(entry.getValue().getObjectId()));

					int commitsPastDate = 0;
					for (RevCommit revCommit : revWal) {
						if (revCommit.getCommitterIdent().getWhen().getTime() < maxDate.getTime()) {
							commitsPastDate++;
						} else {
							commitsPastDate = 0;
							if (visited.add(revCommit)) {
								visitor.visit(revCommit);
							} else {
								break;
							}
						}
						if (commitsPastDate > 5) {
							break;
						}

					}
					revWal.dispose();

				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private List<RevCommit> getAllCommits(Repository repository, Region region, Set<ObjectId> visited) {
		TreeSet<RevCommit> result = new TreeSet<RevCommit>(new Comparator<RevCommit>() {

			@Override
			public int compare(RevCommit o1, RevCommit o2) {
				int ctDiff = o2.getCommitTime() - o1.getCommitTime();
				if (ctDiff != 0) {
					return ctDiff;
				}
				return o1.getId().compareTo(o2.getId());
			}
		});

		int maxResultsToConsider = -1;
		if (region != null) {
			maxResultsToConsider = region.getOffset() + region.getSize();
		}
		long minTime = -1;

		try {

			for (Ref ref : getRefsToAdd(repository)) {
				RevWalk revWal = new RevWalk(repository);
				revWal.markStart(revWal.parseCommit(ref.getObjectId()));

				int index = 0;
				for (RevCommit revCommit : revWal) {
					if (region == null
							|| (index >= region.getOffset() && index < region.getOffset() + region.getSize())) {
						if (minTime > 0 && revCommit.getCommitTime() < minTime) {
							break;
						}
						if (visited.add(revCommit.getId())) {
							result.add(revCommit);

							if (maxResultsToConsider > 0 && result.size() > maxResultsToConsider) {
								RevCommit last = result.last();
								result.remove(last);
								minTime = last.getCommitTime();
							}
						} else {
							break; // Done with this branch
						}
					}
					index++;
					if (region != null && (index >= region.getOffset() + region.getSize())) {
						break;
					}

				}

			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new ArrayList<RevCommit>(result);
	}

	private List<Ref> getRefsToAdd(Repository repository) {
		List<Ref> result = new ArrayList<Ref>();
		Ref master = null;
		for (Entry<String, Ref> entry : repository.getAllRefs().entrySet()) {
			if (entry.getValue().getName().equals(Constants.R_HEADS + Constants.MASTER)) {
				master = entry.getValue();
			} else if (entry.getValue().getName().startsWith(Constants.R_HEADS)) {
				result.add(entry.getValue());
			}
		}
		if (master != null) {
			result.add(0, master);
		}
		return result;
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public List<Commit> getLog(Region region) {
		List<Commit> result = new ArrayList<Commit>();
		Set<ObjectId> visited = new HashSet<ObjectId>();

		for (Repository repo : repositoryProvider.getAllRepositories()) {
			result.addAll(getLog(repo, region, visited));
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
	public List<Commit> getLog(String repoName, String revision, String path, Region region)
			throws EntityNotFoundException {
		try {
			Repository r = findRepositoryByName(repoName);
			List<Commit> commits = GitBrowseUtil.getLog(r, revision, path, region);
			r.close();
			return commits;
		} catch (AmbiguousObjectException ex) {
			throw new EntityNotFoundException();
		} catch (IOException ex) {
			throw new EntityNotFoundException();
		} catch (URISyntaxException ex) {
			throw new EntityNotFoundException();
		}

	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public List<Commit> getLog(String repoName, Region region) throws EntityNotFoundException {
		try {
			Repository repo;
			repo = findRepositoryByName(repoName);
			Set<ObjectId> visited = new HashSet<ObjectId>();

			List<Commit> result = getLog(repo, region, visited);
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
				result.add(GitDomain.createCommit(commit));
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
				return repositoryProvider.getHostedRepository(repositoryName);
			}
		}
		// FIXME potential that an external repo is shadowned by the internal one
		for (ScmRepository repo : getExternalRepositories()) {
			if (repo.getName().equals(repositoryName)) {
				return repositoryProvider.getMirroredRepository(repositoryName);
			}
		}
		throw new EntityNotFoundException();
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public List<ScmSummary> getScmSummary(int numDays) {
		final List<ScmSummary> summary = createEmptySummaries(numDays);

		final Date firstDate = summary.get(0).getDate();

		CommitVisitor visitor = new CommitVisitor() {

			@Override
			public void visit(RevCommit revCommit) {
				Date commitDate = revCommit.getAuthorIdent().getWhen();
				if (commitDate.before(firstDate)) {
					return;
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
		};

		visitAllCommitsAfter(firstDate, visitor);
		return summary;
	}

	@Secured({ Role.Admin })
	@Override
	public void createEmptyRepository(String name) {
		File hostedDir = repositoryProvider.getTenantHostedBaseDir();
		File gitDir = new File(hostedDir, name);
		gitDir.mkdirs();
		try {
			FileRepository repo = new FileRepository(gitDir);
			repo.create();
			StoredConfig config = repo.getConfig();
			config.setBoolean("receive", null, "denynonfastforwards", true);
			config.save();
			repo.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Secured({ Role.Observer, Role.User })
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
		Collections.sort(result, new Comparator<ScmRepository>() {

			@Override
			public int compare(ScmRepository repo1, ScmRepository repo2) {
				return repo1.getName().compareToIgnoreCase(repo2.getName());
			}
		});
		return result;
	}

	private List<ScmRepository> getHostedRepositories() throws IOException {
		List<ScmRepository> result = new ArrayList<ScmRepository>();

		File hostedDir = repositoryProvider.getTenantHostedBaseDir();
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

		File hostedDir = repositoryProvider.getTenantMirroredBaseDir();
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
		final Map<Profile, Integer> result = new HashMap<Profile, Integer>();
		final Map<String, Profile> profilesByName = new HashMap<String, Profile>();

		Date firstDay = new Date(System.currentTimeMillis() - MILLISECONDS_PER_DAY * numDays);

		CommitVisitor visitor = new CommitVisitor() {

			@Override
			public void visit(RevCommit c) {
				Profile p = profilesByName.get(c.getAuthorIdent().getName());
				if (p == null) {
					p = GitDomain.fromPersonIdent(c.getAuthorIdent());
					p.setId((long) profilesByName.size()); // wrong id but need for eq/hash
					profilesByName.put(c.getAuthorIdent().getName(), p);
				}

				Integer count = result.get(p);
				if (count == null) {
					count = 0;
				}
				count++;
				result.put(p, count);

			}
		};

		visitAllCommitsAfter(firstDay, visitor);

		return result;
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
			File dir = new File(repositoryProvider.getTenantMirroredBaseDir(), repoName);
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
		File dir = new File(repositoryProvider.getTenantMirroredBaseDir(), repoName);
		try {
			FileUtils.delete(dir, FileUtils.RECURSIVE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (startThreads) {
			new FetchWorkerThread(this.repositoryProvider).start();
		}
	}

	@Secured({ Role.Admin })
	@Override
	public void removeInternalRepository(String name) {
		File hostedDir = repositoryProvider.getTenantHostedBaseDir();
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

		Commit result = GitDomain.createCommit(theCommit);
		result.setUrl(profileServiceConfiguration.getWebUrlForCommit(TenancyUtil.getCurrentTenantProjectIdentifer(),
				result));
		result.setRepository(repositoryName);

		// FIXME how to handle merges? there can be real diffs there
		if (theCommit.getParentCount() <= 1) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			DiffFormatter diffFormatter = new DiffFormatter(output);
			diffFormatter.setRepository(repo);
			diffFormatter.setPathFilter(TreeFilter.ALL);

			RevTree tree = walk.parseTree(thisC);
			CanonicalTreeParser thisTreeParser = new CanonicalTreeParser();
			thisTreeParser.reset(repo.newObjectReader(), tree);

			CanonicalTreeParser parentTreeParser = new CanonicalTreeParser();

			if (theCommit.getParentCount() == 1) {
				ObjectId parentC = theCommit.getParent(0);
				RevTree parentTree = walk.parseTree(parentC);
				parentTreeParser.reset(repo.newObjectReader(), parentTree);
			}

			diffFormatter.setDetectRenames(true);
			List<DiffEntry> diffEntries = diffFormatter.scan(parentTreeParser, thisTreeParser);
			diffFormatter.format(diffEntries);
			diffFormatter.flush();

			result.setChanges(new ArrayList<com.tasktop.c2c.server.scm.domain.DiffEntry>());
			String theDiff = new String(output.toByteArray());
			result.setDiffText(theDiff);
			result.setChanges(new PatchParser().parsePatch(theDiff));
		}

		return result;
	}

	@Secured({ Role.User })
	@Override
	public String createBranch(String repoName, String branchName) throws EntityNotFoundException {
		try {
			Repository r = findRepositoryByName(repoName);
			Git git = Git.wrap(r);
			CreateBranchCommand command = git.branchCreate();
			command.setName(branchName);

			/*
			 * command.setStartPoint(getStartPoint().name()); if (upstreamMode != null)
			 * command.setUpstreamMode(upstreamMode); command.call();
			 */

			command.call();
			r.close();
			return branchName;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Secured({ Role.User })
	@Override
	public void deleteBranch(String repoName, String branchName) throws EntityNotFoundException {
		try {
			Repository r = findRepositoryByName(repoName);
			Git git = Git.wrap(r);
			DeleteBranchCommand command = git.branchDelete();
			command.setBranchNames(branchName);
			command.setForce(true);
			command.call();
			r.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public Trees getTrees(String repoName, String revision, String path, boolean history, int recursion)
			throws EntityNotFoundException {
		try {
			Repository r = findRepositoryByName(repoName);
			Trees trees = GitBrowseUtil.getTrees(r, revision, path, history, recursion);
			r.close();
			return trees;
		} catch (AmbiguousObjectException ex) {
			throw new EntityNotFoundException();
		} catch (IOException ex) {
			throw new EntityNotFoundException();
		} catch (URISyntaxException ex) {
			throw new EntityNotFoundException();
		} catch (GitAPIException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public Blob getBlob(String repoName, String revision, String path) throws EntityNotFoundException {
		try {
			Repository r = findRepositoryByName(repoName);
			Blob b = GitBrowseUtil.getBlob(r, revision, path);
			r.close();
			return b;
		} catch (AmbiguousObjectException ex) {
			throw new EntityNotFoundException();
		} catch (IOException ex) {
			throw new EntityNotFoundException();
		} catch (URISyntaxException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public Blame getBlame(String repoName, String revision, String path) throws EntityNotFoundException {
		try {
			Repository r = findRepositoryByName(repoName);
			Blame b = GitBrowseUtil.getBlame(r, revision, path);
			r.close();
			return b;
		} catch (AmbiguousObjectException ex) {
			throw new EntityNotFoundException();
		} catch (IOException ex) {
			throw new EntityNotFoundException();
		} catch (URISyntaxException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Secured({ Role.Observer, Role.User })
	@Override
	public Item getItem(String repoName, String revision, String path) throws EntityNotFoundException {
		try {
			Repository r = findRepositoryByName(repoName);
			Item i = GitBrowseUtil.getItem(r, revision, path);
			r.close();
			return i;
		} catch (AmbiguousObjectException ex) {
			throw new EntityNotFoundException();
		} catch (IOException ex) {
			throw new EntityNotFoundException();
		} catch (URISyntaxException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Override
	public String getPublicSshKey() {
		File file = new File(repositoryProvider.getTenantBaseDir(), ".ssh/id_rsa.pub");
		if (!file.exists()) {
			return "";
		}

		try {
			return org.apache.commons.io.FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
