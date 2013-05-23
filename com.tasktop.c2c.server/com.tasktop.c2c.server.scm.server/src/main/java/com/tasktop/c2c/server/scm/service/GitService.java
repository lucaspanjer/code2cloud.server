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

import java.util.List;
import java.util.Map;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.scm.domain.Blame;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.domain.Trees;

/**
 * Service for interacting with a project's git repository. Allows to create and delete new internal/external
 * repositories and to obtain summary and log information. Standard git operations (e.g., clone or push) are done using
 * standard git protocol(s) and tools.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface GitService {

	void addExternalRepository(String url);

	void removeExternalRepository(String url);

	void removeInternalRepository(String name);

	/**
	 * Create an empty repository.
	 * 
	 * @param name
	 *            of the repository
	 */
	void createEmptyRepository(String name);

	/**
	 * Get the logs from all repositories.
	 * 
	 * @param region
	 *            : can be null => all
	 * @return
	 */
	List<Commit> getLog(Region region);

	/**
	 * Get a summary of activity from all repositories.
	 * 
	 * @param numDays
	 *            back in time to go
	 * @return
	 */
	List<ScmSummary> getScmSummary(int numDays);

	Map<Profile, Integer> getNumCommitsByAuthor(int numDays);

	/**
	 * @return
	 */
	List<ScmRepository> listRepositories();

	/**
	 * @param repositoryName
	 * @param commitId
	 * @return
	 * @throws EntityNotFoundException
	 */
	Commit getCommitWithDiff(String repositoryName, String commitId, Integer context) throws EntityNotFoundException;

	/**
	 * @param repositoryName
	 * @param baseCommitId can be null. Null is used in case if <code>List&lt;{@link DiffEntry}&gt;</code> is required
	 * for commit (represented by commitId) and its parent
	 * @param commitId
	 * @param context
	 * @return
	 * @throws EntityNotFoundException 
	 */
	List<DiffEntry> getDiffEntries(String repositoryName, String baseCommitId, String commitId, Integer context) throws EntityNotFoundException;

	/**
	 * @param repoName
	 * @param region
	 * @return
	 * @throws EntityNotFoundException
	 */
	List<Commit> getLog(String repoName, Region region) throws EntityNotFoundException;

	List<Commit> getLog(String repoName, String revision, String path, Region region) throws EntityNotFoundException;

	/**
	 * @param repoName
	 * @param branchNameTrees
	 *            getTrees( String repoName, String revision, String path, boolean history, int recursion) throws
	 *            EntityNotFoundException;
	 * 
	 *            Blame getBlame( String repository, String revision, String path) throws EntityNotFoundException;
	 * 
	 *            Blob getBlob( String repository, String revision, String path) throws EntityNotFoundException;
	 * @param region
	 * @return
	 * @throws EntityNotFoundException
	 */
	List<Commit> getLogForBranch(String repoName, String branchName, Region region) throws EntityNotFoundException;

	/**
	 * Creates branch.
	 * 
	 * @param repoName
	 * @param branchName
	 * @return
	 * @throws EntityNotFoundException
	 */
	String createBranch(String repoName, String branchName) throws EntityNotFoundException;

	/**
	 * Removes branch.
	 * 
	 * @param repoName
	 * @param branchName
	 * @throws EntityNotFoundException
	 */
	void deleteBranch(String repoName, String branchName) throws EntityNotFoundException;

	/**
	 * Lists a folder in given repository on given revision.
	 * 
	 * @param repoName
	 * @param revision
	 * @param path
	 * @param history
	 * @param recursion
	 * @return
	 * @throws EntityNotFoundException
	 */
	Trees getTrees(String repoName, String revision, String path, boolean history, int recursion)
			throws EntityNotFoundException;

	/**
	 * Get resource as lines (or maybe Base64 encoded later.
	 */
	Blob getBlob(String repository, String revision, String path) throws EntityNotFoundException;

	/**
	 * Get blame for given file.
	 */
	Blame getBlame(String repository, String revision, String path) throws EntityNotFoundException;

	/** Resolve item on given patrh and at given change set */
	Item getItem(String repository, String revision, String path) throws EntityNotFoundException;

	Commit getMergeBase(String repository, String revA, String revB) throws EntityNotFoundException;

	/**
	 * @return
	 */
	String getPublicSshKey();

}
