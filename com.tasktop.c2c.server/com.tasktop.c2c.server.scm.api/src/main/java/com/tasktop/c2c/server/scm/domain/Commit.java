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
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class Commit implements Serializable {
	private String repository;
	private String commitId;
	private Profile author;
	private Date date;
	private Profile committer; // may be null
	private Date commitDate;
	private String comment;
	private List<String> parents;

	private List<DiffEntry> changes;
	private String diffText;

	public Commit() {

	}

	public Commit(String number, Profile author, Date date, String comment) {
		this.commitId = number;
		this.author = author;
		this.date = date;
		this.comment = comment;
	}

	public void setCommitId(String number) {
		this.commitId = number;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public Profile getAuthor() {
		return author;
	}

	public void setAuthor(Profile author) {
		this.author = author;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getRepository() {
		return repository;
	}

	public List<String> getParents() {
		return parents;
	}

	public void setParents(List<String> parents) {
		this.parents = parents;
	}

	public String getDiffText() {
		return diffText;
	}

	public void setDiffText(String diffText) {
		this.diffText = diffText;
	}

	public List<DiffEntry> getChanges() {
		return changes;
	}

	public void setChanges(List<DiffEntry> changes) {
		this.changes = changes;
	}

	public String getMinimizedCommitId() {
		return getCommitId().length() > 6 ? getCommitId().substring(0, 7) : getCommitId();
	}

	public Profile getCommitter() {
		return committer;
	}

	public void setCommitter(Profile commiter) {
		this.committer = commiter;
	}

	public Date getCommitDate() {
		return commitDate;
	}

	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}
}
