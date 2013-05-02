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
package com.tasktop.c2c.server.profile.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class HttpForwardManager {

	private static final String FORWARD_HEADER = "X-Forwarded-For";
	private static final String LOCALHOST_ADDRESS = "127.0.0.1";

	@Value("${alm.auth.trustedHost.alwaysTrustLocalhost}")
	private boolean alwaysTrustLocalhost = true;

	@Autowired
	private ProjectServiceService projectServiceService;

	/**
	 * Return the list of remote addresses in the "connection chain". This will start with the immediate connection
	 * (request.getRemoteAddr()), and the capture each subsequent forward (represented in the X-Forwarded-For header.
	 * 
	 * @param request
	 * @return
	 */
	public List<String> computeForwardChain(HttpServletRequest request) {
		// List of forwards, first is most recent.
		List<String> forwards = new ArrayList<String>();
		forwards.add(request.getRemoteAddr());
		String forwardedFors = request.getHeader(FORWARD_HEADER);
		if (forwardedFors != null) {
			String[] forwardHosts = forwardedFors.split(",");
			for (int i = forwardHosts.length - 1; i >= 0; i--) {
				forwards.add(forwardHosts[i].trim());
			}
		}
		return forwards;
	}

	public boolean isTrustedForward(String hostAddr) {
		if (hostAddr.equals(LOCALHOST_ADDRESS) && alwaysTrustLocalhost) {
			return true;
		}
		List<ServiceHost> hosts = projectServiceService.findHostsForAddress(hostAddr);
		for (ServiceHost host : hosts) {
			if (host.getServiceHostConfiguration().getSupportedServices().contains(ServiceType.TRUSTED_PROXY)) {
				return true;
			}
		}
		return false;
	}

}
