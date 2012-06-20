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

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.preauth.AbstractPreAuthenticatedProcessingFilter;

public class ProxyPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

	private AuthenticationTokenSerializer tokenSerializer = new AuthenticationTokenSerializer();

	public ProxyPreAuthenticatedProcessingFilter() {
		// super.setCheckForPrincipalChanges(true); //XXXXXXX - was used in spring3 config
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		return tokenSerializer.deserialize(request);
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		// don't actually need credentials, but must return non-null string
		return request.getRemoteAddr();
	}

	// This is not used if the after, before, or position elements are specified in the applicationContext.xml
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.ui.SpringSecurityFilter#getOrder()
	 */
	@Override
	public int getOrder() {
		return FilterChainOrder.PRE_AUTH_FILTER;
	}

}
