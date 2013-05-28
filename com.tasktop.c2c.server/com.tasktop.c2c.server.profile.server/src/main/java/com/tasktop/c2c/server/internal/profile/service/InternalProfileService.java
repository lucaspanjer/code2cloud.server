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
package com.tasktop.c2c.server.internal.profile.service;

import java.util.List;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectProfile;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.service.ProfileService;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public interface InternalProfileService extends ProfileService {
	void doDeleteProjectIfReady(String projectIdentifier);

	void doDeleteOrganizationIfReady(String organizationIdentifier);

	List<ProjectProfile> findAccessibleProjectsForProfile(Profile profile);

	void doDeleteProject(String projectIdentifier) throws EntityNotFoundException;

	void doDeleteOrganization(String organizationIdentifier) throws EntityNotFoundException;

	/**
	 * @param query
	 * @param additionalJpaWhereClauseOrNull
	 *            . Can reference the project with "project"
	 * @return
	 */
	QueryResult<Project> findProjects(ProjectsQuery query, String additionalJpaWhereClauseOrNull);
}
