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
package com.tasktop.c2c.server.common.profile.web.shared.actions;

import net.customware.gwt.dispatch.shared.Action;

import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateOptions;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class CreateProjectAction implements Action<CreateProjectResult> {
	private Project project;
	private ProjectTemplate projectTemplate;
	private ProjectTemplateOptions projectTemplateOptions;

	public CreateProjectAction(Project project) {
		this.project = project;
	}

	public CreateProjectAction(Project project, ProjectTemplate projectTemplate,
			ProjectTemplateOptions projectTemplateOptions) {
		this(project);
		this.projectTemplate = projectTemplate;
		this.projectTemplateOptions = projectTemplateOptions;
	}

	protected CreateProjectAction() {
	}

	public Project getProject() {
		return project;
	}

	public ProjectTemplate getProjectTemplate() {
		return projectTemplate;
	}

	public ProjectTemplateOptions getProjectTemplateOptions() {
		return projectTemplateOptions;
	}

}
