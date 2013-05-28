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

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.job.Job;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
@SuppressWarnings("serial")
public class OrganizationDeletionJob extends Job {

	private String organizationId;

	public OrganizationDeletionJob(String organizationId) {
		super();
		this.organizationId = organizationId;
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		InternalProfileService service = applicationContext.getBean(InternalProfileService.class);
		AuthUtils.assumeSystemIdentity(organizationId);
		service.doDeleteOrganizationIfReady(organizationId);
	}
}
