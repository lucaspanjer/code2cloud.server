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
package com.tasktop.c2c.server.profile.web.ui.server;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.web.server.AbstractAutowiredRemoteServiceServlet;
import com.tasktop.c2c.server.common.web.shared.NoSuchEntityException;
import com.tasktop.c2c.server.common.web.shared.ValidationFailedException;
import com.tasktop.c2c.server.deployment.domain.CloudService;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentStatus;
import com.tasktop.c2c.server.deployment.service.DeploymentConfigurationService;
import com.tasktop.c2c.server.deployment.service.ServiceException;
import com.tasktop.c2c.server.profile.service.provider.HudsonServiceProvider;
import com.tasktop.c2c.server.profile.web.ui.client.DeploymentService;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsAction;
import com.tasktop.c2c.server.profile.web.ui.server.action.GetProjectBuildInformationActionHandler;

@SuppressWarnings("serial")
public class DeploymentServiceImpl extends AbstractAutowiredRemoteServiceServlet implements DeploymentService {

	@Autowired
	private DeploymentConfigurationService deploymentConfigurationService;

	@Autowired
	private HudsonServiceProvider hudsonServiceProvider;

	@Autowired
	private GetProjectBuildInformationActionHandler getProjectBuildInformationActionHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.web.ui.client.DeploymentService#createDeploymentConfigurations(java.lang
	 * .String, com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration)
	 */
	@Override
	public DeploymentConfiguration createDeploymentConfiguration(String projectId, DeploymentConfiguration configuration)
			throws ValidationFailedException {
		setTenancyContext(projectId);
		try {
			return deploymentConfigurationService.createDeployment(configuration);
		} catch (ValidationException e) {
			handle(e);
		}
		return null; // FIXME

	}

	@Override
	public Boolean validateCredentials(DeploymentConfiguration configuration) {
		try {
			return deploymentConfigurationService.validateCredentials(configuration.getApiBaseUrl(),
					configuration.getUsername(), configuration.getPassword());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public DeploymentConfiguration updateDeployment(DeploymentConfiguration configuration)
			throws NoSuchEntityException, ValidationFailedException {

		try {
			return deploymentConfigurationService.updateDeployment(configuration);
		} catch (EntityNotFoundException e) {
			handle(e);
		} catch (ValidationException e) {
			handle(e);
		}
		return null; // FIXME
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.web.ui.client.DeploymentService#getDeploymentConfigurationOptions(com.tasktop
	 * . alm.server.deployment.domain.DeploymentConfiguration)
	 */
	@Override
	public DeploymentConfigurationOptions getDeploymentConfigurationOptions(String projectIdentifier,
			DeploymentConfiguration configuration) {
		try {
			DeploymentConfigurationOptions result = new DeploymentConfigurationOptions();
			result.setAvailableMemories(deploymentConfigurationService.getAvailableMemoryConfigurations(configuration));
			result.setAvailableServices(deploymentConfigurationService.getAvailableServices(configuration));
			result.setAvailableServiceConfigurations(deploymentConfigurationService
					.getAvailableServiceConfigurations(configuration));

			try {
				result.setBuildInformation(getProjectBuildInformationActionHandler.execute(new GetProjectBuildsAction(
						projectIdentifier, configuration.getBuildJobName()), null));
			} catch (Exception e) {
				e.printStackTrace(); // Continue
			}

			return result;
		} catch (ServiceException e) {
			throw new RuntimeException(); // FIXME
		}
	}

	private void handle(ServiceException exception) throws ValidationFailedException {
		throw new ValidationFailedException(Arrays.asList(exception.getMessage()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.web.ui.client.DeploymentService#deleteDeployment(com.tasktop.c2c.server
	 * .deployment .domain.DeploymentConfiguration)
	 */
	@Override
	public void deleteDeployment(DeploymentConfiguration configuration, boolean alsoDeleteFromCF)
			throws NoSuchEntityException {
		try {
			deploymentConfigurationService.deleteDeployment(configuration, alsoDeleteFromCF);
		} catch (EntityNotFoundException e) {
			handle(e);
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.web.ui.client.DeploymentService#startDeployment(com.tasktop.c2c.server
	 * .deployment .domain.DeploymentConfiguration)
	 */
	@Override
	public DeploymentStatus startDeployment(DeploymentConfiguration configuration) throws ValidationFailedException {
		try {
			return deploymentConfigurationService.startDeployment(configuration);
		} catch (ServiceException e) {
			handle(e);
			return null;
		}
	}

	@Override
	public DeploymentStatus stopDeployment(DeploymentConfiguration configuration) throws ValidationFailedException {
		try {
			return deploymentConfigurationService.stopDeployment(configuration);
		} catch (ServiceException e) {
			handle(e);
			return null;
		}
	}

	@Override
	public DeploymentStatus restartDeployment(DeploymentConfiguration configuration) throws ValidationFailedException {
		try {
			return deploymentConfigurationService.restartDeployment(configuration);
		} catch (ServiceException e) {
			handle(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.web.ui.client.DeploymentService#createService(com.tasktop.c2c.server
	 * .deployment .domain.CloudService)
	 */
	@Override
	public CloudService createService(DeploymentConfiguration configuration, CloudService service) {
		try {
			return deploymentConfigurationService.createService(service, configuration);
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		}
	}

}
