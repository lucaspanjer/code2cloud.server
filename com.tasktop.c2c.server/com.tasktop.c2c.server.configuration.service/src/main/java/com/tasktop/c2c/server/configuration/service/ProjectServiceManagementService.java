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

/**
 * Internal service exposed to manage a project's service from the hub. Intended to be a general interface that each
 * service type can implement.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceManagementService {

	/**
	 * Check the status of the management service. Useful to check the status of service hosts.
	 * 
	 * @return
	 */
	ServiceHostStatus retrieveServiceHostStaus();

	/**
	 * Provision a new project service.
	 * 
	 * @param configuration
	 */
	void provisionService(ProjectServiceConfiguration configuration);

	/**
	 * Get the status of a service. WARNING. This method assumes a correct tenancy context. (provision, deprovision do
	 * not). FIXME
	 * 
	 * @param projectIdentifer
	 * @return
	 */
	ProjectServiceStatus retrieveServiceStatus(String projectIdentifer);

	/**
	 * De-provision (delete) a project service.
	 * 
	 * @param projectIdentifier
	 * */
	void deprovisionService(ProjectServiceConfiguration configuration);

}
