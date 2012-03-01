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

import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.profile.service.InternalAuthenticationService;

@Service("internalAuthenticationService")
public class InternalAuthenticationServiceBean implements InternalAuthenticationService {

	@Override
	public AuthenticationToken specializeAuthenticationToken(AuthenticationToken originalToken,
			String projectIdentifier, boolean projectIsPublic) {

		AuthenticationToken token = originalToken;

		// If we have a public project, we want to pass along a token which indicates that the observer role is present.
		if (token == null) {
			// This is an anonymous user, so create a new token for them.
			token = new AuthenticationToken();

			// Plug in our anonymous role, since this is an unauthenticated user.
			token.getAuthorities().add(Role.Anonymous);
		} else {
			token = originalToken.clone();
			token.setAuthorities(AuthUtils.fromCompoundRole(token.getAuthorities(), projectIdentifier));

			// we already had an authentication token - if we have a public project, add in our community role now.
			if (projectIsPublic) {
				token.getAuthorities().add(Role.Community);
			}
		}

		if (projectIsPublic) {
			// Plug in our observer role since we have a public project.
			token.getAuthorities().add(Role.Observer);
		}

		return token;
	}

}
