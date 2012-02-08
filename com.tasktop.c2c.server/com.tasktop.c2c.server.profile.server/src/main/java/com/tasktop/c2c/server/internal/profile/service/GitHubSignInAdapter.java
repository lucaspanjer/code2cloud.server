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
package com.tasktop.c2c.server.internal.profile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.auth.service.AuthenticationServiceUser;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;

/**
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
@Service("signInAdaptor")
public class GitHubSignInAdapter implements SignInAdapter {

	@Autowired
	private UsersConnectionRepository usersConnRepo;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public String signIn(String username, Connection<?> connection, NativeWebRequest request) {
		// Pull in this user's information
		AuthenticationServiceUser user = (AuthenticationServiceUser) userDetailsService.loadUserByUsername(username);

		// Get a GitHub connection for this user.
		ConnectionRepository connRepo = this.usersConnRepo.createConnectionRepository(username);
		Connection<GitHub> apiConn = connRepo.findPrimaryConnection(GitHub.class);

		if (apiConn == null) {
			// No connection exists right now - that shouldn't happen since this is called from the GitHub connector
			// code. Something's fishy - throw an exception to terminate this call.
			throw new IllegalStateException("No GitHub connection exists for the given user!");
		}

		// Get our connection data so that we can populate the authentication token.
		ConnectionData connData = apiConn.createData();

		// Create a token for this user and push it into our security context, using the access token as the
		// credentials.
		AuthenticationToken token = user.getToken();
		AuthUtils.insertNewAuthToken(user, connData.getAccessToken(), token.getAuthorities(), token);
		return null;
	}

}
