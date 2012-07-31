package com.tasktop.c2c.server.internal.profile.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.tenancy.web.TenantIdentificationStrategy;

import com.tasktop.c2c.server.common.service.web.ProfileHubTenant;

/**
 * Simple strategy to establish an empty tenancy context.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DefaultProfileHubTenantIdentificationStrategy implements TenantIdentificationStrategy {

	@Override
	public Object identifyTenant(HttpServletRequest arg0) {
		return new ProfileHubTenant();
	}

}
