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
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.scm.domain.Blame;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.domain.Trees;

/**
 * Interface for interacting with the {@link ScmRepository}s for a given project.
 * 
 * @author Ryan Slobojon <ryan.slobojan@tasktop.com> (Tasktop Technologies Inc.)
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface ScmService {

	// Keep this synchronized with the OSGI version in MANIFEST.MF
	public static final String VERSION = "1.1.0";

	List<ScmRepository> getScmRepositories() throws EntityNotFoundException;

	/**
	 * Create a new repository record.
	 * 
	 * @param newRepository
	 *            should be non-null and have appropriate fields filled in
	 * @return
	 * @throws EntityNotFoundException
	 * @throws ValidationException
	 */
	ScmRepository createScmRepository(ScmRepository newRepository) throws EntityNotFoundException, ValidationException;

	/**
	 * Remove a repository record.
	 * 
	 * @param scmRepository
	 * @throws EntityNotFoundException
	 */
	void deleteScmRepository(ScmRepository repo) throws EntityNotFoundException;

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

	Commit getCommit(String repoName, String commitId) throws EntityNotFoundException;

	/**
	 * XXX probably could be deleted and handled by getLog
	 * 
	 * @param repoName
	 * @param region
	 * @return
	 * @throws EntityNotFoundException
	 */
	List<Commit> getLog(String repoName, Region region) throws EntityNotFoundException;

	/**
	 * XXX probably could be deleted and handled by getLog
	 * 
	 * @param repoName
	 * @param branchName
	 * @param region
	 * @return
	 * @throws EntityNotFoundException
	 */
	List<Commit> getLogForBranch(String repoName, String branchName, Region region) throws EntityNotFoundException;

	/**
	 * @param repoName
	 * @param revision
	 * @param path
	 * @param region
	 * @return
	 * @throws EntityNotFoundException
	 */
	List<Commit> getLog(String repoName, String revision, String path, Region region) throws EntityNotFoundException;

	/**
	 * Creates branch in given repository.
	 * 
	 * @param repoName
	 * @param branchName
	 * @return Name of the branch created
	 */
	String createBranch(String repoName, String branchName) throws EntityNotFoundException;

	/**
	 * Deletes branch in given repository.
	 * 
	 * @param repoName
	 * @param branchName
	 * @return Name of the branch created
	 */
	void deleteBranch(String repoName, String branchName) throws EntityNotFoundException;

	/**
	 * Lists a folder on given path and ref.
	 * 
	 * @param repoName
	 * @param revision
	 * @param path
	 * @param history
	 *            Should latest commit be included for every entry.
	 * @param recursion
	 *            Depth of recursion currently only -2 is recognized, which is good for skipping folders which only
	 *            contain one subfolder.
	 * @return
	 * @throws EntityNotFoundException
	 */
	Trees getTrees(String repoName, String revision, String path, boolean history, int recursion)
			throws EntityNotFoundException;

	/**
	 * Gets annotated source for blob at given path and ref.
	 * 
	 * @param repository
	 * @param revision
	 * @param path
	 * @return
	 * @throws EntityNotFoundException
	 */
	Blame getBlame(String repository, String revision, String path) throws EntityNotFoundException;

	/**
	 * Gets Blob at given path and ref.
	 * 
	 * @param repository
	 * @param revision
	 * @param path
	 * @return
	 * @throws EntityNotFoundException
	 */
	Blob getBlob(String repository, String revision, String path) throws EntityNotFoundException;

	/**
	 * Tries to resolve the item ie finds out the type of it and its SHA-1.
	 * 
	 * @param repository
	 * @param revision
	 * @param path
	 * @return
	 * @throws EntityNotFoundException
	 */
	Item getItem(String repository, String revision, String path) throws EntityNotFoundException;

	String getPublicSshKey();
}
