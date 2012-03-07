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
package com.tasktop.c2c.server.profile.service;

import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.profile.domain.internal.Project;

public interface InternalAuthenticationService {

	/**
	 * "specialize" an authentication token for a specific project. All general roles will be removed. All roles related
	 * to other projects will be removed. All roles for the given project will become general (e.g, just "USER").
	 * Additionally, more roles may be added based on the project state. For example, a public project will result in a
	 * COMMUNITY role.
	 * 
	 * @param originalToken
	 *            the token to specialize
	 * @param project
	 *            the project to specialize for.
	 * @return the new token
	 */
	public AuthenticationToken specializeAuthenticationToken(AuthenticationToken originalToken, Project project);

}
