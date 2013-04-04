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

import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class Commit extends Item {
	private String url;
	private String repository;
	private Profile author;
	private Date date;
	private Profile committer; // may be null
	private Date commitDate;
	private String comment;
	private List<String> parents;

	private List<DiffEntry> changes;

	public Commit() {

	}

	public Commit(String number, Profile author, Date date, String comment) {
		super(number, Item.Type.COMMIT);
		this.author = author;
		this.date = date;
		this.comment = comment;
	}

	/** Same as setSha(). */
	public void setCommitId(String number) {
		setSha(number);
	}

	/** Same as getSha(). */
	public String getCommitId() {
		return getSha();
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

	public List<DiffEntry> getChanges() {
		return changes;
	}

	public void setChanges(List<DiffEntry> changes) {
		this.changes = changes;
	}

	public String getMinimizedCommitId() {
		return minimizeCommitId(getSha());
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

	@Override
	public String toString() {
		return "{repo: [" + repository + "], id:[" + getMinimizedCommitId() + "]}";
	}

	public static String minimizeCommitId(String commitId) {
		if (commitId == null) {
			return null;
		}
		return commitId.length() > 6 ? commitId.substring(0, 7) : commitId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
