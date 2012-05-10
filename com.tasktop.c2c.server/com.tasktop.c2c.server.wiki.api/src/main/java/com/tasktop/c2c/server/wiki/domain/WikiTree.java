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

import java.util.Collections;
import java.util.List;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class WikiTree {

	public enum Type {
		DIRECTORY, PAGE_HEADER, PAGE_OUTLINE_ITEM, NO_OUTLINE
	};

	private Type type;
	private String path;
	private String parentRelativePath;
	private Page page;
	private PageOutlineItem pageOutlineItem;
	private List<WikiTree> children;

	public WikiTree() {

	}

	public WikiTree(String path, List<WikiTree> children) {
		this.path = path;
		this.children = children;
		this.type = Type.DIRECTORY;
	}

	public WikiTree(Page page) {
		this.page = page;
		this.type = Type.PAGE_HEADER;
		this.path = "/" + page.getPath();
		this.children = Collections.emptyList();
	}

	public WikiTree(Page page, PageOutlineItem pageOutlineItem) {
		this.page = page;
		this.pageOutlineItem = pageOutlineItem;
		this.type = Type.PAGE_OUTLINE_ITEM;
		this.children = Collections.emptyList();
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the page
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(Page page) {
		this.page = page;
	}

	/**
	 * @return the children
	 */
	public List<WikiTree> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<WikiTree> children) {
		this.children = children;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the pageOutline
	 */
	public PageOutlineItem getPageOutlineItem() {
		return pageOutlineItem;
	}

	@Override
	public String toString() {
		if (type == null) {
			return super.toString();
		}
		switch (type) {
		case DIRECTORY:
			return "{type=DIR, path: [" + path + "], numChildren [" + (children == null ? "0" : children.size()) + "]}";
		case PAGE_HEADER:
			return "{type=PAGE_HEADER, path: [" + path + "]}";
		case NO_OUTLINE:
			return "no outline";
		case PAGE_OUTLINE_ITEM:
			return "outline";
		default:
			return super.toString();
		}

	}

	public String getParentRelativePath() {
		return parentRelativePath;
	}

	public void setParentRelativePath(String parentRelativePath) {
		this.parentRelativePath = parentRelativePath;
	}

}
