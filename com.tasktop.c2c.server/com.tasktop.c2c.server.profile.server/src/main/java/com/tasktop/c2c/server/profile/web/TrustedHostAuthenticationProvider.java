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
package com.tasktop.c2c.server.profile.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.tasktop.c2c.server.auth.service.AbstractAuthenticationServiceBean;
import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.auth.service.AuthenticationServiceUser;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.Security;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.web.HeaderConstants;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;
import com.tasktop.c2c.server.profile.web.proxy.AuthenticationProviderNotApplicableException;

// XXX Need to subclass AASB so that I can create auth tokens.
public class TrustedHostAuthenticationProvider extends AbstractAuthenticationServiceBean<AuthenticationServiceUser>
		implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrustedHostAuthenticationProvider.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProjectServiceService projectServiceService;

	@Autowired
	private HttpForwardManager httpForwardManager;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!(authentication instanceof PreAuthenticatedAuthenticationToken)) {
			throw new AuthenticationProviderNotApplicableException("Expcect a PreAutheticatedAuthToken");
		}

		PreAuthenticatedAuthenticationToken token = (PreAuthenticatedAuthenticationToken) authentication;

		if (!(token.getCredentials() instanceof HttpServletRequest)) {
			throw new AuthenticationCredentialsNotFoundException("Could not locate trusted host");
		}

		HttpServletRequest request = (HttpServletRequest) token.getCredentials();
		String remoteAddr = computeTrustedOrigin(request);

		if (remoteAddr == null) {
			throw new AuthenticationCredentialsNotFoundException("Could not locate trusted host");
		}

		List<GrantedAuthority> authorities = getTrustedHostAuthorities(remoteAddr, request);

		if (authorities.isEmpty()) {
			throw new AuthenticationCredentialsNotFoundException("No trusted host credentials");
		}

		AuthenticationServiceUser user = (AuthenticationServiceUser) token.getPrincipal();
		AuthenticationToken authToken;
		try {
			authToken = authenticate(user.getUsername(), user.getPassword());
		} catch (com.tasktop.c2c.server.common.service.AuthenticationException e) {
			throw new AuthenticationServiceException(", e");
		}
		authToken.setAuthorities(convertToStringAuthorities(authorities));
		user = new AuthenticationServiceUser(user.getUsername(), user.getPassword(), authToken, authorities);
		token = new PreAuthenticatedAuthenticationToken(user, token.getCredentials(), authorities);
		token.setAuthenticated(true);

		return token;

	}

	private String computeTrustedOrigin(HttpServletRequest request) {
		// List of forwards, first is most recent.
		List<String> forwards = httpForwardManager.computeForwardChain(request);

		for (String host : forwards) {
			if (httpForwardManager.isTrustedForward(host)) {
				continue;
			} else {
				return host;
			}
		}
		// All trusted forwards. This could happen when nodes take on multiple roles. (EG apache/hub and service on same
		// node)
		return forwards.get(forwards.size() - 1);
	}

	private List<String> convertToStringAuthorities(List<GrantedAuthority> authorities) {
		List<String> result = new ArrayList<String>(authorities.size());
		for (GrantedAuthority a : authorities) {
			result.add(a.getAuthority());
		}
		return result;
	}

	private List<GrantedAuthority> getTrustedHostAuthorities(String remoteAddr, HttpServletRequest request) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		// Should only really have multiple hosts in the local dev setup.
		List<ServiceHost> hosts = projectServiceService.findHostsForAddress(remoteAddr);

		for (ServiceHost host : hosts) {
			if (host.getServiceHostConfiguration().getSupportedServices().contains(ServiceType.BUILD_SLAVE)
					&& host.getProjectServices().size() == 1) {
				// Grant the builder access to the project its building for
				addRolesForUserLevelProjectAccess(authorities, host.getProjectServices().get(0)
						.getProjectServiceProfile().getProject().getIdentifier());
			}

			if (host.getServiceHostConfiguration().getSupportedServices().contains(ServiceType.BUILD)) {
				// Grant the master access only if it has a special header
				String projectId = request.getHeader(HeaderConstants.TRUSTED_HOST_PROJECT_ID_HEADER);

				// This happens for git cloning. The request should only be authorized for the specific project
				if (projectId != null) {
					addRolesForUserLevelProjectAccess(authorities, projectId); // For git polling, etc
					authorities.add(new SimpleGrantedAuthority(Role.System)); // for event service
				}
			}

			// Used when events are pushed to the hub. No user code runs on these nodes
			if (host.getServiceHostConfiguration().getSupportedServices().contains(ServiceType.TASKS)
					|| host.getServiceHostConfiguration().getSupportedServices().contains(ServiceType.SCM)) {
				String projectId = request.getHeader(HeaderConstants.TRUSTED_HOST_PROJECT_ID_HEADER);

				// While not strictly necessary from a security point-of-view, this check ensures we won't give out
				// roles in a local dev setup (when accessing from localhost which is also serving tasks/scm)
				if (projectId != null) {
					authorities.add(new SimpleGrantedAuthority(Role.System));
				}
			}
		}
		return authorities;

	}

	private void addRolesForUserLevelProjectAccess(final List<GrantedAuthority> authorities, final String projectId) {
		authorities.add(new SimpleGrantedAuthority(AuthUtils.toCompoundProjectRole(Role.User, projectId)));
		try {
			Security.callWithRoles(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					try {
						Project project = profileService.getProjectByIdentifier(projectId);
						if (project.getOrganization() != null) {
							authorities.add(new SimpleGrantedAuthority(AuthUtils.toCompoundOrganizationRole(Role.User,
									project.getOrganization().getIdentifier())));
						}
					} catch (EntityNotFoundException e) {
						LOGGER.warn(String.format("Project [%s] not found in request from hudson master", projectId));
					}
					return null;
				}
			}

			, Role.System);

		} catch (Exception e) {
			LOGGER.error("Unexpected exception", e);

		}
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected AuthenticationServiceUser validateCredentials(String username, String password) {
		return new AuthenticationServiceUser(username, password, null, Collections.EMPTY_LIST);
	}

}
