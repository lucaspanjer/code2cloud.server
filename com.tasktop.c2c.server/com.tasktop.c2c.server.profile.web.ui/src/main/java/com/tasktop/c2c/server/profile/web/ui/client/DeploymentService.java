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
package com.tasktop.c2c.server.profile.web.ui.client;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tasktop.c2c.server.common.web.shared.NoSuchEntityException;
import com.tasktop.c2c.server.common.web.shared.ValidationFailedException;
import com.tasktop.c2c.server.deployment.domain.CloudService;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentStatus;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsResult;

/**
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
@RemoteServiceRelativePath("deploymentService")
public interface DeploymentService extends RemoteService {

	/** Container class. */
	public static class DeploymentConfigurationOptions implements Serializable {
		private List<Integer> availableMemories;
		private List<CloudService> availableServices;
		private List<DeploymentServiceConfiguration> availableServiceConfigurations;
		private GetProjectBuildsResult buildInformation;

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
		public void setAvailableServiceConfigurations(
				List<DeploymentServiceConfiguration> availableServiceConfigurations) {
			this.availableServiceConfigurations = availableServiceConfigurations;
		}

		/**
		 * @return the buildInformation
		 */
		public GetProjectBuildsResult getBuildInformation() {
			return buildInformation;
		}

		/**
		 * @param buildInformation
		 *            the buildInformation to set
		 */
		public void setBuildInformation(GetProjectBuildsResult buildInformation) {
			this.buildInformation = buildInformation;
		}
	}

	public DeploymentConfiguration createDeploymentConfiguration(String projectId, DeploymentConfiguration configuration)
			throws ValidationFailedException;

	public DeploymentConfiguration updateDeployment(DeploymentConfiguration configuration)
			throws NoSuchEntityException, ValidationFailedException;

	public void deleteDeployment(DeploymentConfiguration configuration, boolean alsoDeleteFromCF)
			throws NoSuchEntityException;

	public DeploymentStatus startDeployment(DeploymentConfiguration configuration) throws ValidationFailedException;

	public DeploymentStatus stopDeployment(DeploymentConfiguration configuration) throws ValidationFailedException;

	public DeploymentStatus restartDeployment(DeploymentConfiguration configuration) throws ValidationFailedException;

	public Boolean validateCredentials(DeploymentConfiguration configuration);

	public DeploymentConfigurationOptions getDeploymentConfigurationOptions(String projectId,
			DeploymentConfiguration configuration) throws NoSuchEntityException;

	public CloudService createService(DeploymentConfiguration configuration, CloudService service);

}
