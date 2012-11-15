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
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ControlDeploymentAction implements Action<DeploymentStatusResult>, WriteAction {
	public enum Action {
		START, STOP, RESTART;
	}

	private String projectId;
	private DeploymentConfiguration deploymentConfiguration;
	private Action action;

	public ControlDeploymentAction(String projectId, DeploymentConfiguration deploymentConfiguration, Action action) {
		this.projectId = projectId;
		this.deploymentConfiguration = deploymentConfiguration;
		this.action = action;
	}

	protected ControlDeploymentAction() {
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

	public Action getAction() {
		return action;
	}

}
