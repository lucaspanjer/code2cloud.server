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
package com.tasktop.c2c.server.internal.profile.service.template;

import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public abstract class BaseProjectServiceCloner implements ProjectServiceCloner {

	protected ServiceType serviceType;

	protected BaseProjectServiceCloner(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public boolean canClone(ProjectService service) {
		return this.serviceType.equals(service.getType());
	}

	@Override
	public boolean isReadyToClone(ProjectService sourceService, ProjectService targetProjectService) {
		if (targetProjectService.getServiceHost() == null) {
			return false;
		}
		return true;
	}

}
