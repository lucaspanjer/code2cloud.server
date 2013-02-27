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
package com.tasktop.c2c.server.common.profile.web.ui.server;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectTemplatesAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectTemplatesResult;
import com.tasktop.c2c.server.profile.domain.project.ProjectRelationship;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.service.ProjectTemplateService;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GetProjectTemplatesActionHandler extends
		AbstractProfileActionHandler<GetProjectTemplatesAction, GetProjectTemplatesResult> {

	@Autowired
	@Qualifier("main")
	private ProjectTemplateService projectTemplateService;

	@Override
	public GetProjectTemplatesResult execute(GetProjectTemplatesAction action, ExecutionContext context)
			throws DispatchException {
		return new GetProjectTemplatesResult(projectTemplateService.listTemplates(new ProjectsQuery(
				ProjectRelationship.ALL, null)));
	}

}
