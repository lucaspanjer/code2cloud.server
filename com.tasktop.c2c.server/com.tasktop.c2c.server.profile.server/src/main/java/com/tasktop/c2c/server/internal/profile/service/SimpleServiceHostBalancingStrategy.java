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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class SimpleServiceHostBalancingStrategy implements ServiceHostBalancingStrategy {

	@Autowired
	private ProjectServiceMigrationStrategy migrationStrategy;

	@Override
	public void balance(ServiceHost ontoHost, List<ServiceHost> otherHosts) {
		int totalServices = ontoHost.getProjectServices().size();
		int downHosts = 0;
		int servicesOnDownHosts = 0;
		for (ServiceHost otherHost : otherHosts) {
			totalServices += otherHost.getProjectServices().size();
			if (!otherHost.isAvailable()) {
				downHosts++;
				servicesOnDownHosts += otherHost.getProjectServices().size();
			}
		}

		final int availableHosts = 1 + otherHosts.size() - downHosts;
		final int averageServicesPerAvailableHost = totalServices / availableHosts;

		for (ServiceHost otherHost : otherHosts) {
			final int numServicesToTakeFromThisHost;
			if (otherHost.isAvailable()) {
				numServicesToTakeFromThisHost = Math.max(0, otherHost.getProjectServices().size()
						- averageServicesPerAvailableHost);
			} else {
				numServicesToTakeFromThisHost = otherHost.getProjectServices().size();
			}

			int migrated = 0;
			for (ProjectService service : new ArrayList<ProjectService>(otherHost.getProjectServices())) {
				if (migrated >= numServicesToTakeFromThisHost) {
					break;
				}
				migrationStrategy.migrate(service, ontoHost);
				migrated++;
			}
		}
	}

	public void setMigrationStrategy(ProjectServiceMigrationStrategy migrationStrategy) {
		this.migrationStrategy = migrationStrategy;
	}

}
