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

import java.util.List;

import com.tasktop.c2c.server.common.service.AuthenticationException;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.profile.domain.internal.Profile;

/**
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface IdentityManagmentService {

	Profile getProfileByUsername(String username) throws EntityNotFoundException;

	Profile validateCredentials(String username, String password) throws AuthenticationException;

	List<String> computeAuthorities(Profile profile);

	Profile createProfile(Profile profile);

	Profile updateProfile(Profile profile) throws EntityNotFoundException;

}