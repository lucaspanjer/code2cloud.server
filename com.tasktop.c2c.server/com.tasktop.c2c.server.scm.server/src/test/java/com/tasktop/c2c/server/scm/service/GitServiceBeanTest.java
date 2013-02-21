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

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.scm.domain.Commit;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-test.xml" })
public class GitServiceBeanTest extends GitServiceTestBase {

	static int nextDummyFile = 1;

	private void dummyCommitAndPush(Git git) throws GitAPIException, JGitInternalException, IOException {
		commitAndPushFile(git, "dummy" + nextDummyFile++ + ".txt", "TEST", "message");
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

	@Test
	public void testDiffOnInitialCommit() throws Exception {

		String name = newName();
		Git git = createAndCloneRepo(name);
		dummyCommitAndPush(git);
		List<Commit> log = gitService.getLog(name, null);
		Assert.assertEquals(1, log.size());
		Commit c = log.get(0);
		c = gitService.getCommitWithDiff(name, c.getCommitId());
		Assert.assertNotNull(c.getDiffText());

	}

}
