/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
 * Copyright (c) 2010, 2011 SpringSource, a division of VMware
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.wiki.domain;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class WikiTreeModleBuilderTest {

	@Test
	public void testCreateTreeWithDirectoryPage() {
		List<Page> pages = new ArrayList<Page>();
		pages.add(createPage("foo"));
		pages.add(createPage("foo/bar"));
		pages.add(createPage("foo/bonk"));
		// Expect
		// ROOT
		// - foo DIRECTORY
		// -- / PAGE_HEADER (foo page)
		// -- bar PAGE_HEADER
		// -- bonk PAGE_HEADER
		WikiTree tree = WikiTreeModelBuilder.construtTreeModel(pages);
		Assert.assertEquals(1, tree.getChildren().size());
		Assert.assertEquals(WikiTree.Type.DIRECTORY, tree.getChildren().get(0).getType());
		Assert.assertEquals("foo", tree.getChildren().get(0).getParentRelativePath());
		Assert.assertEquals(3, tree.getChildren().get(0).getChildren().size());
		Assert.assertEquals("", tree.getChildren().get(0).getChildren().get(0).getParentRelativePath());
		Assert.assertEquals("bar", tree.getChildren().get(0).getChildren().get(1).getParentRelativePath());
		Assert.assertEquals("bonk", tree.getChildren().get(0).getChildren().get(2).getParentRelativePath());
	}

	@Test
	public void testCreateTreeWithMultiLevelDirectories() {
		List<Page> pages = new ArrayList<Page>();
		pages.add(createPage("A/B/C"));
		pages.add(createPage("A/B"));
		pages.add(createPage("A/B/D"));
		// Expect
		// ROOT
		// - A/B DIRECTORY
		// -- / PAGE_HEADER
		// -- C PAGE_HEADER
		// -- D PAGE_HEADER
		WikiTree tree = WikiTreeModelBuilder.construtTreeModel(pages);
		Assert.assertEquals(1, tree.getChildren().size());
		Assert.assertEquals(WikiTree.Type.DIRECTORY, tree.getChildren().get(0).getType());
		Assert.assertEquals(3, tree.getChildren().get(0).getChildren().size());
	}

	@Test
	public void testCreateTreeWithDeepDirectories() {
		List<Page> pages = new ArrayList<Page>();
		pages.add(createPage("foo/bonk/bar/baz"));
		// Expect
		// ROOT
		// - foo/bonk/bar/baz PAGE_HEADER
		WikiTree tree = WikiTreeModelBuilder.construtTreeModel(pages);
		Assert.assertEquals(1, tree.getChildren().size());
		Assert.assertEquals(WikiTree.Type.PAGE_HEADER, tree.getChildren().get(0).getType());
		Assert.assertEquals(0, tree.getChildren().get(0).getChildren().size());
	}

	private static int nextId = 0;

	private Page createPage(String path) {
		Page p = new Page();
		p.setPath(path);
		p.setContent("DUMMY");
		p.setId(nextId++);
		return p;
	}
}
