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
package com.tasktop.c2c.server.configuration.service;

import java.util.HashMap;
import java.util.Map;

public class ProjectServiceConfiguration {

	/** Property name for the application identifier. */
	public static final String APPLICATION_ID = "profile.application.identifier";
	/** Property name for the profile protocol. */
	public static final String PROFILE_PROTOCOL = "profile.protocol";
	/** Property name for the profile hostname. */
	public static final String PROFILE_HOSTNAME = "profile.hostname";
	/** Property name for the profile base serivce url for the given project (up to the ../s/{projectId}/). */
	public static final String PROFILE_BASE_SERVICE_URL = "profile.service.path";
	/** Property name for the base profile url. */
	public static final String PROFILE_BASE_URL = "profile.url";
	/** Property Name of the git repository to be created. */
	public static final String APPLICATION_GIT_PROPERTY = "application.git.name";
	/** Property Name of the git repository to be created. */
	public static final String APPLICATION_GIT_URL = "application.git.url";
	/** Required properties to be sent when configuring a node. */
	public static final String[] REQUIRED_PROPERTIES = new String[] { ProjectServiceConfiguration.APPLICATION_ID,
			ProjectServiceConfiguration.PROFILE_PROTOCOL, ProjectServiceConfiguration.PROFILE_HOSTNAME,
			ProjectServiceConfiguration.PROFILE_BASE_SERVICE_URL, ProjectServiceConfiguration.PROFILE_BASE_URL };
	public static final String MARKUP_LANGUAGE = "markup.language";

	private String projectId;
	private Map<String, String> properties;

	public String getProjectIdentifier() {
		return projectId;
	}

	public void setProjectIdentifier(String applicationId) {
		this.projectId = applicationId;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void setProperty(String key, String value) {
		if (this.properties == null) {
			this.properties = new HashMap<String, String>();
		}
		this.properties.put(key, value);
	}

}