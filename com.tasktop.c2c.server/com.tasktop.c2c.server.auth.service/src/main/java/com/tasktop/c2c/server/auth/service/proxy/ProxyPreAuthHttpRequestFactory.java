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
package com.tasktop.c2c.server.auth.service.proxy;

import org.apache.commons.httpclient.HttpMethodBase;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.common.service.web.HeaderConstants;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;

public class ProxyPreAuthHttpRequestFactory extends CommonsClientHttpRequestFactory {

	private AuthenticationTokenSerializer tokenSerializer = new AuthenticationTokenSerializer();

	@Override
	protected void postProcessCommonsHttpMethod(final HttpMethodBase httpMethod) {
		final AuthenticationToken authenticationToken = ProxyClient.getAuthenticationToken();
		if (authenticationToken != null) {
			tokenSerializer.serialize(new RequestHeaders() {
				@Override
				public void addHeader(String name, String value) {
					httpMethod.addRequestHeader(name, value);
				}
			}, authenticationToken);
		}

		// Also setup the tenancy context header
		if (TenancyUtil.getCurrentTenantProjectIdentifer() != null) {
			httpMethod.addRequestHeader(HeaderConstants.PROJECT_ID_HEADER, TenancyUtil.getCurrentTenantProjectIdentifer());
		}
	}
}
