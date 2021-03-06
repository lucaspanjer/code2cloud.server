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
package com.tasktop.c2c.server.internal.tasks.domain;

// Generated May 26, 2010 11:31:55 AM by Hibernate Tools 3.3.0.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BugsFulltext generated by hbm2java
 */
@Entity
@Table(name = "bugs_fulltext")
@SuppressWarnings("serial")
public class TaskFulltext implements java.io.Serializable {

	private int bugId;
	private String shortDesc;
	private String comments;
	private String commentsNoprivate;

	public TaskFulltext() {
	}

	@Id
	@Column(name = "bug_id", unique = true, nullable = false)
	public int getBugId() {
		return this.bugId;
	}

	public void setBugId(int bugId) {
		this.bugId = bugId;
	}

	@Column(name = "short_desc", nullable = false)
	public String getShortDesc() {
		return this.shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	@Column(name = "comments", length = 16777215)
	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name = "comments_noprivate", length = 16777215)
	public String getCommentsNoprivate() {
		return this.commentsNoprivate;
	}

	public void setCommentsNoprivate(String commentsNoprivate) {
		this.commentsNoprivate = commentsNoprivate;
	}

}
