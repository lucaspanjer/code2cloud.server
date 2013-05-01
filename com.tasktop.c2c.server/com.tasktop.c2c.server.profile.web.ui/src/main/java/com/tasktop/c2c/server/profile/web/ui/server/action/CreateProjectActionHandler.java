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
package com.tasktop.c2c.server.profile.web.ui.server.action;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.CreateProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.CreateProjectResult;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateOptions;
import com.tasktop.c2c.server.profile.service.ProjectTemplateService;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class CreateProjectActionHandler extends AbstractProfileActionHandler<CreateProjectAction, CreateProjectResult> {

	@Autowired
	@Qualifier("main")
	private ProjectTemplateService projectTemplateService;

	@Override
	public CreateProjectResult execute(CreateProjectAction action, ExecutionContext context) throws DispatchException {
		try {
			Project created = profileWebService.createProject(action.getProject());
			if (action.getProjectTemplate() != null) {
				ProjectTemplateOptions options = new ProjectTemplateOptions();
				if (action.getProjectTemplateOptions() != null) {
					options = action.getProjectTemplateOptions();
				}
				options.setTargetProjectIdentifier(created.getIdentifier());
				options.setTemplate(action.getProjectTemplate());
				projectTemplateService.applyTemplateToProject(options);
			}
			return new CreateProjectResult(created);
		} catch (EntityNotFoundException e) {
			throw new ActionException(e);
		} catch (ValidationException e) {
			throw new ActionException(e);
		}
	}

}
