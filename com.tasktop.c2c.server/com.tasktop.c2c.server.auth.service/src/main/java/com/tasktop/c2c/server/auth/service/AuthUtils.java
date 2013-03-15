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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

	private static final String ORG_PREFIX = "ORG_";

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

	public static void addRolesToAuth(String... roles) {
		Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

		if (currentAuth == null) {
			return;
		}

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(currentAuth.getAuthorities());
		authorities.addAll(toGrantedAuthorities(Arrays.asList(roles)));

		// Create a new authToken for this information
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				currentAuth.getPrincipal(), currentAuth.getCredentials(), authorities);

		if (currentAuth.getDetails() instanceof AuthenticationToken) {
			// Dump these updated roles into our token.
			((AuthenticationToken) currentAuth.getDetails()).setAuthorities(authorities);
		}
		if (currentAuth.getPrincipal() instanceof AuthenticationServiceUser) {
			((AuthenticationServiceUser) currentAuth.getPrincipal()).getToken().setAuthorities(authorities);
		}

		authentication.setDetails(currentAuth.getDetails());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * @return
	 */
	public static AuthenticationToken getAuthToken() {
		Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

		if (currentAuth == null) {
			throw new IllegalStateException("No current auth");
		}

		if (currentAuth.getDetails() instanceof AuthenticationToken) {
			return (AuthenticationToken) currentAuth.getDetails();
		}
		if (currentAuth.getPrincipal() instanceof AuthenticationServiceUser) {
			return ((AuthenticationServiceUser) currentAuth.getPrincipal()).getToken();
		}
		return null;
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

	public static String toCompoundProjectRole(String roleName, String projectIdentifier) {
		if (roleName == null || roleName.contains("/")) {
			throw new IllegalArgumentException();
		}
		return String.format("%s/%s", roleName, projectIdentifier);
	}

	public static String fromCompoundProjectRole(String compoundRole, String projectIdentifier) {
		List<String> retList = fromCompoundProjectRoles(Collections.singletonList(compoundRole), projectIdentifier);
		String retStr = null;

		if (retList.size() > 0) {
			retStr = retList.get(0);
		}

		return retStr;
	}

	public static List<String> fromCompoundProjectRoles(List<String> compoundRoles, String projectIdentifier) {
		Pattern rolePattern = computeProjectRolePattern(projectIdentifier);
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

	private static Pattern computeProjectRolePattern(String projectIdentifier) {
		return Pattern.compile("([^/]+)/" + Pattern.quote(projectIdentifier));
	}

	public static String toCompoundOrganizationRole(String roleName, String organizationIdentifier) {
		if (roleName == null || roleName.contains("/")) {
			throw new IllegalArgumentException();
		}
		return String.format("%s/%s%s", roleName, ORG_PREFIX, organizationIdentifier);
	}

	public static String fromCompoundOrganizationRole(String compoundRole, String organizationIdentifier) {
		List<String> retList = fromCompoundOrganizationRoles(Collections.singletonList(compoundRole),
				organizationIdentifier);
		String retStr = null;

		if (retList.size() > 0) {
			retStr = retList.get(0);
		}

		return retStr;
	}

	public static List<String> fromCompoundOrganizationRoles(List<String> compoundRoles, String organizationIdentifier) {
		Pattern rolePattern = computeOrganizationRolePattern(organizationIdentifier);
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

	public static List<String> findOrganizationIdsInRoles(Collection<? extends GrantedAuthority> collection) {
		List<String> foundOrgs = new ArrayList<String>();
		for (GrantedAuthority role : collection) {
			String roleStr = role.getAuthority();
			if (roleStr.contains(ORG_PREFIX)) {
				String orgId = roleStr.substring(roleStr.indexOf(ORG_PREFIX) + ORG_PREFIX.length());
				// the org id may already be present from another composite role
				if (!foundOrgs.contains(orgId)) {
					foundOrgs.add(orgId);
				}
			}
		}
		return foundOrgs;
	}

	public static List<String> findOrganizationIdsForCurrentUser() {
		return findOrganizationIdsInRoles(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
	}

	private static Pattern computeOrganizationRolePattern(String organizationIdentifier) {
		return Pattern.compile("([^/]+)/" + Pattern.quote(ORG_PREFIX + organizationIdentifier));
	}

	private static AuthenticationToken createSystemAuthenticationToken(String projectIdentifier) {
		AuthenticationToken token = new AuthenticationToken();
		token.setUsername("SYSTEM");
		token.setKey(UUID.randomUUID().toString());
		token.getAuthorities().add(Role.System);
		token.getAuthorities().add(Role.User);
		token.getAuthorities().add(Role.Admin);
		if (projectIdentifier != null) {
			token.getAuthorities().add(AuthUtils.toCompoundProjectRole(Role.System, projectIdentifier));
			token.getAuthorities().add(AuthUtils.toCompoundProjectRole(Role.User, projectIdentifier));
			token.getAuthorities().add(AuthUtils.toCompoundProjectRole(Role.Admin, projectIdentifier));
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
