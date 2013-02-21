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
package com.tasktop.c2c.server.scm.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author phrebejk
 */
@SuppressWarnings("serial")
public class Trees extends Item {

	private List<Tree> tree = new ArrayList<Tree>();

	public Trees() {
	}

	public Trees(String sha) {
		super(sha, Type.TREE);
	}

	public List<Tree> getTree() {
		return tree;
	}

	public void setTree(List<Tree> tree) {
		this.tree = tree;
	}

	public static class Tree extends Item {

		public Tree() {
		}

		public Tree(String sha, String path, String name, Item.Type type, int mode, long size) {
			super(sha, type);
			this.path = path;
			this.name = name;
			this.mode = mode;
			this.size = size;
		}

		private String path;
		private String name;
		private int mode;
		private long size;
		private String empty; // Path containing only single folder if asked for
		private Commit latestCommit; // Commit in case history has been asked for

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getMode() {
			return mode;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public String getEmpty() {
			return empty;
		}

		public void setEmpty(String empty) {
			this.empty = empty;
		}

		public Commit getLatestCommit() {
			return latestCommit;
		}

		public void setLatestCommit(Commit commit) {
			this.latestCommit = commit;
		}

	}

}
