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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.service.GitConstants;
import com.tasktop.c2c.server.scm.service.GitService;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-test.xml" })
public class GitServiceBeanTest {

	@Value("${git.root}")
	private String gitRoot;

	@Value("${temp.dir}")
	private String tempDir;

	@Resource
	private GitService gitService;

	private String projId = "projid";

	@Before
	public void setup() throws IOException {
		ensureDirExistsAndIsEmpty(gitRoot);
		ensureDirExistsAndIsEmpty(tempDir);

		TenancyContextHolder.createEmptyContext();
		TenancyUtil.setProjectTenancyContext(projId);
	}

	private void ensureDirExistsAndIsEmpty(String dir) throws IOException {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			FileUtils.deleteDirectory(file);
		}
	}

	static int testNum = 1;

	private String newName() {
		return "test" + testNum++ + ".git";
	}

	private Git createAndCloneRepo(String name) throws IOException {
		gitService.createEmptyRepository(name);

		File gitDir = new File(gitRoot + "/" + projId + "/" + GitConstants.HOSTED_GIT_DIR + "/" + name);
		Git git = Git.cloneRepository().setURI(gitDir.getAbsolutePath()).setDirectory(new File(tempDir)).call();
		return git;
	}

	static int nextDummyFile = 1;

	private void dummyCommitAndPush(Git git) throws GitAPIException, JGitInternalException, IOException {
		File dummy = new File(git.getRepository().getDirectory().getParentFile(), "dummy" + nextDummyFile++ + ".txt");
		FileOutputStream writer = new FileOutputStream(dummy);
		writer.write("TEST".getBytes());
		writer.close();
		git.add().addFilepattern(dummy.getName()).call();
		git.commit().setMessage("message").call();
		git.push().call();

	}

	@Test
	public void testCreateRepoAndGetLog() throws Exception {
		String name = newName();
		Git git = createAndCloneRepo(name);

		List<Commit> log = gitService.getLog(name, null);
		Assert.assertEquals(0, log.size());
		try {
			gitService.getLogForBranch(name, "master", null);
			Assert.fail();
		} catch (EntityNotFoundException e) {
			// expected
		}

		dummyCommitAndPush(git);

		log = gitService.getLog(name, null);
		Assert.assertEquals(1, log.size());
		log = gitService.getLogForBranch(name, "master", null);
		Assert.assertEquals(1, log.size());

		dummyCommitAndPush(git);

		log = gitService.getLog(name, null);
		Assert.assertEquals(2, log.size());
		log = gitService.getLogForBranch(name, "master", null);
		Assert.assertEquals(2, log.size());

	}

}
