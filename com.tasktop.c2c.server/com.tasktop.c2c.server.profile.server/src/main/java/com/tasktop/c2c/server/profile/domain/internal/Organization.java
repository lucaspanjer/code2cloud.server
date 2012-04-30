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
package com.tasktop.c2c.server.profile.domain.internal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.tasktop.c2c.server.profile.domain.internal.ProjectPreferences;

/**
 * An organization groups together related users and projects.
 * 
 */
@Entity
public class Organization extends BaseEntity {
	private String name;
	private String identifier;
	private String description;

	private List<Project> projects = new ArrayList<Project>();
	private List<OrganizationProfile> organizationProfiles = new ArrayList<OrganizationProfile>();

	private ProjectPreferences preferences;

	public Organization() {
		super();

		// Default constructor, does nothing.
	}

	/**
	 * The name of the organization. Every org must have a name that is unique system-wide.
	 */
	@Basic(optional = false)
	@Column(unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The short name of the organization, used to identify it in URLs.
	 */
	@Basic(optional = false)
	@Column(unique = true, nullable = false)
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * an optional description
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * the projects that participate in this organization
	 */
	@OneToMany(cascade = { CascadeType.PERSIST }, mappedBy = "organization")
	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	/**
	 * compute the {@link #getIdentifier() identifier} from the {@link #getName() name}.
	 */
	public void computeIdentifier() {
		if (getName() != null) {
			// CAREFUL: don't introduce new allowable characters here without considering tenancy and database names
			setIdentifier(getName().trim().replaceAll("[^a-zA-Z0-9\\.]", "-").toLowerCase());
		}
	}

	/**
	 * the profiles that participate in this organization
	 */
	@OneToMany(cascade = { CascadeType.PERSIST }, mappedBy = "organization")
	public List<OrganizationProfile> getOrganizationProfiles() {
		return organizationProfiles;
	}

	public void setOrganizationProfiles(List<OrganizationProfile> organizationProfiles) {
		this.organizationProfiles = organizationProfiles;
	}

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, optional = false)
	@JoinColumn(name = "projectpreferences_id", nullable = false)
	public ProjectPreferences getProjectPreferences() {
		return preferences;
	}

	public void setProjectPreferences(ProjectPreferences preferences) {
		this.preferences = preferences;
	}
}
