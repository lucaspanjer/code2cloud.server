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
package com.tasktop.c2c.server.cloud.service;

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.common.service.job.Job;

public class FinishReleaseHudsonSlaveJob extends Job {

	private String projectId;
	private Long serviceHostId;

	public FinishReleaseHudsonSlaveJob(String projectId, Long serviceHostId) {
		this.serviceHostId = serviceHostId;
		this.projectId = projectId;
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		try {
			final HudsonSlavePoolServiceInternal hudsonSlavePoolServiceInternal = applicationContext.getBean(
					"hudsonSlavePoolService", HudsonSlavePoolServiceInternal.class);
			hudsonSlavePoolServiceInternal.doReleaseSlave(projectId, serviceHostId);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
