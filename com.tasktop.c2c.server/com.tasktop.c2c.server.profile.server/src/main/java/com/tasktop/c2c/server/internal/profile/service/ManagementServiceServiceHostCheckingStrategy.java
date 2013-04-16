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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementService;
import com.tasktop.c2c.server.configuration.service.ProjectServiceMangementServiceProvider;
import com.tasktop.c2c.server.configuration.service.ServiceHostStatus;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class ManagementServiceServiceHostCheckingStrategy implements ServiceHostCheckingStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManagementServiceServiceHostCheckingStrategy.class
			.getSimpleName());

	@Autowired
	private ProjectServiceMangementServiceProvider projectServiceMangementServiceProvider;

	@Override
	public boolean canCheckServiceHost(ServiceHost serviceHost) {
		for (ServiceType type : serviceHost.getServiceHostConfiguration().getSupportedServices()) {
			if (!canCheckService(type)) {
				return false;
			}
		}
		return true;
	}

	protected boolean canCheckService(ServiceType type) {
		switch (type) {
		case BUILD:
		case MAVEN:
		case SCM:
		case TASKS:
		case WIKI:
			return true;
		default:
			return false;

		}
	}

	@Override
	public boolean checkServiceHost(ServiceHost serviceHost) {
		boolean allServicesUp = true;
		for (ServiceType typeToCheck : serviceHost.getServiceHostConfiguration().getSupportedServices()) {

			boolean serviceIsBackUp = false;
			try {
				serviceIsBackUp = checkServiceHostService(serviceHost, typeToCheck);
			} catch (Exception e) {
				LOGGER.info(String.format("Exception trying to retrieve service host [%s]'s [%s] service status",
						serviceHost.getInternalNetworkAddress(), typeToCheck), e);
			}

			if (!serviceIsBackUp) {
				allServicesUp = false;
				LOGGER.info(String.format("Service host [%s]'s [%s] service is still down",
						serviceHost.getInternalNetworkAddress(), typeToCheck));
				break;
			}
		}

		return allServicesUp;
	}

	protected boolean checkServiceHostService(ServiceHost serviceHost, ServiceType type) {
		ProjectServiceManagementService serviceMangementService = projectServiceMangementServiceProvider.getNewService(
				serviceHost.getInternalNetworkAddress(), type);

		ServiceHostStatus status = serviceMangementService.retrieveServiceHostStaus();

		switch (status.getState()) {
		case RUNNING:
			return true;
		default:
		}
		return false;
	}

}
