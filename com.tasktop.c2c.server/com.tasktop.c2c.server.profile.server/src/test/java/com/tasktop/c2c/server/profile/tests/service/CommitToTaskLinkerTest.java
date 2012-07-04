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
package com.tasktop.c2c.server.profile.tests.service;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.tasktop.c2c.server.internal.profile.service.CommitToTaskLinker;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.tasks.service.TaskService;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class CommitToTaskLinkerTest {

	TaskService mock;

	private String COMMIT_MSG = "task 4341: Chef scripts loop on deploy";
	private String COMMIT_URL = "https://q.tasktop.com/alm/#projects/c2c/task/4341";
	private String PROJ_ID = "c2c";
	private String T_ID = "4341";

	@Ignore
	public void test() {

		CommitToTaskLinker linker = new CommitToTaskLinker(PROJ_ID, mock);
		Commit commit = new Commit();
		commit.setComment(COMMIT_MSG + "\n" + COMMIT_URL);
		linker.process(Arrays.asList(commit));
	}

	@Test
	public void testIntegerPattern() {
		check(CommitToTaskLinker.INT_IDENTIFIER_PATTERN, COMMIT_MSG, T_ID);
		check(CommitToTaskLinker.INT_IDENTIFIER_PATTERN, COMMIT_URL);
		check(CommitToTaskLinker.INT_IDENTIFIER_PATTERN, "task XXX");
	}

	@Test
	public void testUrlPattern() {

		check(CommitToTaskLinker.taskUrlPattern(PROJ_ID), COMMIT_MSG);
		check(CommitToTaskLinker.taskUrlPattern(PROJ_ID), COMMIT_URL, T_ID);
		check(CommitToTaskLinker.taskUrlPattern(PROJ_ID), COMMIT_URL
				+ "\nFOO\nBAR\nhttps://q.tasktop.com/alm/#projects/c2c/task/4342", T_ID, "4342");
		check(CommitToTaskLinker.taskUrlPattern(PROJ_ID + 2), COMMIT_URL);
		check(CommitToTaskLinker.taskUrlPattern(PROJ_ID), "projects/c2c/task/4341");
	}

	private void check(String pattern, String message, String... expectedGroupMatchs) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(message);
		int i = 0;

		boolean found = false;
		while (m.find()) {
			String group = m.group(1);
			found = true;
			Assert.assertEquals(expectedGroupMatchs[i++], group);
		}

		if (expectedGroupMatchs.length == 0) {
			Assert.assertFalse(found);
		}

	}
}
