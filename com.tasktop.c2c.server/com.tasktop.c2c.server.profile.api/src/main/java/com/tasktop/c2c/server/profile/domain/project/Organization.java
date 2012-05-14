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
package com.tasktop.c2c.server.profile.domain.project;

import java.util.List;

import com.tasktop.c2c.server.common.service.domain.AbstractEntity;

@SuppressWarnings("serial")
public class Organization extends AbstractEntity {
	private String identifier;
	private String name;
	private String description;
	private List<Profile> members;
	private List<Project> projects;
	private ProjectPreferences preferences;

	public Organization() {
		// Default constructor, does nothing.
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Profile> getMembers() {
		return members;
	}

	public void setMembers(List<Profile> members) {
		this.members = members;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public ProjectPreferences getProjectPreferences() {
		return preferences;
	}

	public void setProjectPreferences(ProjectPreferences preferences) {
		this.preferences = preferences;
	}

}
