/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.profile.web.proxy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.web.proxy.HeaderFilter;

/**
 * Responsible for re-writing the Location and Content-Location Headers to account for path changes in the reverse
 * proxy.
 * 
 * Similar to http://httpd.apache.org/docs/2.2/mod/mod_proxy.html#proxypassreverse
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class LocationHeaderFilter extends HeaderFilter {

	@Autowired
	private ProfileServiceConfiguration configuration;

	public String processResponseHeader(String headerName, String headerValue) {
		if (headerName.equalsIgnoreCase("Location") || headerName.equalsIgnoreCase("Content-Location")
				|| headerName.equalsIgnoreCase("URI")) {

			if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes)) {
				return headerValue;
			}

			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			if (request == null) {
				return headerValue;
			}

			ProjectService service = (ProjectService) request
					.getAttribute(ApplicationServiceProxyFilter.ATTR_APPLICATION_SERVICE);
			String uri = (String) request.getAttribute(ApplicationServiceProxyFilter.ATTR_APPLICATION_SERVICE_URI);

			if (service == null || uri == null) {
				return headerValue;
			}

			// headerValue = http://c2c.dev/hudson/job/foo
			// desiredValue is http://c2c.dev/alm/s/project1/hudson/job/foo

			String originalPath = request.getRequestURI(); // /alm/s/project1/hudson/job/foo
			String proxiedpath = service.computeInternalProxyPath(uri); // /hudson/job/foo

			String hostName = configuration.getProfileApplicationProtocol() + "://" + configuration.getWebHost(); // http://c2c.dev

			if (headerValue.startsWith(hostName) && originalPath.endsWith(proxiedpath)) {
				String redirectPath = headerValue.substring(hostName.length());
				String prefixToAdd = originalPath.substring(0, originalPath.indexOf(proxiedpath));

				headerValue = hostName + prefixToAdd + redirectPath;
			}

			return headerValue;
		} else {
			return super.processResponseHeader(headerName, headerValue);
		}
	}

	public void setConfiguration(ProfileServiceConfiguration configuration) {
		this.configuration = configuration;
	}
}
