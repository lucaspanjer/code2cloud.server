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

import java.io.Serializable;

import com.tasktop.c2c.server.common.service.domain.QueryRequest;

/**
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public class ProjectsQuery implements Serializable {
	private ProjectRelationship projectRelationship;
	private String queryString;
	private String organizationIdentifier;
	private QueryRequest queryRequest;

	public ProjectsQuery() {

	}

	public ProjectsQuery(ProjectRelationship projectRelationship, QueryRequest queryRequest) {
		this.projectRelationship = projectRelationship;
		this.queryRequest = queryRequest;
	}

	public ProjectsQuery(String queryString, QueryRequest queryRequest) {
		this.queryString = queryString;
		this.queryRequest = queryRequest;
	}

	public ProjectRelationship getProjectRelationship() {
		return projectRelationship;
	}

	public void setProjectRelationship(ProjectRelationship projectRelationship) {
		this.projectRelationship = projectRelationship;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public QueryRequest getQueryRequest() {
		return queryRequest;
	}

	public void setQueryRequest(QueryRequest queryRequest) {
		this.queryRequest = queryRequest;
	}

	public String getOrganizationIdentifier() {
		return organizationIdentifier;
	}

	public void setOrganizationIdentifier(String organizationIdentifier) {
		this.organizationIdentifier = organizationIdentifier;
	}
}
