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
}
