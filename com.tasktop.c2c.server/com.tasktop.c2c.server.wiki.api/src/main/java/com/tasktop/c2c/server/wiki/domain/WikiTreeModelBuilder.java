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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.tasktop.c2c.server.wiki.domain.WikiTree.Type;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class WikiTreeModelBuilder {
	public static final WikiTree construtTreeModel(List<Page> allpages) {
		return new WikiTreeModelBuilder().createTree(allpages);
	}

	private Map<String, WikiTree> treesByDirectory = new HashMap<String, WikiTree>();

	private WikiTreeModelBuilder() {

	}

	public WikiTree createTree(List<Page> allpages) {
		WikiTree root = createDirectories(allpages);
		addPages(allpages);
		trimSingularDirectories(root);
		setParentRelativePath(root);

		return root;
	}

	private void trimSingularDirectories(WikiTree root) {
		Queue<WikiTree> queue = new LinkedList<WikiTree>();
		queue.addAll(root.getChildren());

		while (!queue.isEmpty()) {
			WikiTree current = queue.poll();

			if (current.getChildren().size() == 1) {
				WikiTree singleChild = current.getChildren().get(0);

				if (singleChild.getType().equals(Type.DIRECTORY)) {
					current.getChildren().clear();
					current.getChildren().addAll(singleChild.getChildren());
					current.setPath(singleChild.getPath());
					queue.add(current); // Repeat
				} else if (singleChild.getType().equals(Type.PAGE_HEADER)) {
					// Replace the dir with the page
					current.setType(Type.PAGE_HEADER);
					current.setChildren(Collections.EMPTY_LIST);
					current.setPage(singleChild.getPage());
					current.setPath(singleChild.getPath());
				}
			} else if (current.getType().equals(Type.DIRECTORY)) {
				queue.addAll(current.getChildren());
			}
		}
	}

	private void setParentRelativePath(WikiTree root) {
		Queue<WikiTree> queue = new LinkedList<WikiTree>();
		queue.add(root);

		while (!queue.isEmpty()) {
			WikiTree current = queue.poll();

			for (WikiTree child : current.getChildren()) {
				if (!child.getPath().startsWith(current.getPath())) {
					throw new IllegalStateException();
				}
				String parentRelativePath;
				if (child.getPath().equals(current.getPath())) {
					parentRelativePath = "";
				} else {
					int idx = child.getPath().indexOf(current.getPath());
					parentRelativePath = (child.getPath().substring(idx + current.getPath().length() + 1));
				}
				child.setParentRelativePath(parentRelativePath);
			}

			Collections.sort(current.getChildren(), new Comparator<WikiTree>() {

				public int compare(WikiTree o1, WikiTree o2) {
					return o1.getParentRelativePath().compareToIgnoreCase(o2.getParentRelativePath());
				}
			});

			if (current.getType().equals(Type.DIRECTORY)) {
				queue.addAll(current.getChildren());
			}
		}
	}

	private void addPages(List<Page> allpages) {

		for (Page page : allpages) {
			int lastSlash = page.getPath().lastIndexOf("/");
			if (lastSlash == -1) {
				addPage("/", page);
			} else {
				addPage("/" + page.getPath().substring(0, lastSlash), page);
			}
		}
	}

	private void addPage(String parentPath, Page page) {
		// First check if there is a directory with the page name
		WikiTree parent = treesByDirectory.get("/" + page.getPath());
		if (parent == null) {
			parent = treesByDirectory.get(parentPath);
		}
		parent.getChildren().add(new WikiTree(page));

	}

	/**
	 * @param allpages
	 * @return
	 */
	private WikiTree createDirectories(List<Page> allpages) {
		WikiTree root = new WikiTree("", new ArrayList<WikiTree>());
		treesByDirectory.put("/", root);

		for (Page page : allpages) {
			if (page.getPath().indexOf("/") != -1) {

				WikiTree currentParent = root;
				String currentDir = "";
				for (String dir : page.getPath().split("/")) {
					currentDir = currentDir + "/" + dir;
					WikiTree dirTree = treesByDirectory.get(currentDir);
					if (dirTree == null) {
						dirTree = new WikiTree(currentDir, new ArrayList<WikiTree>());
						treesByDirectory.put(currentDir, dirTree);
						currentParent.getChildren().add(dirTree);
					}
					currentParent = dirTree;
				}
			}
		}
		return root;
	}
}
