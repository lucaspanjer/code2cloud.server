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
import com.tasktop.c2c.server.cloud.domain.ServiceType;

/**
 * Internal service exposed to manage a project's service.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceManagementService {

	/**
	 * Provision a new project service.
	 * 
	 * @param configuration
	 */
	void provisionService(ProjectServiceConfiguration configuration);

	/**
	 * Get the status of a service.
	 * 
	 * @param projectIdentifer
	 * @param serviceType
	 * @return
	 */
	ProjectServiceStatus retrieveServiceStatus(String projectIdentifer, ServiceType serviceType);

}
