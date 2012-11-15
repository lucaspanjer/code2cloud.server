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
package com.tasktop.c2c.server.internal.deployment.service;

import java.io.IOException;

import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.service.DeploymentConfigurationService;
import com.tasktop.c2c.server.deployment.service.ServiceException;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface DeploymentConfigurationServiceInternal extends DeploymentConfigurationService {

	/**
	 * @param deploymentConfiguration
	 * @param deploymentService
	 * @param projectId
	 * @throws IOException
	 * @throws ServiceException
	 */
	void deployLatestArtifact(DeploymentConfiguration deploymentConfiguration, DeploymentService deploymentService,
			String projectId) throws IOException, ServiceException;

}
