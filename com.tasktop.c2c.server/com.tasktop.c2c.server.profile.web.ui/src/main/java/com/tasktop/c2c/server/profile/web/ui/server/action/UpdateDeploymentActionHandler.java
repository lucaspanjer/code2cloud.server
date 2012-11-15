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
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentResult;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.UpdateDeploymentAction;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class UpdateDeploymentActionHandler extends
		AbstractDeploymentActionHandler<UpdateDeploymentAction, DeploymentResult> {

	@Override
	public DeploymentResult execute(UpdateDeploymentAction action, ExecutionContext context) throws DispatchException {
		setTenancyContext(action.getProjectId());
		try {
			return new DeploymentResult(deploymentConfigurationService.updateDeployment(action
					.getDeploymentConfiguration()));
		} catch (ValidationException e) {
			throw new ActionException(e);
		} catch (EntityNotFoundException e) {
			throw new ActionException(e);
		}
	}

}
