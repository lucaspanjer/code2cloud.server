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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * NamedqueryGroupMap generated by hbm2java
 */
@Entity
@Table(name = "namedquery_group_map", uniqueConstraints = @UniqueConstraint(columnNames = "namedquery_id"))
@SuppressWarnings("serial")
public class NamedqueryGroupMap implements java.io.Serializable {

	private NamedqueryGroupMapId id;
	private Group groups;
	private Namedquery namedqueries;

	public NamedqueryGroupMap() {
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "namedqueryId", column = @Column(name = "namedquery_id", unique = true, nullable = false)),
			@AttributeOverride(name = "groupId", column = @Column(name = "group_id", nullable = false)) })
	public NamedqueryGroupMapId getId() {
		return this.id;
	}

	public void setId(NamedqueryGroupMapId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false, insertable = false, updatable = false)
	public Group getGroups() {
		return this.groups;
	}

	public void setGroups(Group groups) {
		this.groups = groups;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "namedquery_id", unique = true, nullable = false, insertable = false, updatable = false)
	public Namedquery getNamedqueries() {
		return this.namedqueries;
	}

	public void setNamedqueries(Namedquery namedqueries) {
		this.namedqueries = namedqueries;
	}

}
