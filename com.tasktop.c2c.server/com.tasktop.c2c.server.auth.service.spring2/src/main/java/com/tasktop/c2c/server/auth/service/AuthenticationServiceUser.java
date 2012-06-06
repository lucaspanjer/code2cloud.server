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

import java.util.List;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.User;

/**
 * A user that has an {@link #getToken() authentication token}.
 * 
 * @author David Green
 */
public class AuthenticationServiceUser extends User {

	private static final long serialVersionUID = 1L;

	private final AuthenticationToken token;

	/**
	 * get the user that is associated with the current security context.
	 * 
	 * @return the current user, or null if the security context has no authentication or if the current authentication
	 *         has no corresponding AuthenticationServiceUser.
	 */
	public static AuthenticationServiceUser getCurrent() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof AuthenticationServiceUser) {
				AuthenticationServiceUser user = (AuthenticationServiceUser) principal;
				return user;
			}
		}
		return null;
	}

	public static AuthenticationServiceUser fromAuthenticationToken(AuthenticationToken token) {
		return fromAuthenticationToken(token, "");
	}

	public static AuthenticationServiceUser fromAuthenticationToken(AuthenticationToken token, String userPassword) {
		List<GrantedAuthority> grantedAuthorities = AuthUtils.toGrantedAuthorities(token.getAuthorities());
		GrantedAuthority[] ga = new GrantedAuthority[grantedAuthorities.size()];
		ga = grantedAuthorities.toArray(ga);
		return new AuthenticationServiceUser(token.getUsername(), userPassword, token, ga);
	}

	public AuthenticationServiceUser(String username, String password, AuthenticationToken token,
			GrantedAuthority[] authorities) {
		super(username, password, true, true, true, true, authorities);
		this.token = token;
	}

	public AuthenticationToken getToken() {
		return token;
	}
}
