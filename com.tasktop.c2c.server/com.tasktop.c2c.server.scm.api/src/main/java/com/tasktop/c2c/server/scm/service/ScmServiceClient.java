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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.WrappedCheckedException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.web.AbstractVersionedRestServiceClient;
import com.tasktop.c2c.server.scm.domain.Blame;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.domain.Trees;

/**
 * A client to communicate to the SCM REST service.
 */
@Service
public class ScmServiceClient extends AbstractVersionedRestServiceClient implements ScmService {

	/**
	 * Essentially a map entry, but spring does not like map return results that are meant to be response data.
	 * 
	 */
	public static final class CommitsForAuthor {
		private Profile author;
		private Integer count;

		public void setAuthor(Profile author) {
			this.author = author;
		}

		public Profile getAuthor() {
			return author;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Integer getCount() {
			return count;
		}
	}

	@SuppressWarnings("unused")
	// All of the setters in this method are used programmatically by the JSON serializer.
	private static class ServiceCallResult {
		private List<Commit> commitList;
		private List<CommitsForAuthor> commitsForAuthorList;
		private ScmRepository scmRepository;
		private List<ScmRepository> scmRepositoryList;
		private List<ScmSummary> scmSummaryList;
		private Commit commit;
		private String string;
		private Trees trees;
		private Blob blob;
		private Blame blame;
		private Item item;

		public List<Commit> getCommitList() {
			return commitList;
		}

		public void setCommitList(List<Commit> commitList) {
			this.commitList = commitList;
		}

		public List<CommitsForAuthor> getCommitsForAuthorList() {
			return commitsForAuthorList;
		}

		public void setCommitsForAuthorList(List<CommitsForAuthor> commitsForAuthorList) {
			this.commitsForAuthorList = commitsForAuthorList;
		}

		public ScmRepository getScmRepository() {
			return scmRepository;
		}

		public void setScmRepository(ScmRepository scmRepo) {
			this.scmRepository = scmRepo;
		}

		public List<ScmRepository> getScmRepositoryList() {
			return scmRepositoryList;
		}

		public void setScmRepositoryList(List<ScmRepository> scmRepoSet) {
			this.scmRepositoryList = scmRepoSet;
		}

		public List<ScmSummary> getScmSummaryList() {
			return scmSummaryList;
		}

		public void setScmSummaryList(List<ScmSummary> scmSummaryList) {
			this.scmSummaryList = scmSummaryList;
		}

		public Commit getCommit() {
			return commit;
		}

		public void setCommit(Commit commit) {
			this.commit = commit;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

		public Trees getTrees() {
			return trees;
		}

		public void setTrees(Trees trees) {
			this.trees = trees;
		}

		public Blob getBlob() {
			return blob;
		}

		public void setBlob(Blob blob) {
			this.blob = blob;
		}

		public Blame getBlame() {
			return blame;
		}

		public void setBlame(Blame blame) {
			this.blame = blame;
		}

		public Item getItem() {
			return item;
		}

		public void setItem(Item item) {
			this.item = item;
		}

	}

	private abstract class GetCall<T> {

		public abstract T getValue(ServiceCallResult result);

		public T doCall(String urlStub, Object... variables) {
			ServiceCallResult callResult = template.getForObject(computeUrl(urlStub), ServiceCallResult.class,
					variables);

			T retVal = getValue(callResult);

			if (retVal == null) {
				throw new IllegalStateException("Illegal result from call to scmService");
			}

			return retVal;
		}
	}

	private abstract class PostCall<T> {

		public abstract T getValue(ServiceCallResult result);

		public T doCall(String urlStub, Object objToPost, Object... variables) {
			ServiceCallResult callResult = template.postForObject(computeUrl(urlStub), objToPost,
					ServiceCallResult.class, variables);

			T retVal = getValue(callResult);

			if (retVal == null) {
				throw new IllegalStateException("Illegal result from call to scmService");
			}

			return retVal;
		}
	}

	public static final String GET_SCM_REPOSITORIES_URL = "repository";

	public List<ScmRepository> getScmRepositories() throws EntityNotFoundException {

		try {
			return new GetCall<List<ScmRepository>>() {
				public List<ScmRepository> getValue(ServiceCallResult result) {
					return result.getScmRepositoryList();
				}
			}.doCall(GET_SCM_REPOSITORIES_URL);
		} catch (WrappedCheckedException e) {
			convertEntityNotFoundException(e);
			throw e;
		}
	}

	public static final String CREATE_SCM_REPOSITORY_URL = "repository";

	public ScmRepository createScmRepository(ScmRepository scmRepository) throws EntityNotFoundException,
			ValidationException {
		try {
			return new PostCall<ScmRepository>() {
				public ScmRepository getValue(ServiceCallResult result) {
					return result.getScmRepository();
				}
			}.doCall(CREATE_SCM_REPOSITORY_URL, scmRepository);
		} catch (WrappedCheckedException e) {
			convertEntityNotFoundException(e);
			convertValidationException(e);
			throw e;
		}
	}

	public static final String REPOSITORY_ID_URLPARAM = "repositoryId";

	public static final String DELETE_SCM_REPOSITORY_URL = "repository/delete";

	public void deleteScmRepository(ScmRepository repo) throws EntityNotFoundException {
		try {
			new PostCall<Object>() {

				@Override
				public Object getValue(ServiceCallResult result) {
					return "empty";
				}

			}.doCall(DELETE_SCM_REPOSITORY_URL, repo);
		} catch (WrappedCheckedException e) {
			convertEntityNotFoundException(e);
			throw e;
		}
	}

	public static final String OFFSET_URL_PARAM = "offset";
	public static final String PAGESIZE_URL_PARAM = "pageSize";
	public static final String NUMDAYS_URL_PARAM = "numDays";

	public static final String GET_LOG_URL = "log";

	public List<Commit> getLog(Region region) {

		// Calculate the correct URL now.
		String url = GET_LOG_URL;
		if (region != null) {
			url = String.format("%s?%s=%s&%s=%s", url, OFFSET_URL_PARAM, region.getOffset(), PAGESIZE_URL_PARAM,
					region.getSize());
		}

		return new GetCall<List<Commit>>() {
			public List<Commit> getValue(ServiceCallResult result) {
				return result.getCommitList();
			}
		}.doCall(url);
	}

	public static final String GET_NUM_COMMITS_BY_AUTHOR_URL = "commitsByAuthor";

	public Map<Profile, Integer> getNumCommitsByAuthor(int numDays) {
		String url = String.format("%s?%s=%s", GET_NUM_COMMITS_BY_AUTHOR_URL, NUMDAYS_URL_PARAM, numDays);

		return toMap(new GetCall<List<CommitsForAuthor>>() {
			public List<CommitsForAuthor> getValue(ServiceCallResult result) {
				return result.getCommitsForAuthorList();
			}
		}.doCall(url));
	}

	private Map<Profile, Integer> toMap(List<CommitsForAuthor> commitsForAuthor) {
		Map<Profile, Integer> result = new HashMap<Profile, Integer>();
		for (CommitsForAuthor c : commitsForAuthor) {
			result.put(c.getAuthor(), c.getCount());
		}
		return result;
	}

	public static final String GET_SCM_SUMMARY_URL = "summary";

	public List<ScmSummary> getScmSummary(int numDays) {
		String url = String.format("%s?%s=%s", GET_SCM_SUMMARY_URL, NUMDAYS_URL_PARAM, numDays);

		return new GetCall<List<ScmSummary>>() {
			@Override
			public List<ScmSummary> getValue(ServiceCallResult result) {
				return result.getScmSummaryList();
			}
		}.doCall(url);
	}

	public static final String GET_COMMIT_URL = "commit/{repoName}/{commitId}";

	public Commit getCommit(String repoName, String commitId, Integer context) throws EntityNotFoundException {

		try {
			return new GetCall<Commit>() {
				@Override
				public Commit getValue(ServiceCallResult result) {
					return result.getCommit();
				}
			}.doCall(GET_COMMIT_URL, repoName, commitId);
		} catch (WrappedCheckedException e) {
			convertEntityNotFoundException(e);
		}
		throw new IllegalStateException();
	}

	public static final String GET_LOG_FOR_REPO_URL = "repository/{repo}/repolog";

	public List<Commit> getLog(String repoName, Region region) {
		// Calculate the correct URL now.
		String url = GET_LOG_FOR_REPO_URL;
		if (region != null) {
			url = String.format("%s?%s=%s&%s=%s", url, OFFSET_URL_PARAM, region.getOffset(), PAGESIZE_URL_PARAM,
					region.getSize());
		}

		return new GetCall<List<Commit>>() {
			public List<Commit> getValue(ServiceCallResult result) {
				return result.getCommitList();
			}
		}.doCall(url, repoName);
	}

	public static final String GET_LOG_FOR_PATH_URL = "repository/{repo}/commits/{revision}";

	public List<Commit> getLog(String repoName, String revision, String path, Region region)
			throws EntityNotFoundException {
		// Calculate the correct URL now.
		String url = GET_LOG_FOR_PATH_URL;
		if (region != null) {
			url = String.format("%s/%s?%s=%s&%s=%s", url, path, OFFSET_URL_PARAM, region.getOffset(),
					PAGESIZE_URL_PARAM, region.getSize());
		}

		return new GetCall<List<Commit>>() {
			public List<Commit> getValue(ServiceCallResult result) {
				return result.getCommitList();
			}
		}.doCall(url, repoName, revision);
	}

	public static final String BRANCH_PARAM = "branch";
	public static final String GET_LOG_FOR_BRANCH_URL = "repository/{repo}/branchlog";

	public List<Commit> getLogForBranch(String repoName, String branchName, Region region) {
		// Calculate the correct URL now.
		String url = GET_LOG_FOR_BRANCH_URL;
		if (region != null) {
			url = String.format("%s?%s=%s&%s=%s&%s=%s", url, BRANCH_PARAM, branchName, OFFSET_URL_PARAM,
					region.getOffset(), PAGESIZE_URL_PARAM, region.getSize());
		}

		return new GetCall<List<Commit>>() {
			public List<Commit> getValue(ServiceCallResult result) {
				return result.getCommitList();
			}
		}.doCall(url, repoName);
	}

	public static final String CREATE_BRANCH_URL = "branch/{repoName}/{branchName}";

	/** @Override in fact it does override, but ... */
	public String createBranch(String repoName, String branchName) throws EntityNotFoundException {
		try {
			return new PostCall<String>() {
				public String getValue(ServiceCallResult result) {
					return result.getString();
				}
			}.doCall(CREATE_BRANCH_URL, repoName, branchName);
		} catch (WrappedCheckedException e) {
			convertEntityNotFoundException(e);
			// convertValidationException(e);
			throw e;
		}
	}

	public static final String DELETE_BRANCH_URL = "unbranch/{repoName}/{branchName}";

	/** @Override in fact it does override, but ... */
	public void deleteBranch(String repoName, String branchName) throws EntityNotFoundException {
		try {
			new PostCall<Void>() {
				public Void getValue(ServiceCallResult result) {
					return null;
				}
			}.doCall(DELETE_BRANCH_URL, repoName, branchName);
		} catch (WrappedCheckedException e) {
			convertEntityNotFoundException(e);
			// convertValidationException(e);
			throw e;
		}
	}

	public static final String PUBILC_SSH_KEY_URL = "sshkey";

	public String getPublicSshKey() {
		return new GetCall<String>() {
			public String getValue(ServiceCallResult result) {
				return result.getString();
			}
		}.doCall(PUBILC_SSH_KEY_URL);
	}

	public String getClientVersion() {
		return ScmService.VERSION;
	}

	public static final String REVISION_PARAM = "revision";

	/*
	 * This does not work. sadly public static final String GET_TREES_URL =
	 * "repository/{repo}/trees/{revision}/{filePath:[.*]/*}";
	 */
	public static final String GET_TREES_URL = "repository/{repo}/trees/{revision}";

	/** @Override in fact it does override, but ... */
	public Trees getTrees(String repo, String revision, String path, boolean history, int recursion)
			throws EntityNotFoundException {
		// Calculate the correct URL now.
		String url = GET_TREES_URL;

		url = String.format("%s/%s?%s=%b&%s=%d", url, path, "history", history, "recursion", recursion);

		return new GetCall<Trees>() {
			public Trees getValue(ServiceCallResult result) {
				return result.getTrees();
			}
		}.doCall(url, repo, revision);
	}

	public static final String GET_BLOB_URL = "repository/{repo}/blob/{revision}";

	/** @Override in fact it does override, but ... */
	public Blob getBlob(String repository, String revision, String path) throws EntityNotFoundException {

		String url = GET_BLOB_URL;

		url = String.format("%s/%s", url, path);

		return new GetCall<Blob>() {
			public Blob getValue(ServiceCallResult result) {
				return result.getBlob();
			}
		}.doCall(url, repository, revision);
	}

	public static final String GET_BLAME_URL = "repository/{repo}/blame/{revision}";

	/** @Override in fact it does override, but ... */
	public Blame getBlame(String repository, String revision, String path) throws EntityNotFoundException {

		String url = GET_BLAME_URL;

		url = String.format("%s/%s", url, path);

		return new GetCall<Blame>() {
			public Blame getValue(ServiceCallResult result) {
				return result.getBlame();
			}
		}.doCall(url, repository, revision);
	}

	public static final String GET_ITEM_URL = "repository/{repo}/item/{revision}";

	/** @Override in fact it does override, but ... */
	public Item getItem(String repository, String revision, String path) throws EntityNotFoundException {

		String url = GET_ITEM_URL;

		url = String.format("%s/%s", url, path);

		return new GetCall<Item>() {
			public Item getValue(ServiceCallResult result) {
				return result.getItem();
			}
		}.doCall(url, repository, revision);
	}
}
