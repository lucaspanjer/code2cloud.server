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
package com.tasktop.c2c.server.profile.web.ui.server.action;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.ControlDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentResult;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class ControlDeploymentActionHandler extends
		AbstractDeploymentActionHandler<ControlDeploymentAction, DeploymentResult> {

	@Override
	public DeploymentResult execute(ControlDeploymentAction action, ExecutionContext context) throws DispatchException {
		setTenancyContext(action.getProjectId());
		try {
			DeploymentConfiguration result = null;
			switch (action.getAction()) {
			case RESTART:
				result = deploymentConfigurationService.restartDeployment(action.getDeploymentConfiguration());
				break;
			case START:
				result = deploymentConfigurationService.startDeployment(action.getDeploymentConfiguration());
				break;
			case STOP:
				result = deploymentConfigurationService.stopDeployment(action.getDeploymentConfiguration());
				break;
			}
			return new DeploymentResult(result);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
