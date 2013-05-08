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
package com.tasktop.c2c.server.internal.profile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus.ServiceState;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.cloud.service.HudsonSlavePoolServiceInternal;
import com.tasktop.c2c.server.common.service.NoNodeAvailableException;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class BuildSlaveProjectServiceManagementStrategy implements ProjectServiceManagementStrategy {

	@Autowired
	private HudsonSlavePoolServiceInternal hudsonSlavePoolServiceInternal;

	@Override
	public boolean canHandle(ProjectService service) {
		return service.getType().equals(ServiceType.BUILD_SLAVE);
	}

	@Override
	public boolean isReadyToProvision(ProjectService service) {
		throw new IllegalStateException("Should only be deprovisioning this service");
	}

	@Override
	public void provisionService(ProjectService service) throws NoNodeAvailableException, ProvisioningException {
		throw new IllegalStateException("Should only be deprovisioning this service");

	}

	@Override
	public void deprovisionService(ProjectService projectService) {
		hudsonSlavePoolServiceInternal.doReleaseSlave(projectService.getProjectServiceProfile().getProject()
				.getIdentifier(), projectService.getServiceHost().getId());

	}

	@Override
	public ProjectServiceStatus computeServiceStatus(ProjectService service) {
		ProjectServiceStatus result = new ProjectServiceStatus();
		result.setProjectIdentifier(service.getProjectServiceProfile().getProject().getIdentifier());
		result.setServiceType(service.getType());
		if (service.getServiceHost() != null && service.getServiceHost().isAvailable()) {
			result.setServiceState(ServiceState.RUNNING);
		} else {
			result.setServiceState(ServiceState.UNAVAILABLE);
		}
		return result;
	}

}
