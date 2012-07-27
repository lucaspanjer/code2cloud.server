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
package com.tasktop.c2c.server.common.internal.tenancy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.tenancy.web.TenantIdentificationStrategy;

import com.tasktop.c2c.server.common.service.web.HeaderConstants;
import com.tasktop.c2c.server.common.service.web.ProfileHubTenant;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class InternalTenantIdentificationStrategy implements TenantIdentificationStrategy {

	@Override
	public Object identifyTenant(HttpServletRequest request) {
		ProfileHubTenant tenant = new ProfileHubTenant();
		if (request.getHeader(HeaderConstants.PROJECT_ID_HEADER) != null) {
			tenant.setProjectIdentifier(request.getHeader(HeaderConstants.PROJECT_ID_HEADER));
		}
		if (request.getHeader(HeaderConstants.ORGANIZATION_ID_HEADER) != null) {
			tenant.setOrganizationIdentifier(request.getHeader(HeaderConstants.ORGANIZATION_ID_HEADER));
		}
		if (request.getHeader(HeaderConstants.SHORT_PROJECT_ID_HEADER) != null) {
			tenant.setShortProjectIdentifier(request.getHeader(HeaderConstants.SHORT_PROJECT_ID_HEADER));
		}

		return tenant;
	}

}
