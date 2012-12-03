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

import java.util.List;

import com.tasktop.c2c.server.profile.domain.activity.ProjectActivity;
import com.tasktop.c2c.server.profile.domain.activity.ProjectDashboard;

/**
 * Service for retrieving information about recent activity for a project.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface ActivityService {

	// Keep this synchronized with the OSGI version in MANIFEST.MF
	public static final String VERSION = "1.1.0";

	List<ProjectActivity> getRecentActivity(String projectIdentifier);

	List<ProjectActivity> getShortActivityList(final String projectIdentifier);

	/**
	 * @param projectIdentifier
	 * @return
	 */
	ProjectDashboard getDashboard(String projectIdentifier);

}
