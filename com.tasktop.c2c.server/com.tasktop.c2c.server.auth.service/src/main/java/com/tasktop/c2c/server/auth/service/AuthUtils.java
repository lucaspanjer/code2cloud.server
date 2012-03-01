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
package com.tasktop.c2c.server.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.tasktop.c2c.server.common.service.domain.Role;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public final class AuthUtils {

	// No instantiation of this class
	private AuthUtils() {

	}

	// Ensure that auth tokens are inserted and handled in a consistent manner.
	public static void insertNewAuthToken(Object principal, Object credentials, List<String> authorities, Object details) {

		// Calculate our set of authorities
		Collection<GrantedAuthority> authList = toGrantedAuthorities(authorities);

		// Create a new authToken for this information
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
				credentials, authList);

		// Ensure that our token is saved as Details
		authentication.setDetails(details);

		// Push this into the Spring Security context.
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public static List<GrantedAuthority> toGrantedAuthorities(Collection<String> roles) {
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		if (roles != null) {
			for (String roleName : roles) {
				authorities.add(new SimpleGrantedAuthority(roleName));
			}
		}

		return authorities;
	}

	public static List<String> toAuthorityStrings(Collection<? extends GrantedAuthority> roles) {
		ArrayList<String> authorities = new ArrayList<String>();
		if (roles != null) {
			for (GrantedAuthority role : roles) {
				authorities.add(role.getAuthority());
			}
		}
		return authorities;
	}

	public static String toCompoundRole(String roleName, String projectIdentifier) {
		if (roleName == null || roleName.contains("/")) {
			throw new IllegalArgumentException();
		}
		return String.format("%s/%s", roleName, projectIdentifier);
	}

	public static String fromCompoundRole(String compoundRole, String projectIdentifier) {
		List<String> retList = fromCompoundRole(Collections.singletonList(compoundRole), projectIdentifier);
		String retStr = null;

		if (retList.size() > 0) {
			retStr = retList.get(0);
		}

		return retStr;
	}

	public static List<String> fromCompoundRole(List<String> compoundRoles, String projectIdentifier) {
		Pattern rolePattern = computeRolePattern(projectIdentifier);
		List<String> roleNames = new ArrayList<String>(compoundRoles.size());
		for (String authority : compoundRoles) {
			Matcher matcher = rolePattern.matcher(authority);
			if (matcher.matches()) {
				String role = matcher.group(1);
				roleNames.add(role);
			}
		}
		return roleNames;
	}

	private static Pattern computeRolePattern(String projectIdentifier) {
		return Pattern.compile("([^/]+)/" + Pattern.quote(projectIdentifier));
	}

	private static AuthenticationToken createSystemAuthenticationToken(String projectIdentifier) {
		AuthenticationToken token = new AuthenticationToken();
		token.setUsername("SYSTEM");
		token.setKey(UUID.randomUUID().toString());
		token.getAuthorities().add(Role.System);
		token.getAuthorities().add(Role.User);
		if (projectIdentifier != null) {
			token.getAuthorities().add(AuthUtils.toCompoundRole(Role.System, projectIdentifier));
			token.getAuthorities().add(AuthUtils.toCompoundRole(Role.User, projectIdentifier));
		}
		return token;
	}

	/**
	 * Replace the current security context with one authorized for Role.User and Role.System access for the given
	 * project.
	 * 
	 * @param projectIdentifier
	 */
	public static void assumeSystemIdentity(String projectIdentifier) {
		AuthenticationToken authenticationToken = createSystemAuthenticationToken(projectIdentifier);
		AuthenticationServiceUser user = AuthenticationServiceUser.fromAuthenticationToken(authenticationToken);
		PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(user,
				authenticationToken, user.getAuthorities());
		authentication.setAuthenticated(true);
		SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
