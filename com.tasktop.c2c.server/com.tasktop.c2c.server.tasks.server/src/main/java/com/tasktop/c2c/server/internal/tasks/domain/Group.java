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

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Convert;

/**
 * Groups generated by hbm2java
 */
@Entity
@Table(name = "groups", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@SuppressWarnings("serial")
public class Group implements java.io.Serializable {

	private Integer id;
	private String name;
	private String description;
	private boolean isbuggroup;
	private String userregexp;
	private boolean isactive;
	private String iconUrl;
	private Set<UserGroupMap> userGroupMaps = new HashSet<UserGroupMap>(0);
	private Set<BugGroupMap> bugGroupMaps = new HashSet<BugGroupMap>(0);
	private Set<NamedqueryGroupMap> namedqueryGroupMaps = new HashSet<NamedqueryGroupMap>(0);
	private Set<GroupGroupMap> groupGroupMapsForGrantorId = new HashSet<GroupGroupMap>(0);
	private Set<Flagtypes> flagtypesesForGrantGroupId = new HashSet<Flagtypes>(0);
	private Set<GroupControlMap> groupControlMaps = new HashSet<GroupControlMap>(0);
	private Set<Flagtypes> flagtypesesForRequestGroupId = new HashSet<Flagtypes>(0);
	private Set<CategoryGroupMap> categoryGroupMaps = new HashSet<CategoryGroupMap>(0);
	private Set<GroupGroupMap> groupGroupMapsForMemberId = new HashSet<GroupGroupMap>(0);

	public Group() {
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", unique = true, nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", nullable = false, length = 16777215)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "isbuggroup", nullable = false)
	@Convert("booleanToByte")
	public boolean getIsbuggroup() {
		return this.isbuggroup;
	}

	public void setIsbuggroup(boolean isbuggroup) {
		this.isbuggroup = isbuggroup;
	}

	@Column(name = "userregexp", nullable = false)
	public String getUserregexp() {
		return this.userregexp;
	}

	public void setUserregexp(String userregexp) {
		this.userregexp = userregexp;
	}

	@Column(name = "isactive", nullable = false)
	@Convert("booleanToByte")
	public boolean getIsactive() {
		return this.isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}

	@Column(name = "icon_url")
	public String getIconUrl() {
		return this.iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	public Set<UserGroupMap> getUserGroupMaps() {
		return this.userGroupMaps;
	}

	public void setUserGroupMaps(Set<UserGroupMap> userGroupMaps) {
		this.userGroupMaps = userGroupMaps;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	public Set<BugGroupMap> getBugGroupMaps() {
		return this.bugGroupMaps;
	}

	public void setBugGroupMaps(Set<BugGroupMap> bugGroupMaps) {
		this.bugGroupMaps = bugGroupMaps;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	public Set<NamedqueryGroupMap> getNamedqueryGroupMaps() {
		return this.namedqueryGroupMaps;
	}

	public void setNamedqueryGroupMaps(Set<NamedqueryGroupMap> namedqueryGroupMaps) {
		this.namedqueryGroupMaps = namedqueryGroupMaps;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groupsByGrantorId")
	public Set<GroupGroupMap> getGroupGroupMapsForGrantorId() {
		return this.groupGroupMapsForGrantorId;
	}

	public void setGroupGroupMapsForGrantorId(Set<GroupGroupMap> groupGroupMapsForGrantorId) {
		this.groupGroupMapsForGrantorId = groupGroupMapsForGrantorId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groupsByGrantGroupId")
	public Set<Flagtypes> getFlagtypesesForGrantGroupId() {
		return this.flagtypesesForGrantGroupId;
	}

	public void setFlagtypesesForGrantGroupId(Set<Flagtypes> flagtypesesForGrantGroupId) {
		this.flagtypesesForGrantGroupId = flagtypesesForGrantGroupId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	public Set<GroupControlMap> getGroupControlMaps() {
		return this.groupControlMaps;
	}

	public void setGroupControlMaps(Set<GroupControlMap> groupControlMaps) {
		this.groupControlMaps = groupControlMaps;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groupsByRequestGroupId")
	public Set<Flagtypes> getFlagtypesesForRequestGroupId() {
		return this.flagtypesesForRequestGroupId;
	}

	public void setFlagtypesesForRequestGroupId(Set<Flagtypes> flagtypesesForRequestGroupId) {
		this.flagtypesesForRequestGroupId = flagtypesesForRequestGroupId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	public Set<CategoryGroupMap> getCategoryGroupMaps() {
		return this.categoryGroupMaps;
	}

	public void setCategoryGroupMaps(Set<CategoryGroupMap> categoryGroupMaps) {
		this.categoryGroupMaps = categoryGroupMaps;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groupsByMemberId")
	public Set<GroupGroupMap> getGroupGroupMapsForMemberId() {
		return this.groupGroupMapsForMemberId;
	}

	public void setGroupGroupMapsForMemberId(Set<GroupGroupMap> groupGroupMapsForMemberId) {
		this.groupGroupMapsForMemberId = groupGroupMapsForMemberId;
	}

}
