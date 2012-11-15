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

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.tasktop.c2c.server.deployment.domain.CloudService;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceConfiguration;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DeploymentConfigOptionsResult implements Result {
	private List<Integer> availableMemories;
	private List<CloudService> availableServices;
	private List<DeploymentServiceConfiguration> availableServiceConfigurations;

	/**
	 * @return the availableMemories
	 */
	public List<Integer> getAvailableMemories() {
		return availableMemories;
	}

	/**
	 * @param availableMemories
	 *            the availableMemories to set
	 */
	public void setAvailableMemories(List<Integer> availableMemories) {
		this.availableMemories = availableMemories;
	}

	/**
	 * @return the availableServices
	 */
	public List<CloudService> getAvailableServices() {
		return availableServices;
	}

	/**
	 * @param availableServices
	 *            the availableServices to set
	 */
	public void setAvailableServices(List<CloudService> availableServices) {
		this.availableServices = availableServices;
	}

	/**
	 * @return the availableServiceConfigurations
	 */
	public List<DeploymentServiceConfiguration> getAvailableServiceConfigurations() {
		return availableServiceConfigurations;
	}

	/**
	 * @param availableServiceConfigurations
	 *            the availableServiceConfigurations to set
	 */
	public void setAvailableServiceConfigurations(List<DeploymentServiceConfiguration> availableServiceConfigurations) {
		this.availableServiceConfigurations = availableServiceConfigurations;
	}

}
