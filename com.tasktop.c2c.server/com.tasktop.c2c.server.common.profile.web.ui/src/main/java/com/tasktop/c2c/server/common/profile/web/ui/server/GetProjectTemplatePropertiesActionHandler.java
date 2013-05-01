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
package com.tasktop.c2c.server.common.profile.web.ui.server;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectTemplatePropertiesAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectTemplatePropertiesResult;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.profile.service.ProjectTemplateService;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GetProjectTemplatePropertiesActionHandler extends
		AbstractProfileActionHandler<GetProjectTemplatePropertiesAction, GetProjectTemplatePropertiesResult> {

	@Autowired
	@Qualifier("main")
	private ProjectTemplateService projectTemplateService;

	@Override
	public GetProjectTemplatePropertiesResult execute(GetProjectTemplatePropertiesAction action,
			ExecutionContext context) throws DispatchException {
		try {
			setTenancyContext(action.getProjectTemplate().getProjectId());
			return new GetProjectTemplatePropertiesResult(projectTemplateService.getPropertiesForTemplate(action
					.getProjectTemplate()));
		} catch (EntityNotFoundException e) {
			throw new ActionException(e);
		}
	}
}
