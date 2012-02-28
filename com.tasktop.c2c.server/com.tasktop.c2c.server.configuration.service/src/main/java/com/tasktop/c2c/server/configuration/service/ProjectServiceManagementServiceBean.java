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
package com.tasktop.c2c.server.configuration.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus.ServiceState;
import com.tasktop.c2c.server.cloud.domain.ServiceType;

public class ProjectServiceManagementServiceBean implements ProjectServiceManagementService {

	public interface Configurator {
		void configure(ProjectServiceConfiguration configuration);
	}

	public interface MetricCollector {
		void collect(ProjectServiceStatus status);
	}

	private List<Configurator> configurators;
	private List<MetricCollector> metricCollectors = Collections.emptyList();

	@Override
	public void provisionService(ProjectServiceConfiguration configuration) {
		initializeConfiguration(configuration);

		for (String requiredProperty : ProjectServiceConfiguration.REQUIRED_PROPERTIES) {
			if (!configuration.getProperties().containsKey(requiredProperty)) {
				throw new IllegalArgumentException("Missing required property: " + requiredProperty);
			}
		}

		for (Configurator configurator : configurators) {
			configurator.configure(configuration);
		}

	}

	private void initializeConfiguration(ProjectServiceConfiguration config) {
		if (config.getProperties() == null) {
			config.setProperties(new HashMap<String, String>());
		}
		config.getProperties().put(ProjectServiceConfiguration.APPLICATION_GIT_PROPERTY,
				config.getProjectIdentifier() + ".git");
		config.getProperties().put(ProjectServiceConfiguration.APPLICATION_ID, config.getProjectIdentifier());
		String servicePath = config.getProperties().get(ProjectServiceConfiguration.PROFILE_BASE_SERVICE_URL);
		config.getProperties().put(ProjectServiceConfiguration.APPLICATION_GIT_URL,
				servicePath + "scm/" + config.getProjectIdentifier() + ".git");
	}

	@Override
	public ProjectServiceStatus retrieveServiceStatus(String projectIdentifer, ServiceType serviceType) {
		ProjectServiceStatus result = new ProjectServiceStatus();
		result.setProjectIdentifier(projectIdentifer);
		result.setServiceType(serviceType);
		result.setMetrics(new HashMap<String, String>());
		result.setServiceState(ServiceState.UNKNOWN);

		for (MetricCollector mc : metricCollectors) {
			mc.collect(result);
		}

		return result;
	}

	public void setConfigurators(List<Configurator> configurators) {
		this.configurators = configurators;
	}

	public void setMetricCollectors(List<MetricCollector> metricCollectors) {
		this.metricCollectors = metricCollectors;
	}

}
