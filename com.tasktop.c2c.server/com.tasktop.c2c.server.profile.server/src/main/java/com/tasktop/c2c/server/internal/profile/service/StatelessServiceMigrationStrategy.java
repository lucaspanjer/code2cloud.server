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
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class StatelessServiceMigrationStrategy implements ProjectServiceMigrationStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatelessServiceMigrationStrategy.class
			.getSimpleName());

	@Override
	public boolean canMigrate(ProjectService service) {
		switch (service.getType()) {
		case TASKS:
		case WIKI:
			return true;
		default:
			LOGGER.info(String.format("Unable to migrate [%s][%s]. No strategy exists.", service
					.getProjectServiceProfile().getProject().getIdentifier(), service.getType()));
			return false;
		}
	}

	@Override
	public void migrate(ProjectService service, ServiceHost newHost) {
		ServiceHost oldHost = service.getServiceHost();
		oldHost.getProjectServices().remove(service);

		service.setServiceHost(newHost);
		newHost.getProjectServices().add(service);

		LOGGER.info(String.format("Migrating [%s][%s] to host at [%s]", service.getProjectServiceProfile().getProject()
				.getIdentifier(), service.getType(), newHost.getInternalNetworkAddress()));

	}

}
