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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.service.web.TenancyUtil;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class JgitRepositoryProvider {

	@Value("${git.root}")
	private String basePath;

	public Repository getHostedRepository(String name) throws IOException {
		File repoDir = new File(getTenantHostedBaseDir(), name);
		FileKey key = FileKey.exact(repoDir, FS.DETECTED);
		Repository repo = RepositoryCache.open(key, true);
		return repo;
	}

	public Repository getMirroredRepository(String name) throws IOException {
		return new FileRepository(new File(getTenantMirroredBaseDir(), name));
	}

	public List<Repository> getAllRepositories() {
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

	public File getBaseDir() {
		return new File(basePath);
	}

	public File getTenantBaseDir() {
		return new File(basePath, TenancyUtil.getCurrentTenantProjectIdentifer());
	}

	public File getTenantHostedBaseDir() {
		return new File(getTenantBaseDir(), GitConstants.HOSTED_GIT_DIR);
	}

	public File getTenantMirroredBaseDir() {
		return new File(getTenantBaseDir(), GitConstants.MIRRORED_GIT_DIR);
	}

}
