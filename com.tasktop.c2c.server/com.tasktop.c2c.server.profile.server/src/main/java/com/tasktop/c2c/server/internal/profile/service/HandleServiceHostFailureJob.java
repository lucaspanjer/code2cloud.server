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

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.common.service.job.Job;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class HandleServiceHostFailureJob extends Job {

	private Long serviceHostId;

	public HandleServiceHostFailureJob(Long serviceHostId) {
		this.serviceHostId = serviceHostId;
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		applicationContext.getBean(InternalProjectServiceService.class).handleServiceHostFailure(serviceHostId);
	}

}
