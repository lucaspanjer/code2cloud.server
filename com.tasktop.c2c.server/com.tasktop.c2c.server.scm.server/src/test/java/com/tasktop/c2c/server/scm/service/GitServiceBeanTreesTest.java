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
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Trees;

/**
 * @author phrebejk (Oracle)
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-test.xml" })
public class GitServiceBeanTreesTest extends GitServiceTestBase {

	private String repoName;
	private Git repo;

	@Before
	public void setup() throws IOException, GitAPIException {

		super.setup();

		if (repo == null) {
			repoName = newName();
			repo = createAndCloneRepo(repoName);

			commitAndPushFile(repo, "d1/t1.txt", "Test1", "t1");
			commitAndPushFile(repo, "d1/t2.txt", "test2", "t-2");
			commitAndPushFile(repo, "d1/d2/t3.txt", "Test3", "t3");
			commitAndPushFile(repo, "de/de1/de2/de3/te.txt", "TestEmpty", "te");

			// For history testing
			commitAndPushFile(repo, "d1/t2.txt", "Test2", "t2");
		}

	}

	@Test
	public void testGetTrees() throws Exception {

		Trees t = gitService.getTrees(repoName, "HEAD", "d1", false, 0);

		Assert.assertEquals("There should be 3 items in d1", 3, t.getTree().size());
		assertTree(t, Item.Type.BLOB, "d1/t1.txt", false);
		assertTree(t, Item.Type.BLOB, "d1/t2.txt", false);
		assertTree(t, Item.Type.TREE, "d1/d2", false);
	}

	@Test
	public void testGetTreesEmpty() throws Exception {

		Trees t = gitService.getTrees(repoName, "HEAD", "de", false, -2);

		Assert.assertEquals("There should be 1 items in de", 1, t.getTree().size());
		assertTree(t, Item.Type.TREE, "de/de1", false);
		Assert.assertEquals("empty", "/de2/de3", getTreeByName(t, "de1").getEmpty());
	}

	@Test
	public void testGetTreesLatestCommit() throws Exception {

		Trees t = gitService.getTrees(repoName, "HEAD", "d1", true, 0);

		assertCommit(getTreeByName(t, "t1.txt").getLatestCommit(), "t1");
		assertCommit(getTreeByName(t, "t2.txt").getLatestCommit(), "t2");
		assertCommit(getTreeByName(t, "d2").getLatestCommit(), "t3");

		t = gitService.getTrees(repoName, "HEAD", "", true, 0);
		assertCommit(getTreeByName(t, "d1").getLatestCommit(), "t2");
	}

	@Test
	public void testGetTreesENFOnNonExisting() throws Exception {

		try {
			Trees t = gitService.getTrees(repoName, "HEAD", "/non/existing/path", false, 0);
		} catch (EntityNotFoundException ex) {
			Assert.assertNotNull("", ex);
			return;
		}

		Assert.fail("Should have thrown EntityNotFoundException");

	}

	@Test
	public void testGetTreesOnEmpty() throws Exception {

		ensureDirExistsAndIsEmpty(gitRoot);
		ensureDirExistsAndIsEmpty(tempDir);

		String name = newName();
		createAndCloneRepo(name);

		try {
			gitService.getTrees(name, "master", "", false, 0);
		} catch (EntityNotFoundException ex) {
			Assert.assertNotNull(ex);
			return;
		}

		Assert.fail("Should have thrown EntityNotFoundException");

	}

	private void assertTree(Trees trees, Item.Type type, String path, boolean hasCommit) {
		String name = new File(path).getName();
		Trees.Tree t = getTreeByName(trees, name);

		Assert.assertNotNull("Tree must exist", t);
		Assert.assertEquals("Path", path, t.getPath());
		Assert.assertEquals("Name", new File(path).getName(), t.getName());
		Assert.assertEquals("Has commit", hasCommit, t.getLatestCommit() != null);
	}

	private void assertCommit(Commit commit, String message) {

		Assert.assertNotNull("Commit must exist", commit);
		Assert.assertEquals("Message", message, commit.getComment());

	}

}
