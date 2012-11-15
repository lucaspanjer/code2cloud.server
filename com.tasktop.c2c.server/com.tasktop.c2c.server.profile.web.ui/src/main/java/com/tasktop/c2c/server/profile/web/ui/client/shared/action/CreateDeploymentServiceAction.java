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
package com.tasktop.c2c.server.profile.web.ui.client.shared.action;

import net.customware.gwt.dispatch.shared.Action;

import com.tasktop.c2c.server.common.web.shared.WriteAction;
import com.tasktop.c2c.server.deployment.domain.CloudService;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class CreateDeploymentServiceAction implements Action<CreateDeploymentServiceResult>, WriteAction {
	private String projectId;
	private DeploymentConfiguration deploymentConfiguration;
	private CloudService cloudService;

	public CreateDeploymentServiceAction(String projectId, DeploymentConfiguration deploymentConfiguration,
			CloudService cloudService) {
		this.projectId = projectId;
		this.deploymentConfiguration = deploymentConfiguration;
		this.cloudService = cloudService;
	}

	protected CreateDeploymentServiceAction() {
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	public DeploymentConfiguration getDeploymentConfiguration() {
		return deploymentConfiguration;
	}

	public CloudService getCloudService() {
		return cloudService;
	}

}
