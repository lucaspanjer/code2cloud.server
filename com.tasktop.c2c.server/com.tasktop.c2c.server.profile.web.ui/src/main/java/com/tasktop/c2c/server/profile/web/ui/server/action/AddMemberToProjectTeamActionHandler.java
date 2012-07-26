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

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.AddMemberToProjectTeamAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectTeamResult;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectTeamSummary;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class AddMemberToProjectTeamActionHandler extends
		AbstractProfileActionHandler<AddMemberToProjectTeamAction, GetProjectTeamResult> {

	@Override
	public GetProjectTeamResult execute(AddMemberToProjectTeamAction action, ExecutionContext context)
			throws DispatchException {
		try {
			setTenancyContext(action.getProjectId());

			profileService.createProjectProfile(action.getProjectId(), action.getProfile().getUsername());

			Project project = profileService.getActiveProjectByIdentifier(action.getProjectId());
			ProjectTeamSummary summary = webServiceDomain.copyTeamSummary(project);
			return new GetProjectTeamResult(summary);

		} catch (EntityNotFoundException e) {
			throw new ActionException(e);
		}
	}

}
