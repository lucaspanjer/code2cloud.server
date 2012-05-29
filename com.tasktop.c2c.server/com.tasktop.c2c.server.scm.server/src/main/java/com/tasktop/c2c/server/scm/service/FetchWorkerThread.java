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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;

class FetchWorkerThread extends Thread {

	private final GitServiceBean gitServiceBean;

	FetchWorkerThread(GitServiceBean gitServiceBean) {
		this.gitServiceBean = gitServiceBean;
	}

	/** Time between fetches for a repo. */
	private long milisecondsBetweenUpdates = 5 * 60 * 1000;

	/** Time between scans for new repos to fetch. */
	private long milisecondsBetweenScans = 1000;

	private AtomicBoolean stopRequest = new AtomicBoolean(false);

	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private Map<String, Long> lastUpdateTimeByRepoPath = new HashMap<String, Long>();

	@Override
	public void run() {
		while (!stopRequest.get()) {
			try {
				Thread.sleep(milisecondsBetweenScans);
				triggerMirroredFetches();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void triggerMirroredFetches() {
		Long reFetchTime = System.currentTimeMillis() - milisecondsBetweenUpdates;
		for (File projectRoot : new File(this.gitServiceBean.basePath).listFiles()) {
			File mirroredRepos = new File(projectRoot, GitConstants.MIRRORED_GIT_DIR);
			if (!mirroredRepos.exists()) {
				continue;
			}
			for (final File mirroredRepo : mirroredRepos.listFiles()) {
				String repoPath = mirroredRepo.getAbsolutePath();
				Long lastFetch = lastUpdateTimeByRepoPath.get(repoPath);
				if (lastFetch == null || lastFetch < reFetchTime) {
					lastUpdateTimeByRepoPath.put(repoPath, System.currentTimeMillis());
					threadPool.execute(new Runnable() {

						@Override
						public void run() {
							doMirrorFetch(mirroredRepo);

						}
					});
				}
			}
		}
	}

	// TODO make these from cache
	private void doMirrorFetch(File mirroredRepo) {
		try {
			Git git = new Git(new FileRepository(mirroredRepo));
			git.fetch().setRefSpecs(new RefSpec("refs/heads/*:refs/heads/*")).setThin(true).call();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JGitInternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}