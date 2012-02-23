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

import java.util.Map;

import com.tasktop.c2c.server.cloud.domain.ServiceType;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ProjectServiceStatus {
	private String projectIdentifier;
	private ServiceType serviceType;
	private Map<String, String> metrics;

	public String getProjectIdentifier() {
		return projectIdentifier;
	}

	public void setProjectIdentifier(String projectIdentifier) {
		this.projectIdentifier = projectIdentifier;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public Map<String, String> getMetrics() {
		return metrics;
	}

	public void setMetrics(Map<String, String> metrics) {
		this.metrics = metrics;
	}
}
