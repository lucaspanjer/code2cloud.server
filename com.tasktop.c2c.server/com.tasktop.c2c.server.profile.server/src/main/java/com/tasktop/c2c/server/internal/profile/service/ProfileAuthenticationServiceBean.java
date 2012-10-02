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

import java.security.PublicKey;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.auth.service.AbstractAuthenticationServiceBean;
import com.tasktop.c2c.server.auth.service.AuthenticationServiceUser;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.auth.service.PublicKeyAuthenticationService;
import com.tasktop.c2c.server.common.service.AuthenticationException;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.SshPublicKey;

@Qualifier("main")
@Service("authenticationService")
public class ProfileAuthenticationServiceBean extends AbstractAuthenticationServiceBean<Profile> implements
		UserDetailsService, PublicKeyAuthenticationService {

	@Autowired
	protected IdentityManagmentService identityManagmentService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		Profile profile;
		try {
			profile = identityManagmentService.getProfileByUsername(username);
			AuthenticationToken token = createAuthenticationToken(username, profile);
			return AuthenticationServiceUser.fromAuthenticationToken(token, profile.getPassword());
		} catch (EntityNotFoundException e) {
			throw new UsernameNotFoundException(username);
		}
	}

	@Override
	protected Profile validateCredentials(String username, String password) throws AuthenticationException {
		return identityManagmentService.validateCredentials(username, password);
	}

	@Override
	protected void configureToken(Profile profile, AuthenticationToken token) {
		token.setFirstName(profile.getFirstName());
		token.setLastName(profile.getLastName());
	}

	@Override
	protected List<String> computeAuthorities(Profile profile) {
		return identityManagmentService.computeAuthorities(profile);
	}

	@Override
	public AuthenticationToken authenticate(String username, PublicKey publicKey) throws AuthenticationException {

		try {
			Profile profile = identityManagmentService.getProfileByUsername(username);

			for (SshPublicKey sshPublicKey : profile.getSshPublicKeys()) {
				if (sshPublicKey.isSameAs(publicKey)) {
					return createAuthenticationToken(username, profile);
				}
			}
			throw new AuthenticationException();
		} catch (EntityNotFoundException e) {
			throw new UsernameNotFoundException(username);
		}
	}
}
