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
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Trees;

/**
 * @author phrebejk (Oracle)
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-test.xml" })
public class GitServiceBeanItemTest extends GitServiceTestBase {

	private String repoName;
	private Git repo;

	@Before
	public void setup() throws IOException, GitAPIException {

		super.setup();

		if (repo == null) {
			repoName = newName();
			repo = createAndCloneRepo(repoName);

			commitAndPushFile(repo, "d1/t1.txt", "Test1", "t1");
			commitAndPushFile(repo, "d1/t2.txt", "test2", "t2");
			commitAndPushFile(repo, "d1/d2/t3.txt", "Test3", "t3");

		}

	}

	@Test
	public void testGetItem() throws Exception {

		Trees rootTree = gitService.getTrees(repoName, "HEAD", "", false, 0);
		Trees d1Tree = gitService.getTrees(repoName, "HEAD", "d1", false, 0);
		Trees d2Tree = gitService.getTrees(repoName, "HEAD", "d1/d2", false, 0);

		Item i = gitService.getItem(repoName, "HEAD", "d1");
		Assert.assertEquals("d1 type", Item.Type.TREE, i.getType());
		Assert.assertEquals("d1 sha", getTreeByName(rootTree, "d1").getSha(), i.getSha());

		i = gitService.getItem(repoName, "HEAD", "d1/t1.txt");
		Assert.assertEquals("d1/t1.txt type", Item.Type.BLOB, i.getType());
		Assert.assertEquals("d1/t1.txt sha", getTreeByName(d1Tree, "t1.txt").getSha(), i.getSha());

		i = gitService.getItem(repoName, "HEAD", "d1/t2.txt");
		Assert.assertEquals("d1/t2.txt type", Item.Type.BLOB, i.getType());
		Assert.assertEquals("d1/t2.txt sha", getTreeByName(d1Tree, "t2.txt").getSha(), i.getSha());

		i = gitService.getItem(repoName, "HEAD", "d1/d2");
		Assert.assertEquals("d1/d2 type", Item.Type.TREE, i.getType());
		Assert.assertEquals("d1/d2 sha", getTreeByName(d1Tree, "d2").getSha(), i.getSha());

		i = gitService.getItem(repoName, "HEAD", "d1/d2/t3.txt");
		Assert.assertEquals("d1/d2/t3.txt type", Item.Type.BLOB, i.getType());
		Assert.assertEquals("d1/d2/t3.txt sha", getTreeByName(d2Tree, "t3.txt").getSha(), i.getSha());

	}

	@Test
	public void testGetItemRoot() throws Exception {
		Item i = gitService.getItem(repoName, "HEAD", "");

		Assert.assertEquals("root type", Item.Type.TREE, i.getType());

	}

	@Test
	public void testGetItemENFOnNonExisting() throws Exception {

		try {
			Item i = gitService.getItem(repoName, "HEAD", "/non/existing/path");
		} catch (EntityNotFoundException ex) {
			Assert.assertNotNull("", ex);
			return;
		}

		Assert.fail("Should have thrown EntityNotFoundException");

	}

	@Test
	public void testGetItemOnEmpty() throws Exception {

		ensureDirExistsAndIsEmpty(gitRoot);
		ensureDirExistsAndIsEmpty(tempDir);

		String name = newName();
		createAndCloneRepo(name);

		try {
			gitService.getItem(name, "master", "");
		} catch (EntityNotFoundException ex) {
			Assert.assertNotNull(ex);
			return;
		}

		Assert.fail("Should have thrown EntityNotFoundException");

	}

}
