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
package com.tasktop.c2c.server.tasks.tests.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.auth.service.AuthenticationServiceUser;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.internal.tasks.domain.Profile;

@Ignore
public class TestSecurity {

	public static void login(Profile profile, String... roles) {
		AuthenticationToken token = createToken(profile, roles);
		login(token);
	}

	public static AuthenticationToken createToken(Profile profile, String... roleNames) {
		List<String> roles = new ArrayList<String>();
		// add the default role
		roles.add(Role.User);
		// add other roles as specified
		for (String role : roleNames) {
			roles.add(role);
		}
		AuthenticationToken token = new AuthenticationToken();
		token.setAuthorities(roles);
		token.setExpiry(new Date(System.currentTimeMillis() + 100000L));
		token.setIssued(new Date());
		token.setFirstName(profile.getRealname().split("\\s")[0]);
		token.setLastName(profile.getRealname().split("\\s")[1]);
		token.setKey(UUID.randomUUID().toString());
		token.setUsername(profile.getLoginName());
		if (token.getUsername() == null) {
			token.setUsername(profile.getLoginName());
		}
		token.setLanguage("en");
		return token;
	}

	public static void login(AuthenticationToken token) {
		List<GrantedAuthority> authorities = AuthUtils.toGrantedAuthorities(token.getAuthorities());
		AuthenticationServiceUser user = new AuthenticationServiceUser(token.getUsername(), "test123", token,
				authorities);
		Authentication authentication = new TestingAuthenticationToken(user, user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
