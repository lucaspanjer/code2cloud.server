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
package com.tasktop.c2c.server.common.service.web;

import org.springframework.tenancy.context.TenancyContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.tenancy.core.Tenant;

/**
 * Util class for working the the Hub/Profile's tenancy concepts.
 * 
 * @author clint.morgan (Tasktop Technologies Inc.)
 * 
 */
public final class TenancyUtil {

	public static void setOrganizationTenancyContext(String organizationIdentifier) {
		organizationIdentifier = organizationIdentifier.toLowerCase();
		ProfileHubTenant tenant = new ProfileHubTenant();
		tenant.setOrganizationIdentifier(organizationIdentifier);
		tenant.setIdentity(organizationIdentifier);
		TenancyContextHolder.createEmptyContext();
		TenancyContextHolder.getContext().setTenant(tenant);
	}

	public static void setProjectTenancyContext(String projectIdentifier) {
		projectIdentifier = projectIdentifier.toLowerCase();
		ProfileHubTenant tenant = new ProfileHubTenant();
		tenant.setProjectIdentifier(projectIdentifier);
		tenant.setIdentity(projectIdentifier);
		TenancyContextHolder.createEmptyContext();
		TenancyContextHolder.getContext().setTenant(tenant);
	}

	public static String getCurrentTenantProjectIdentifer() {
		TenancyContext tc = TenancyContextHolder.getContext();
		if (tc == null) {
			return null;
		}
		Tenant tenant = tc.getTenant();
		if (tenant == null) {
			return null;
		}
		if (tenant instanceof ProfileHubTenant) {
			return ((ProfileHubTenant) tenant).getProjectIdentifier();
		} else {
			// REVIEW just to catch missed refactor from old method
			return tenant.getIdentity() == null ? null : tenant.getIdentity().toString();
		}
	}

	public static String getCurrentTenantOrganizationIdentifer() {
		TenancyContext tc = TenancyContextHolder.getContext();
		if (tc == null) {
			return null;
		}
		Tenant tenant = tc.getTenant();
		if (tenant == null) {
			return null;
		}
		if (tenant instanceof ProfileHubTenant) {
			return ((ProfileHubTenant) tenant).getOrganizationIdentifier();
		} else {
			return null;
		}
	}

	private TenancyUtil() {

	}

	public static void clearContext() {
		TenancyContextHolder.clearContext();

	}
}
