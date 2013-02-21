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
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Item;

/**
 * @author phrebejk (Tasktop Technologies Inc.)
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-test.xml" })
public class GitServiceBeanBlobTest extends GitServiceTestBase {

	private String repoName;
	private Git repo;

	@Before
	public void setup() throws IOException, GitAPIException {

		super.setup();

		if (repo == null) {
			repoName = newName();
			repo = createAndCloneRepo(repoName);

			commitAndPushFile(repo, "d1/t1.txt", "Test1\nTest2\nTest3", "t1");
		}

	}

	@Test
	public void testGetBlob() throws Exception {

		// Test d1
		Blob b = gitService.getBlob(repoName, "HEAD", "d1/t1.txt");

		Assert.assertNotNull("Blob should not be null", b);

		Assert.assertEquals("Type blob", Item.Type.BLOB, b.getType());
		List<String> lines = b.getLines();

		Assert.assertArrayEquals("Lines", new String[] { "Test1", "Test2", "Test3" },
				lines.toArray(new String[lines.size()]));

	}

	@Test
	public void testGetBlobENFOnNonExisting() throws Exception {

		try {
			Blob b = gitService.getBlob(repoName, "HEAD", "/non/existing/path");
		} catch (EntityNotFoundException ex) {
			Assert.assertNotNull("", ex);
			return;
		}

		Assert.fail("Should have thrown EntityNotFoundException");

	}

	@Test
	public void testGetBlobOnEmpty() throws Exception {

		ensureDirExistsAndIsEmpty(gitRoot);
		ensureDirExistsAndIsEmpty(tempDir);

		String name = newName();
		createAndCloneRepo(name);

		try {
			gitService.getBlob(name, "master", "");
		} catch (EntityNotFoundException ex) {
			Assert.assertNotNull(ex);
			return;
		}

		Assert.fail("Should have thrown EntityNotFoundException");

	}

}
