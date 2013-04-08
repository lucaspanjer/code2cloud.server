/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.profile.service;

import java.util.List;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateOptions;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;

/**
 * Allows to list project templates and apply a template to an existing project.
 * 
 * Applying a template to a project will copy over certain artifacts from the template project including: wiki pages,
 * git repositories, and build jobs
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectTemplateService {

	List<ProjectTemplate> listTemplates(ProjectsQuery projectsQuery);

	List<ProjectTemplateProperty> getPropertiesForTemplate(ProjectTemplate template) throws EntityNotFoundException;

	void applyTemplateToProject(ProjectTemplateOptions options) throws EntityNotFoundException;
}
