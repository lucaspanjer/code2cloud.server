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
package com.tasktop.c2c.server.scm.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DiffEntry implements Serializable {

	/** General type of change a single file-level patch describes. */
	public static enum ChangeType {
		/** Add a new file to the project */
		ADD,

		/** Modify an existing file in the project (content and/or mode) */
		MODIFY,

		/** Delete an existing file from the project */
		DELETE,

		/** Rename an existing file to a new location */
		RENAME,

		/** Copy an existing file to a new location, keeping the original */
		COPY;
	}

	public static class Content implements Serializable {
		public static enum Type {
			CONTEXT, ADDED, REMOVED;
		}

		public Content() {

		}

		public Content(Type type, String content) {
			this.type = type;
			this.content = content;
		}

		private Type type;
		private String content;

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
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content
		 *            the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

	}

	private String oldPath;
	private String newPath;
	private ChangeType changeType;
	private int linesAdded = 0;
	private int linesRemoved = 0;
	private List<Content> content;

	public String getOldPath() {
		return oldPath;
	}

	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}

	public String getNewPath() {
		return newPath;
	}

	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public int getLinesAdded() {
		return linesAdded;
	}

	public void setLinesAdded(int linesAdded) {
		this.linesAdded = linesAdded;
	}

	public int getLinesRemoved() {
		return linesRemoved;
	}

	public void setLinesRemoved(int linesRemoved) {
		this.linesRemoved = linesRemoved;
	}

	/**
	 * @return the content
	 */
	public List<Content> getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(List<Content> content) {
		this.content = content;
	}

}
