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
package com.tasktop.c2c.server.internal.profile.service;

import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.NoNodeAvailableException;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;

public interface InternalProjectServiceService {

	public void initializeProjectServiceProfileTemplate();

	public void doProvisionServices(Long projectId, ServiceType serviceType) throws EntityNotFoundException,
			ProvisioningException, NoNodeAvailableException;

	public void doDeprovisionService(Long projectServiceId) throws EntityNotFoundException;

	void checkServiceHostStatus(Long projectServiceId);

	void handleServiceHostFailure(Long serviceHostId);

	ServiceHost convertToInternal(com.tasktop.c2c.server.cloud.domain.ServiceHost serviceHost);
}
