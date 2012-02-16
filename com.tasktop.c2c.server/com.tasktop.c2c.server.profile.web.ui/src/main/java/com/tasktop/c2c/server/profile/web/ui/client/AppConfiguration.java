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
package com.tasktop.c2c.server.profile.web.ui.client;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class AppConfiguration {

	private boolean enableGoogleAnalytics = true;
	private boolean enablePasswordManagment = true;
	private boolean enableGitHubAuth = true;

	/**
	 * @return the enableGoogleAnalytics
	 */
	public boolean isEnableGoogleAnalytics() {
		return enableGoogleAnalytics;
	}

	/**
	 * @param enableGoogleAnalytics
	 *            the enableGoogleAnalytics to set
	 */
	public void setEnableGoogleAnalytics(boolean enableGoogleAnalytics) {
		this.enableGoogleAnalytics = enableGoogleAnalytics;
	}

	/**
	 * @return the enablePasswordManagment
	 */
	public boolean isEnablePasswordManagment() {
		return enablePasswordManagment;
	}

	/**
	 * @param enablePasswordManagment
	 *            the enablePasswordManagment to set
	 */
	public void setEnablePasswordManagment(boolean enablePasswordManagment) {
		this.enablePasswordManagment = enablePasswordManagment;
	}

	/**
	 * @return the enableGitHubAuth
	 */
	public boolean isEnableGitHubAuth() {
		return enableGitHubAuth;
	}

	/**
	 * @param enableGitHubAuth
	 *            the enableGitHubAuth to set
	 */
	public void setEnableGitHubAuth(boolean enableGitHubAuth) {
		this.enableGitHubAuth = enableGitHubAuth;
	}

}
