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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.tenancy.core.Tenant;
import org.springframework.tenancy.provider.TenantProvider;

import com.tasktop.c2c.server.auth.service.AuthUtils;
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
		String projectId = identityObj.toString();

		ProfileHubTenant tenant = new ProfileHubTenant();
		tenant.setIdentity(projectId);
		tenant.setProjectIdentifier(projectId);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try {
			AuthUtils.assumeSystemIdentity(projectId);
			Project project = profileService.getProjectByIdentifier(projectId);
			if (project.getOrganization() != null) {
				tenant.setOrganizationIdentifier(project.getOrganization().getIdentifier());
			}
		} catch (Throwable t) {
			LOG.warn("caught exception trying to get project, ignoring", t);
		} finally {
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		return tenant;
	}

}
