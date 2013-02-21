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
package com.tasktop.c2c.server.profile.service.provider;

import java.net.HttpURLConnection;
import java.util.Map.Entry;

import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.auth.service.proxy.AuthenticationTokenSerializer;
import com.tasktop.c2c.server.auth.service.proxy.RequestHeaders;
import com.tasktop.c2c.server.common.internal.tenancy.InternalTenancyContextHttpHeaderProvider;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.service.InternalAuthenticationService;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class InternalJgitProvider {

	static {
		for (TransportProtocol tp : Transport.getTransportProtocols()) {
			if (tp.getSchemes().contains("http") || tp.getSchemes().contains("https")) {
				Transport.unregister(tp);
			}
		}
		Transport.register(ProxyPreAuthTransportHttp.PROTO_PRE_AUTH_HTTP);
	}

	private static InternalJgitProvider instance;

	public static InternalJgitProvider getInstance() {
		return instance;
	}

	@Autowired
	private InternalAuthenticationService internalAuthenticationService;

	private AuthenticationTokenSerializer authenticationTokenSerializer = new AuthenticationTokenSerializer();

	@Autowired
	private InternalTenancyContextHttpHeaderProvider tenancySerializer;

	@Autowired
	private ProfileService profileService;

	@Autowired
	protected ProjectServiceService projectServiceService;

	public InternalJgitProvider() {
		instance = this;
	}

	void addHeaders(final HttpURLConnection connection) {
		String projectId = TenancyUtil.getCurrentTenantProjectIdentifer();
		Project project;
		try {
			project = profileService.getProjectByIdentifier(projectId);
		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e);
		}
		AuthenticationToken token = internalAuthenticationService.specializeAuthenticationToken(
				AuthUtils.getAuthToken(), project);

		// Security headers
		authenticationTokenSerializer.serialize(new RequestHeaders() {
			@Override
			public void addHeader(String name, String value) {
				connection.addRequestProperty(name, value);
			}
		}, token);

		// Also setup the tenancy context headers
		for (Entry<String, String> header : tenancySerializer.computeHeaders().entrySet()) {
			connection.addRequestProperty(header.getKey(), header.getValue());
		}
	}

	/**
	 * @param projectId
	 * @param repoName
	 * @return
	 */
	public String computeRepositoryUrl(String projectIdentifier, String repoName) {
		try {
			ProjectService appService = projectServiceService.findServiceByUri(projectIdentifier, "/scm/");
			if (appService == null) {
				throw new IllegalStateException("No services found for project " + projectIdentifier);
			}
			String baseUri = appService.getInternalBaseUri();
			if (appService.getServiceHost() == null || appService.getServiceHost().getInternalNetworkAddress() == null
					|| !appService.getServiceHost().isAvailable()) {
				throw new IllegalStateException("Service is not avaialable");
			}

			return baseUri + "/" + repoName;

		} catch (EntityNotFoundException e) {
			throw new IllegalStateException();
		}

	}

}
