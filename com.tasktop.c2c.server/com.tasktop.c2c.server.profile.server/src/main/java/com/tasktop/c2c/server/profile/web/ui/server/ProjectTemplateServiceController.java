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
import com.tasktop.c2c.server.common.service.doc.Title;
import com.tasktop.c2c.server.profile.domain.project.ProjectRelationship;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateOptions;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.service.ProjectTemplateService;

@Documentation("A service listing project templates and applying them to projects"
		+ "The template service methods are available by appending the URI to the base URL\n"
		+ "https://{hostname}/{prefix}/api/project-templates/ + URI")
@Controller
public class ProjectTemplateServiceController implements ProjectTemplateService {

	@Autowired
	@Qualifier("main")
	private ProjectTemplateService projectTemplateService;

	@Title("List templates given a project query")
	@Documentation("find project templates matching a project query.")
	@RequestMapping(value = "list", method = RequestMethod.POST)
	@Override
	public List<ProjectTemplate> listTemplates(@RequestBody ProjectsQuery projectsQuery) {
		return projectTemplateService.listTemplates(projectsQuery);
	}

	@Title("List all templates")
	@Documentation("find all project templates a user has access to.")
	// Convenience GET method
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public List<ProjectTemplate> listTemplates() {
		return projectTemplateService.listTemplates(new ProjectsQuery(ProjectRelationship.ALL, null));
	}

	@Title("Apply template")
	@Documentation("Apply a template to an existing project.")
	@RequestMapping(value = "apply", method = RequestMethod.POST)
	@Override
	public void applyTemplateToProject(@RequestBody ProjectTemplateOptions templateOptions)
			throws EntityNotFoundException {
		projectTemplateService.applyTemplateToProject(templateOptions);
	}

	@Title("Get template properties")
	@Documentation("Get the properties for a template.")
	@RequestMapping(value = "properties", method = RequestMethod.POST)
	@Override
	public List<ProjectTemplateProperty> getPropertiesForTemplate(@RequestBody ProjectTemplate template)
			throws EntityNotFoundException {
		return projectTemplateService.getPropertiesForTemplate(template);
	}

}
