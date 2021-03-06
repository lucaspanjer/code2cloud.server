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

import java.util.HashMap;
import java.util.Map;

import com.tasktop.c2c.server.common.service.Security;
import com.tasktop.c2c.server.common.service.web.HeaderConstants;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class InternalTenancyContextHttpHeaderProvider {

	public Map<String, String> computeHeaders() {
		Map<String, String> result = new HashMap<String, String>();

		if (Security.getCurrentUser() != null) {
			String username = Security.getCurrentUser();
			result.put(HeaderConstants.USERNAME_HEADER, username);
		}

		if (TenancyUtil.getCurrentTenantProjectIdentifer() != null) {
			result.put(HeaderConstants.PROJECT_ID_HEADER, TenancyUtil.getCurrentTenantProjectIdentifer());
		}

		if (TenancyUtil.getCurrentTenantOrganizationIdentifer() != null) {
			result.put(HeaderConstants.ORGANIZATION_ID_HEADER, TenancyUtil.getCurrentTenantOrganizationIdentifer());
		}

		if (TenancyUtil.getCurrentTenantShortProjectIdentifer() != null) {
			result.put(HeaderConstants.SHORT_PROJECT_ID_HEADER, TenancyUtil.getCurrentTenantShortProjectIdentifer());
		}

		return result;
	}
}
