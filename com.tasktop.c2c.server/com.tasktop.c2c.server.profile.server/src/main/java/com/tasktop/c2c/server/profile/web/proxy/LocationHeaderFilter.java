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

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;

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

			String originalPath = request.getRequestURI(); // /alm/s2/project1/hudson/job/foo
			try {
				uri = UriUtils.encodePath(uri, "utf8");
				originalPath = UriUtils.encodePath(originalPath, "utf8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}

			// uri = /hudson/job/foo
			// headerValue = http://c2c.dev/s2/qa_hudson/job/foo
			// desiredValue is http://c2c.dev/alm/s2/project1/hudson/job/foo

			String hostName = configuration.getProfileApplicationProtocol() + "://" + configuration.getWebHost(); // http://c2c.dev

			if (headerValue.startsWith(hostName + service.getInternalUriPrefix()) && originalPath.contains(uri)) {
				String externalPrefix = originalPath.substring(0, originalPath.indexOf(uri)); // /alm/s2/project1

				Matcher matcher = Pattern.compile(service.getUriPattern()).matcher(uri);
				if (matcher.matches()) {
					String otherBit = matcher.group(1);
					String servicePart = uri.substring(0, uri.lastIndexOf(otherBit)); // hudson

					String restOfPath = headerValue.substring(hostName.length()
							+ service.getInternalUriPrefix().length()); // /job/foo

					headerValue = hostName + externalPrefix + servicePart + restOfPath;
				} // Else something is wrong...
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
