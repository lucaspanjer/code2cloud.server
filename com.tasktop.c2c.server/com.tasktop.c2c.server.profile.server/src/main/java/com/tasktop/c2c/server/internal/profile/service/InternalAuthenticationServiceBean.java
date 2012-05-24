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
import com.tasktop.c2c.server.profile.domain.internal.OrganizationProfile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.service.InternalAuthenticationService;

@Service("internalAuthenticationService")
public class InternalAuthenticationServiceBean implements InternalAuthenticationService {

	@Override
	public AuthenticationToken specializeAuthenticationToken(AuthenticationToken originalToken, Project project) {

		AuthenticationToken token;

		// If we have a public project, we want to pass along a token which indicates that the observer role is present.
		if (originalToken == null) {
			// This is an anonymous user, so create a new token for them.
			token = new AuthenticationToken();

			// Plug in our anonymous role, since this is an unauthenticated user.
			token.getAuthorities().add(Role.Anonymous);
		} else {
			token = originalToken.clone();
			token.setAuthorities(AuthUtils.fromCompoundProjectRoles(token.getAuthorities(), project.getIdentifier()));
		}

		if (project.getAccessibility() != null) {

			switch (project.getAccessibility()) {
			case PUBLIC:
				token.getAuthorities().add(Role.Observer);
				if (originalToken != null) {
					token.getAuthorities().add(Role.Community);
				}
				break;
			case ORGANIZATION_PRIVATE:
				if (userIsMemberOfProjectOrg(token.getUsername(), project)) {
					token.getAuthorities().add(Role.Community);
					token.getAuthorities().add(Role.Observer);
				}
			case PRIVATE:
				// nothing
			default:
			}
		}

		return token;
	}

	private boolean userIsMemberOfProjectOrg(String username, Project project) {
		if (username == null) {
			return false;
		}

		if (project.getOrganization() == null) {
			return false;
		}

		for (OrganizationProfile op : project.getOrganization().getOrganizationProfiles()) {
			if (op.getProfile().getUsername().equals(username)) {
				return true;
			}
		}

		return false;
	}

}
