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
package com.tasktop.c2c.server.common.service.domain;

public final class Role {

	private Role() {
		// no instantiation of this class.
	}

	/**
	 * A user who is not authenticated.
	 */
	public static final String Anonymous = "ROLE_ANONYMOUS";

	/**
	 * A role used for user that executes system actions.
	 */
	public static final String System = "ROLE_SYSTEM";

	/**
	 * An admin user.
	 */
	public static final String Admin = "ROLE_ADMIN";

	/**
	 * An authenticated user that is not necessarily a member of a project, but has access. (e.g., a watcher.)
	 */
	public static final String Community = "ROLE_COMMUNITY";

	/**
	 * An authenticated user that has read-only access to a project.
	 */
	public static final String Observer = "ROLE_OBSERVER";

	/**
	 * An authenticated user. In project context, an authenticated user who is a member of the project.
	 */
	public static final String User = "ROLE_USER";

	/**
	 * This is a User who has pending legal agreements. As such the permissions are restricted to just agreeing to
	 * agreements.
	 */
	public static final String UserWithPendingAgreements = "ROLE_USER_PENDING_AGREEMENTS";
}
