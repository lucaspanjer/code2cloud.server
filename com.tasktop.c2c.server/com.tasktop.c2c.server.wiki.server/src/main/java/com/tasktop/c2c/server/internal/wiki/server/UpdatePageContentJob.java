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
package com.tasktop.c2c.server.internal.wiki.server;

import org.springframework.context.ApplicationContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.tenancy.core.Tenant;

import com.tasktop.c2c.server.common.service.job.Job;

/**
 * a job for updating PageContent
 * 
 * @see InternalWikiService#updatePageContent()
 */
public class UpdatePageContentJob extends Job {

	private static final long serialVersionUID = 1L;

	private final Tenant tenant;

	public UpdatePageContentJob() {
		this(TenancyContextHolder.getContext().getTenant());
	}

	public UpdatePageContentJob(Tenant tenant) {
		this.tenant = tenant;
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		TenancyContextHolder.createEmptyContext();
		TenancyContextHolder.getContext().setTenant(tenant);

		try {
			InternalWikiService service = applicationContext.getBean(InternalWikiService.class);
			service.updatePageContent();
		} finally {
			TenancyContextHolder.clearContext();
		}
	}

}
