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
package com.tasktop.c2c.server.profile.web.ui.client.view.deployment;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceType;
import com.tasktop.c2c.server.deployment.domain.DeploymentStatus;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentConfigOptionsResult;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface IDeploymentsView extends IsWidget {

	public interface Presenter {
		void save();

		void update();

		void delete(final DeploymentConfiguration config, boolean alsoDeleteFromCF);

		void doStart();

		void doStop();

		void doRestart();

		void jobNameChanged(final String jobName);

		void validateCredentials();

		void editStarted(final DeploymentConfiguration config);

		void newDeployment();
	}

	void setPresenter(Presenter p);

	/**
	 * @param jobName
	 * @param builds
	 */
	void setBuilds(String jobName, List<BuildDetails> builds);

	/**
	 * @return
	 */
	DeploymentConfiguration getNewValue();

	/**
	 * @param boolean1
	 */
	void setCredentialsValid(Boolean boolean1);

	/**
	 * @param config
	 */
	void deleted(DeploymentConfiguration config);

	/**
	 * @param deploymentConfiguration
	 */
	void updated(DeploymentConfiguration deploymentConfiguration);

	/**
	 * @return
	 */
	DeploymentConfiguration getEditValue();

	/**
	 * @param deploymentConfiguration
	 */
	void newDeploymentCreated(DeploymentConfiguration deploymentConfiguration);

	/**
	 * @param configuration
	 * @param deploymentStatus
	 */
	void updateStatus(DeploymentConfiguration configuration, DeploymentStatus deploymentStatus);

	/**
	 * @return
	 */
	DeploymentConfiguration getOriginalValue();

	/**
	 * @param b
	 */
	void setEnableEdit(boolean b);

	/**
	 * @param deploymentConfigurations
	 */
	void setDeploymentConfigurations(List<DeploymentConfiguration> deploymentConfigurations);

	/**
	 * @param serviceTypes
	 */
	void setServiceTypes(List<DeploymentServiceType> serviceTypes);

	/**
	 * @param deploymentConfiguration
	 */
	void setSelectedConfig(DeploymentConfiguration deploymentConfiguration);

	/**
	 * @param buildJobNames
	 */
	void setJobNames(List<String> buildJobNames);

	/**
	 * @param result
	 */
	void setConfigOptions(DeploymentConfigOptionsResult result);

	/**
	 * @return
	 */
	DeploymentConfiguration getValue();
}
