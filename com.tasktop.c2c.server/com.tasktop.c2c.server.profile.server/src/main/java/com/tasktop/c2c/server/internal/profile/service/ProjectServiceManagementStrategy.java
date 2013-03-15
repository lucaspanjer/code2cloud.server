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

import com.tasktop.c2c.server.common.service.NoNodeAvailableException;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceManagementStrategy {

	/**
	 * @param service
	 * @return
	 */
	boolean canHandle(ProjectService service);

	/**
	 * @param project
	 * @param type
	 * @return
	 */
	boolean isReadyToProvision(ProjectService service);

	/**
	 * @param service
	 * @throws NoNodeAvailableException
	 * @throws ProvisioningException
	 */
	void provisionService(ProjectService service) throws NoNodeAvailableException, ProvisioningException;

	/**
	 * @param service
	 */
	void deprovisionService(ProjectService service);
}
