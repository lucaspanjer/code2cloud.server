/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.tenancy.context.TenancyContextHolder;

import com.tasktop.c2c.server.common.service.web.ProfileHubTenant;
import com.tasktop.c2c.server.common.service.web.TenancyManager;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class TenancyManagerImpl implements TenancyManager {

	@Autowired
	private ProfileHubTenantProvider profileHubTenantProvider;

	@Override
	public void establishTenancyContextFromProjectIdentifier(String projectIdentifier) {
		ProfileHubTenant tenant = profileHubTenantProvider.findTenantForProjectIdentifer(projectIdentifier);
		TenancyContextHolder.createEmptyContext();
		TenancyContextHolder.getContext().setTenant(tenant);
	}
}
