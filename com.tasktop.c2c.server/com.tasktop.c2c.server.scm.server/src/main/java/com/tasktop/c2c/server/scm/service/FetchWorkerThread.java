/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FetchWorkerThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(FetchWorkerThread.class);

	private static final class FetchStatus {
		long lastUpdateTime = -1;
		boolean isFetching = false;
	}

	private final JgitRepositoryProvider repositoryProvider;

	FetchWorkerThread(JgitRepositoryProvider repositoryProvider) {
		this.repositoryProvider = repositoryProvider;
	}

	/** Time between fetches for a repo. */
	private long milisecondsBetweenUpdates = 5 * 60 * 1000;

	/** Time between scans for new repos to fetch. */
	private long milisecondsBetweenScans = 1000;

	private AtomicBoolean stopRequest = new AtomicBoolean(false);

	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private Map<String, FetchStatus> fetchStatusByRepoPath = new HashMap<String, FetchStatus>();

	@Override
	public void run() {
		while (!stopRequest.get()) {
			try {
				Thread.sleep(milisecondsBetweenScans);
				triggerMirroredFetches();
			} catch (Throwable e) {
				logger.warn("Caught while trying to trigger fetches", e);
			}
		}
	}

	private void triggerMirroredFetches() {
		Long reFetchTime = System.currentTimeMillis() - milisecondsBetweenUpdates;
		for (File projectRoot : this.repositoryProvider.getBaseDir().listFiles()) {
			final String projectId = projectRoot.getName();
			File mirroredRepos = new File(projectRoot, GitConstants.MIRRORED_GIT_DIR);
			if (!mirroredRepos.exists()) {
				continue;
			}
			for (final File mirroredRepo : mirroredRepos.listFiles()) {
				String repoPath = mirroredRepo.getAbsolutePath();
				FetchStatus status = fetchStatusByRepoPath.get(repoPath);
				if (status == null) {
					status = new FetchStatus();
					fetchStatusByRepoPath.put(repoPath, status);
				}
				if (!status.isFetching && status.lastUpdateTime < reFetchTime) {
					status.isFetching = true;
					final FetchStatus fStatus = status;
					threadPool.execute(new Runnable() {

						@Override
						public void run() {
							try {
								doMirrorFetch(projectId, mirroredRepo);
							} finally {
								fStatus.lastUpdateTime = System.currentTimeMillis();
								fStatus.isFetching = false;
							}

						}
					});
				}
			}
		}
	}

	// TODO make these from cache
	private void doMirrorFetch(String projectId, File mirroredRepo) {
		try {
			// Set the home directory. This is used for configuration and ssh keys
			FS fs = FS.detect();
			fs.setUserHome(new File(repositoryProvider.getBaseDir(), projectId));
			FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(mirroredRepo).setFS(fs).setup();
			Git git = new Git(new FileRepository(builder));
			git.fetch().setRefSpecs(new RefSpec("refs/heads/*:refs/heads/*")).setThin(true).call();
		} catch (Exception e) {
			logger.warn("Caught exception durring fetch", e);
		}
	}
}