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
package com.tasktop.c2c.server.auth.service.proxy;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.AccessDeniedException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.SpringSecurityFilter;

/**
 * @author Lucas Panjer (Tasktop Technologies Inc.)
 * 
 */
public class BasicAuthAccessDeniedExceptionHandlingFilter extends SpringSecurityFilter {

	@Override
	protected void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			SecurityContextHolder.getContext().getAuthentication();
			chain.doFilter(request, response);
		} catch (AccessDeniedException e) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.addHeader("WWW-Authenticate", "Basic realm=\"" + "Code2Cloud" + "\"");
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} finally {
			SecurityContextHolder.clearContext();
		}
	}

	// This is not used if the after, before, or position elements are specified in the applicationContext.xml
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.ui.SpringSecurityFilter#getOrder()
	 */
	@Override
	public int getOrder() {
		return FilterChainOrder.EXCEPTION_TRANSLATION_FILTER;
	}

}
