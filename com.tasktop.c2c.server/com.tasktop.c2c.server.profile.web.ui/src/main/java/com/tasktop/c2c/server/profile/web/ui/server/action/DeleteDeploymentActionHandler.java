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

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.deployment.service.ServiceException;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeleteDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentResult;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class DeleteDeploymentActionHandler extends
		AbstractDeploymentActionHandler<DeleteDeploymentAction, DeploymentResult> {

	@Override
	public DeploymentResult execute(DeleteDeploymentAction action, ExecutionContext context) throws DispatchException {
		setTenancyContext(action.getProjectId());
		try {
			deploymentConfigurationService.deleteDeployment(action.getDeploymentConfiguration(),
					action.isDeleteInService());
			return new DeploymentResult(action.getDeploymentConfiguration());
		} catch (ServiceException e) {
			throw new ActionException(e);
		} catch (EntityNotFoundException e) {
			throw new ActionException(e);
		}
	}

}
