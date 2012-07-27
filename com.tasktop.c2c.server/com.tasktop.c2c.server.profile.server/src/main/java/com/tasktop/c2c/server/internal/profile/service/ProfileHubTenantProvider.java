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

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.tenancy.core.Tenant;
import org.springframework.tenancy.provider.TenantProvider;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.Security;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.web.ProfileHubTenant;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.service.ProfileService;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class ProfileHubTenantProvider implements TenantProvider {

	private final Logger LOG = LoggerFactory.getLogger(ProfileHubTenantProvider.class.getName());

	@Autowired
	ProfileService profileService;

	@Override
	public Tenant findTenant(Object identityObj) {
		String projectId = identityObj.toString().toLowerCase();

		ProfileHubTenant tenant = createNewTenant();
		tenant.setIdentity(projectId);
		tenant.setProjectIdentifier(projectId);

		fillTenant(tenant);

		return tenant;
	}

	private void fillTenant(final ProfileHubTenant tenant) {

		try {
			if (tenant.getProjectIdentifier() != null) {
				Security.callWithRoles(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						Project proj = profileService.getProjectByIdentifier(tenant.getProjectIdentifier());
						if (proj.getOrganization() != null) {
							tenant.setOrganizationIdentifier(proj.getOrganization().getIdentifier());
						}
						tenant.setShortProjectIdentifier(proj.getShortIdentifier());
						return null;

					}

				}, AuthUtils.toCompoundProjectRole(Role.User, tenant.getProjectIdentifier()));

			}

		} catch (Exception e) {
			// ignore, tenancy context will not be setup properly, but this is to be expected
			LOG.debug("Error computing tenancycontext", e);
		}
	}

	protected ProfileHubTenant createNewTenant() {
		return new ProfileHubTenant();
	}

}
