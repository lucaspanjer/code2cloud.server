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
package com.tasktop.c2c.server.profile.web.ui.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.doc.Documentation;
import com.tasktop.c2c.server.profile.domain.project.ProjectRelationship;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateOptions;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.service.ProjectTemplateService;

@Documentation("A service for managing a users profile, consisting of identity, projects and project teams.\n"
		+ "The profile service methods are available by appending the URI to the base URL\n"
		+ "https://{hostname}/{prefix}/api + URI, for example: https://example.com/api/profile")
@Controller
public class ProjectTemplateServiceController implements ProjectTemplateService {

	@Autowired
	@Qualifier("main")
	private ProjectTemplateService projectTemplateService;

	@RequestMapping(value = "list", method = RequestMethod.POST)
	@Override
	public List<ProjectTemplate> listTemplates(@RequestBody ProjectsQuery projectsQuery) {
		return projectTemplateService.listTemplates(projectsQuery);
	}

	// Convenience GET method
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public List<ProjectTemplate> listTemplates() {
		return projectTemplateService.listTemplates(new ProjectsQuery(ProjectRelationship.ALL, null));
	}

	@RequestMapping(value = "apply", method = RequestMethod.POST)
	@Override
	public void applyTemplateToProject(@RequestBody ProjectTemplateOptions templateOptions)
			throws EntityNotFoundException {
		projectTemplateService.applyTemplateToProject(templateOptions);
	}

}
