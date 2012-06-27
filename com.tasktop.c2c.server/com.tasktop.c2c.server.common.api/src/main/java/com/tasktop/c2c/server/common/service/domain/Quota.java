/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public final class Quota {

	private Quota() {
		// no instantiation of this class.
	}

	public static final String MAX_PROJECTS_QUOTA_NAME = "MAX_PROJECTS";
	public static final String MAX_CONCURRENT_EXECUTORS_QUOTA_NAME = "HUDSON.MAXCONCURRENTBUILDS";
	public static final String MAX_DISK_USAGE_BYTES_QUOTA_NAME = "MAX_DISK_USAGE_BYTES";
	public static final String MAX_USERS_QUOTA_NAME = "MAX_USERS";
	public static final String MAX_GIT_REPOSITORIES_QUOTA_NAME = "MAX_GIT_REPOSITORIES";
	public static final String MAX_DATA_TRANSFER_BYTES_QUOTA_NAME = "MAX_DATA_TRANSFER_BYTES";

}
