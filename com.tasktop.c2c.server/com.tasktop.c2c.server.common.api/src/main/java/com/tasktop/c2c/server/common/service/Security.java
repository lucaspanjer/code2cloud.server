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
package com.tasktop.c2c.server.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A utility for use with security
 */
public class Security {
	/**
	 * get the current username in the security context
	 * 
	 * @return the current user, or null if there is no current user
	 */
	public static String getCurrentUser() {
		String username = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}

		if (authentication != null) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails) {
				username = ((UserDetails) principal).getUsername();
			} else if (principal != null) {
				username = principal.toString();
			}
		}
		return username;
	}

	/**
	 * indicate if the current security context has the given role.
	 * 
	 * @param role
	 *            the role name to check
	 * @return true if and only if the security context has the given role name.
	 * 
	 * @see Authentication#getAuthorities()
	 * @see SecurityContext#getAuthentication()
	 * @see SecurityContextHolder#getContext()
	 */
	public static boolean hasRole(String role) {
		return hasOneOfRoles(role);
	}

	/**
	 * indicate if the current security context has at least one of the given roles
	 * 
	 * @param roles
	 * @return
	 */
	public static boolean hasOneOfRoles(String... roles) {
		if (roles == null || roles.length < 1) {
			throw new IllegalArgumentException();
		}
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication != null) {
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				for (String role : roles) {
					if (authority.getAuthority().equals(role)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Allow to run code with additional roles for the duration of the call.
	 * 
	 * @param callable
	 * @param roles
	 * @return callable result
	 * @throws Exception
	 */
	public static <T> T callWithRoles(Callable<T> callable, String... roles) throws Exception {
		return callAsUserWithRoles(callable, "Run-as-user", roles);
	}

	/**
	 * Allow to run code with additional roles for the duration of the call.
	 * 
	 * @param callable
	 * @param roles
	 * @return callable result
	 * @throws Exception
	 */
	public static <T> T callAsUserWithRoles(Callable<T> callable, String username, String... roles) throws Exception {
		SecurityContext securityCtxBefore = SecurityContextHolder.getContext();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(roles.length);
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));

		}
		RunAsUserToken runAsAuth = new RunAsUserToken("unused", username, null, authorities, null);

		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		SecurityContextHolder.getContext().setAuthentication(runAsAuth);
		try {
			return callable.call();
		} finally {
			SecurityContextHolder.setContext(securityCtxBefore);
		}
	}
}
