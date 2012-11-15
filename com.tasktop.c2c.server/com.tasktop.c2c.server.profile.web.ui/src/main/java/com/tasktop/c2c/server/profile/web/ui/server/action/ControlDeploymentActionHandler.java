/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

import com.tasktop.c2c.server.deployment.domain.DeploymentStatus;
import com.tasktop.c2c.server.deployment.service.ServiceException;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.ControlDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentStatusResult;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class ControlDeploymentActionHandler extends
		AbstractDeploymentActionHandler<ControlDeploymentAction, DeploymentStatusResult> {

	@Override
	public DeploymentStatusResult execute(ControlDeploymentAction action, ExecutionContext context)
			throws DispatchException {
		setTenancyContext(action.getProjectId());
		try {
			DeploymentStatus status = null;
			switch (action.getAction()) {
			case RESTART:
				status = deploymentConfigurationService.restartDeployment(action.getDeploymentConfiguration());
				break;
			case START:
				status = deploymentConfigurationService.startDeployment(action.getDeploymentConfiguration());
				break;
			case STOP:
				status = deploymentConfigurationService.stopDeployment(action.getDeploymentConfiguration());
				break;
			}
			return new DeploymentStatusResult(status);
		} catch (ServiceException e) {
			throw new ActionException(e);
		}
	}

}
