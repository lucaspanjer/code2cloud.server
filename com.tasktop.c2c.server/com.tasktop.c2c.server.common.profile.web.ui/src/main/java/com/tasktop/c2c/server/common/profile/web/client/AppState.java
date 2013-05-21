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
package com.tasktop.c2c.server.common.profile.web.client;

import java.util.Map;

import com.tasktop.c2c.server.common.profile.web.shared.Credentials;
import com.tasktop.c2c.server.common.web.client.widgets.chooser.person.Person;

public class AppState {
	private Credentials credentials;
	private String afterLoginNav;
	private Boolean hasPendingAgreements;
	private Map<String, String> configurationProperties;

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public Person getSelf() {
		if (credentials == null) {
			return null;
		}
		return new Person(credentials.getProfile().getUsername(), credentials.getProfile().getFirstName() + ' '
				+ credentials.getProfile().getLastName());
	}

	public Boolean isUserAnonymous() {
		return credentials == null ? true : false;
	}

	public String getAfterLoginNav() {
		return afterLoginNav;
	}

	public void setAfterLoginNav(String mapping) {
		afterLoginNav = mapping;
	}

	public Boolean hasPendingAgreements() {
		return hasPendingAgreements;
	}

	public void setHasPendingAgreements(boolean value) {
		this.hasPendingAgreements = value;
	}

	public Map<String, String> getConfigurationProperties() {
		return configurationProperties;
	}

	public void setConfigurationProperties(Map<String, String> configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	public String getConfigProperty(String key) {
		if (configurationProperties == null) {
			return null;
		}
		return configurationProperties.get(key);
	}

}
