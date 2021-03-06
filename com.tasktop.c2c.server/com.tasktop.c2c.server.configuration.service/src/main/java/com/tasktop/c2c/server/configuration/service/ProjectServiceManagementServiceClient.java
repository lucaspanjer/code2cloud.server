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

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.common.service.web.AbstractRestServiceClient;

public class ProjectServiceManagementServiceClient extends AbstractRestServiceClient implements
		ProjectServiceManagementService {

	@SuppressWarnings("unused")
	// All of the setters in this method are used programmatically by the JSON serializer.
	private static class ServiceCallResult {
		private ProjectServiceStatus projectServiceStatus;
		private ServiceHostStatus serviceHostStatus;

		public ProjectServiceStatus getProjectServiceStatus() {
			return projectServiceStatus;
		}

		public void setProjectServiceStatus(ProjectServiceStatus projectServiceStatus) {
			this.projectServiceStatus = projectServiceStatus;
		}

		public ServiceHostStatus getServiceHostStatus() {
			return serviceHostStatus;
		}

		public void setServiceHostStatus(ServiceHostStatus serviceHostStatus) {
			this.serviceHostStatus = serviceHostStatus;
		}
	}

	@Override
	public void provisionService(ProjectServiceConfiguration configuration) {
		template.postForObject(computeUrl("provision"), configuration, Void.class);
	}

	@Override
	public ProjectServiceStatus retrieveServiceStatus(String projectIdentifer) {
		return template.getForObject(computeUrl("status/{projectId}"), ServiceCallResult.class, projectIdentifer)
				.getProjectServiceStatus();
	}

	/**
	 * @param identifier
	 */
	public void deprovisionService(ProjectServiceConfiguration configuration) {
		template.postForObject(computeUrl("deprovision"), configuration, Void.class);
	}

	@Override
	public ServiceHostStatus retrieveServiceHostStaus() {
		return template.getForObject(computeUrl("status"), ServiceCallResult.class).getServiceHostStatus();
	}
}
