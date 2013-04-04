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
import java.util.ArrayList;
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

	public static class Hunk implements Serializable {

		public static final int NO_CONTEXT = -1;
		public static final int ALL_CONTEXT = -2;

		public static class LineChange implements Serializable {

			public static enum Type {
				CONTEXT, ADDED, REMOVED, BINARY;
			}

			private Type type;

			private String text;

			public LineChange() {
			}; // To support GWT serialization

			public LineChange(Type type, String text) {
				this.type = type;
				this.text = text;
			}

			public Type getType() {
				return type;
			}

			public void setType(Type type) {
				this.type = type;
			}

			public String getText() {
				return text;
			}

			public void setText(String text) {
				this.text = text;
			}

		}

		private int aStartLine;

		private int aEndLine;

		private int bStartLine;

		private int bEndLine;

		private List<LineChange> lineChanges;

		public Hunk() {
		}; // To support GWT serialization

		public Hunk(int aStartLine, int aEndLine, int bStartLine, int bEndLine) {
			this.aStartLine = aStartLine;
			this.aEndLine = aEndLine;
			this.bStartLine = bStartLine;
			this.bEndLine = bEndLine;
			this.lineChanges = new ArrayList<LineChange>();
		}

		public int getAStartLine() {
			return aStartLine;
		}

		public void setAStartLine(int aStartLine) {
			this.aStartLine = aStartLine;
		}

		public int getAEndLine() {
			return aEndLine;
		}

		public void setAEndLine(int aEndLine) {
			this.aEndLine = aEndLine;
		}

		public int getBStartLine() {
			return bStartLine;
		}

		public void setBStartLine(int bStartLine) {
			this.bStartLine = bStartLine;
		}

		public int getBEndLine() {
			return bEndLine;
		}

		public void setBEndLine(int bEndLine) {
			this.bEndLine = bEndLine;
		}

		public List<LineChange> getLineChanges() {
			return lineChanges;
		}

		public void setLineChanges(List<LineChange> lineChanges) {
			this.lineChanges = lineChanges;
		}

	}

	private String oldPath;
	private String newPath;
	private ChangeType changeType;
	private int linesAdded = 0;
	private int linesRemoved = 0;
	private boolean binary;
	private List<Hunk> hunks;

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

	public List<Hunk> getHunks() {
		return hunks;
	}

	public void setHunks(List<Hunk> hunks) {
		this.hunks = hunks;
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

}
