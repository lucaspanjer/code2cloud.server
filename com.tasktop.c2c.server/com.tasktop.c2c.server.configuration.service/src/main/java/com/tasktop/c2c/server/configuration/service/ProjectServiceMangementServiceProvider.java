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

import javax.annotation.Resource;

import org.springframework.web.client.RestTemplate;

import com.tasktop.c2c.server.cloud.domain.ServiceType;

public class ProjectServiceMangementServiceProvider {
	private RestTemplate template;

	private static final int ALM_HTTP_PORT = 8080;

	@Resource
	private Map<ServiceType, String> configPathsByServiceType;

	public ProjectServiceManagementServiceClient getNewService(String internalNetworkAddress, ServiceType type) {
		if (!configPathsByServiceType.containsKey(type)) {
			throw new IllegalStateException("Unknown service type config path: " + type);
		}

		ProjectServiceManagementServiceClient service = new ProjectServiceManagementServiceClient();
		service.setRestTemplate(template);

		String baseUrl = "http://" + internalNetworkAddress + ":" + ALM_HTTP_PORT + "/"
				+ configPathsByServiceType.get(type);
		service.setBaseUrl(baseUrl);

		return service;
	}

	public void setRestTemplate(RestTemplate template) {
		this.template = template;
	}

	public void setConfigPathsByServiceType(Map<ServiceType, String> configPathsByServiceType) {
		this.configPathsByServiceType = configPathsByServiceType;
	}

}
