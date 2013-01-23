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
			switch (type) {
			case BUILD:
			case MAVEN:
			case SCM:
			case TASKS:
			case WIKI:
				continue;
			default:
				return false;

			}
		}
		return true;
	}

	@Override
	public boolean checkServiceHost(ServiceHost serviceHost) {
		boolean allServicesUp = false;
		for (ServiceType typeToCheck : serviceHost.getServiceHostConfiguration().getSupportedServices()) {

			ProjectServiceManagementService serviceMangementService = projectServiceMangementServiceProvider
					.getNewService(serviceHost.getInternalNetworkAddress(), typeToCheck);

			boolean serviceIsBackUp;
			try {
				ServiceHostStatus status = serviceMangementService.retrieveServiceHostStaus();

				switch (status.getState()) {
				case RUNNING:
					serviceIsBackUp = true;
					break;
				default:
					serviceIsBackUp = false;
				}

			} catch (Exception e) {
				LOGGER.info(String.format("Exception trying to retrieve service host [%s]'s [%s] service status",
						serviceHost.getInternalNetworkAddress(), typeToCheck), e);
				serviceIsBackUp = false;
			}

			if (!serviceIsBackUp) {
				allServicesUp = false;
				LOGGER.info(String.format("Service host [%s]'s [%s] service is still down",
						serviceHost.getInternalNetworkAddress(), typeToCheck));
			}
		}

		return allServicesUp;

	}

}
