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

import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.service.ServiceException;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentConfigOptionsResult;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetDeploymentConfigOptionsAction;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GetDeploymentConfigOptionsHandler extends
		AbstractDeploymentActionHandler<GetDeploymentConfigOptionsAction, DeploymentConfigOptionsResult> {

	@Override
	public DeploymentConfigOptionsResult execute(GetDeploymentConfigOptionsAction action, ExecutionContext context)
			throws DispatchException {
		setTenancyContext(action.getProjectId());
		DeploymentConfigOptionsResult result = new DeploymentConfigOptionsResult();
		try {
			DeploymentConfiguration deploymentConfiguration = action.getDeploymentConfiguration();
			result.setAvailableMemories(deploymentConfigurationService
					.getAvailableMemoryConfigurations(deploymentConfiguration));
			result.setAvailableServiceConfigurations(deploymentConfigurationService
					.getAvailableServiceConfigurations(deploymentConfiguration));
			result.setAvailableServices(deploymentConfigurationService.getAvailableServices(deploymentConfiguration));

			return result;
		} catch (ServiceException e) {
			throw new ActionException(e);
		}
	}

}
