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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.domain.DiffEntry.ChangeType;
import com.tasktop.c2c.server.scm.domain.DiffEntry.Content;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class PatchParserTest {

	private static final String SINGLE_FILE_MODIFY_PATCH = "diff --git a/FileName.ext\n" + //
			"index 8097b47..c1899d8 100644\n" + //
			"--- a/FileName.ext\n" + //
			"+++ b/FileName.ext\n" + //
			"@@ -23,15 +23,15 @@\n" + //
			" context 1\n" + //
			" context 2\n" + //
			"+ add 1\n" + //
			"+ add 2\n" + //
			"- remove 1\n" + //
			"- remove 2\n" + //
			" context 3\n" + //
			" context 4\n";//

	private static final String SINGLE_BINARY_MODIFY_PATCH = "diff --git a/FileName.ext\n" + //
			"index 8097b47..c1899d8 100644\n" + //
			"--- a/FileName.ext\n" + //
			"+++ b/FileName.ext\n" + //
			"Binary files differ\n";//

	private static final String RENAME_PATCH = "diff --git a/bonk.txt b/bash.txt\n" + //
			"similarity index 72%\n" + //
			"rename from bonk.txt\n" + //
			"rename to bash.txt\n";

	private static final String RENAME_CHANGES_PATCH = "diff --git a/bonk.txt b/bash.txt\n" + //
			"similarity index 100%\n" + //
			"rename from bonk.txt\n" + //
			"rename to bash.txt\n" + //
			"index f514131..06d3f2c 100644\n" + //
			"--- a/bonk.txt\n" + //
			"+++ b/bash.txt\n" + //
			"@@ -1,3 +1,4 @@\n" + //
			"+bark\n" + //
			" bar\n" + //
			" foo\n" + //
			" bonk";

	private static final String COPY_PATCH = "diff --git a/bonk.txt b/bash.txt\n" + //
			"similarity index 72%\n" + //
			"copy from bonk.txt\n" + //
			"copy to bash.txt\n";

	private static final String COPY_CHANGES_PATCH = "diff --git a/bonk.txt b/bash.txt\n" + //
			"similarity index 100%\n" + //
			"rename from bonk.txt\n" + //
			"rename to bash.txt\n" + //
			"index f514131..06d3f2c 100644\n" + //
			"--- a/bonk.txt\n" + //
			"+++ b/bash.txt\n" + //
			"@@ -1,3 +1,4 @@\n" + //
			"+bark\n" + //
			" bar\n" + //
			" foo\n" + //
			" bonk";

	private PatchParser parser = new PatchParser();

	@Test
	public void testSimple() {
		List<DiffEntry> diffs = parser.parsePatch(SINGLE_FILE_MODIFY_PATCH);

		Assert.assertEquals(1, diffs.size());
		DiffEntry diff = diffs.get(0);

		Assert.assertEquals(ChangeType.MODIFY, diff.getChangeType());
		Assert.assertEquals("FileName.ext", diff.getOldPath());
		Assert.assertEquals("FileName.ext", diff.getNewPath());
		Assert.assertEquals(4, diff.getContent().size());
	}

	@Test
	public void testBinary() {
		List<DiffEntry> diffs = parser.parsePatch(SINGLE_BINARY_MODIFY_PATCH);

		Assert.assertEquals(1, diffs.size());
		DiffEntry diff = diffs.get(0);

		Assert.assertEquals(ChangeType.MODIFY, diff.getChangeType());
		Assert.assertEquals("FileName.ext", diff.getOldPath());
		Assert.assertEquals("FileName.ext", diff.getNewPath());
		Assert.assertEquals(1, diff.getContent().size());
		Assert.assertEquals(Content.Type.BINARY, diff.getContent().get(0).getType());
	}

	@Test
	public void testRename() {
		List<DiffEntry> diffs = parser.parsePatch(RENAME_PATCH);

		Assert.assertEquals(1, diffs.size());
		DiffEntry diff = diffs.get(0);

		Assert.assertEquals(ChangeType.RENAME, diff.getChangeType());
		Assert.assertEquals("bonk.txt", diff.getOldPath());
		Assert.assertEquals("bash.txt", diff.getNewPath());
	}

	@Test
	public void testRenameWithChanges() {
		List<DiffEntry> diffs = parser.parsePatch(RENAME_CHANGES_PATCH);

		Assert.assertEquals(1, diffs.size());
		DiffEntry diff = diffs.get(0);

		Assert.assertEquals(ChangeType.RENAME, diff.getChangeType());
		Assert.assertEquals("bonk.txt", diff.getOldPath());
		Assert.assertEquals("bash.txt", diff.getNewPath());
	}

	@Test
	public void testCopy() {
		List<DiffEntry> diffs = parser.parsePatch(COPY_PATCH);

		Assert.assertEquals(1, diffs.size());
		DiffEntry diff = diffs.get(0);

		Assert.assertEquals(ChangeType.COPY, diff.getChangeType());
		Assert.assertEquals("bonk.txt", diff.getOldPath());
		Assert.assertEquals("bash.txt", diff.getNewPath());
	}
}
