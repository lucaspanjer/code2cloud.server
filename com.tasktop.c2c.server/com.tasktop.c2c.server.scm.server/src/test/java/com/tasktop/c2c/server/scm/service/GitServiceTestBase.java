/*******************************************************************************
 * Copyright (c) 2010, 2012 Oracle and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Oracle and/or its affiliates.
 ******************************************************************************/
package com.tasktop.c2c.server.scm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.test.context.ContextConfiguration;

import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.scm.domain.Trees;

/**
 * @author phrebejk(Oracle)
 * 
 *         Contains some utility methods for testing the git service
 * 
 */
@ContextConfiguration({ "/applicationContext-test.xml" })
public class GitServiceTestBase {

	@Value("${git.root}")
	protected String gitRoot;

	@Value("${temp.dir}")
	protected String tempDir;

	@Resource
	protected GitService gitService;

	private static int testNum = 1;

	protected String projId = "projid";

	@Before
	public void setup() throws IOException, GitAPIException {
		ensureDirExistsAndIsEmpty(gitRoot);
		ensureDirExistsAndIsEmpty(tempDir);

		TenancyContextHolder.createEmptyContext();
		TenancyUtil.setProjectTenancyContext(projId);
	}

	public static void ensureDirExists(File dir) throws IOException {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void ensureDirExistsAndIsEmpty(String dir) throws IOException {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			FileUtils.deleteDirectory(file);
		}
	}

	protected String newName() {
		return "test" + testNum++ + ".git";
	}

	protected Git createAndCloneRepo(String name) throws IOException {
		gitService.createEmptyRepository(name);

		File gitDir = new File(gitRoot + "/" + projId + "/" + GitConstants.HOSTED_GIT_DIR + "/" + name);
		Git git = Git.cloneRepository().setURI(gitDir.getAbsolutePath()).setDirectory(new File(tempDir)).call();
		return git;
	}

	protected void commitAndPushFile(Git git, String path, String content, String message) throws GitAPIException,
			JGitInternalException, IOException {
		File f = new File(git.getRepository().getDirectory().getParentFile(), path);
		ensureDirExists(f.getParentFile());
		FileOutputStream writer = new FileOutputStream(f);
		writer.write(content.getBytes());
		writer.close();
		git.add().addFilepattern(path).call();
		git.commit().setMessage(message).call();
		git.push().call();
	}

	/** Utility method used from Trees and Item tests */
	static Trees.Tree getTreeByName(Trees trees, String name) {

		for (Trees.Tree t : trees.getTree()) {
			if (name.equals(t.getName())) {
				return t;
			}
		}
		return null;
	}

}
