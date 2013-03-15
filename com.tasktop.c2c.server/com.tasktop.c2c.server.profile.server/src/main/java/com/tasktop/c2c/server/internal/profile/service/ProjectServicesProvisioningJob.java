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

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.NoNodeAvailableException;
import com.tasktop.c2c.server.common.service.job.Job;

@SuppressWarnings("serial")
public class ProjectServicesProvisioningJob extends Job {

	private Long projectServiceId;

	public ProjectServicesProvisioningJob(Long projectServiceId) {
		this.projectServiceId = projectServiceId;
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		InternalProjectServiceService service = applicationContext.getBean("projectServiceService",
				InternalProjectServiceService.class);
		try {
			service.doProvisionServices(projectServiceId);
		} catch (EntityNotFoundException e) {
			throw new IllegalStateException(e);
		} catch (ProvisioningException e) {
			throw new IllegalStateException(e);
		} catch (NoNodeAvailableException e) {
			throw new RuntimeException(e);
		}
	}

}
