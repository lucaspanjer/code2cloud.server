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
package com.tasktop.c2c.server.internal.profile.service;

import com.tasktop.c2c.server.auth.service.job.SystemJob;
import com.tasktop.c2c.server.profile.domain.internal.Project;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractUpdateProjectPreferencesJob extends SystemJob {

	private static final long serialVersionUID = 1L;

	private final String value;
	private final String projectIdentifier;
	private final String key;

	public AbstractUpdateProjectPreferencesJob(Project project, String key) {
		value = project.getProjectPreferences().getWikiLanguage().toString();
		projectIdentifier = project.getIdentifier();
		this.key = key;
		setType(Type.SHORT);
	}

	public String getProjectIdentifier() {
		return projectIdentifier;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

}
