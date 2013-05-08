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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.common.service.NoNodeAvailableException;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component("projectServiceManagementStrategyList")
public class ProjectServiceManagementStrategyList implements ProjectServiceManagementStrategy {

	@Autowired
	List<ProjectServiceManagementStrategy> strategyList;

	protected ProjectServiceManagementStrategy get(ProjectService service) {
		for (ProjectServiceManagementStrategy s : strategyList) {
			if (s.canHandle(service)) {
				return s;
			}
		}
		return null;
	}

	@Override
	public boolean canHandle(ProjectService service) {
		return get(service) != null;
	}

	@Override
	public boolean isReadyToProvision(ProjectService service) {
		return get(service).isReadyToProvision(service);
	}

	@Override
	public void provisionService(ProjectService service) throws NoNodeAvailableException, ProvisioningException {
		get(service).provisionService(service);
	}

	@Override
	public void deprovisionService(ProjectService service) {
		get(service).deprovisionService(service);
	}

	@Override
	public ProjectServiceStatus computeServiceStatus(ProjectService service) {
		return get(service).computeServiceStatus(service);
	}

}
