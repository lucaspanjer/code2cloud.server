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

import com.tasktop.c2c.server.common.service.BaseProfileConfiguration;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * Simple container class for the configuration needed by profile service.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public class ProfileServiceConfiguration extends BaseProfileConfiguration {

	private Boolean invitationOnlySignUp;
	private String signupNotificationEmail;
	private String appName;

	public ProfileServiceConfiguration() {
	}

	public String getProfilePasswordResetURL(String token) {
		return getProfileBaseUrl() + "/#resetPassword/" + token;
	}

	public String getInvitationURL(String token) {
		return getProfileBaseUrl() + "/#invitation/" + token;
	}

	public String getSignUpInvitationURL(String token) {
		return getProfileBaseUrl() + "/#signup/" + token;
	}

	public String getEmailVerificationURL(String token) {
		return getProfileBaseUrl() + "/#verifyEmail/" + token;
	}

	public Boolean getInvitationOnlySignUp() {
		return invitationOnlySignUp;
	}

	public void setInvitationOnlySignUp(Boolean invitationOnlySignUp) {
		this.invitationOnlySignUp = invitationOnlySignUp;
	}

	public String getSignupNotificationEmail() {
		return signupNotificationEmail;
	}

	public void setSignupNotificationEmail(String signupNotificationEmail) {
		this.signupNotificationEmail = signupNotificationEmail;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getMainUrlForService(ProjectService projectService) {
		if (projectService.getExternalUrl() != null) {
			return projectService.getExternalUrl();
		} else {

			String projectIdentifier = projectService.getProjectServiceProfile().getProject().getIdentifier();
			switch (projectService.getType()) {
			case TASKS:
				return buildServiceProxyUrl(projectIdentifier, "tasks");
			case WIKI:
				return buildServiceProxyUrl(projectIdentifier, "wiki");
			case SCM:
				return buildServiceProxyUrl(projectIdentifier, "scm");
			case DEPLOYMENT:
				return null; // ??
			case REVIEW:
				return buildServiceProxyUrl(projectIdentifier, "gerrit");
			case BUILD:
				return buildServiceProxyUrl(projectIdentifier, "hudson");
			case MAVEN:
				return buildServiceProxyUrl(projectIdentifier, "maven");
			default:
				return null;
			}
		}
	}

	public String getWebUrlForService(ProjectService service) {
		String projectIdentifier = service.getProjectServiceProfile().getProject().getIdentifier();

		switch (service.getType()) {
		case TASKS:
			return buildWebUrl(projectIdentifier, "tasks");
		case WIKI:
			return buildWebUrl(projectIdentifier, "wiki");
		case SCM:
			return buildWebUrl(projectIdentifier, "scm");
		case DEPLOYMENT:
			return buildWebUrl(projectIdentifier, "deployments");
		case REVIEW:
		case BUILD:
			return getMainUrlForService(service);
		default:
			return null;
		}
	}

	private String buildWebUrl(String projectIdentifier, String restOfUrl) {
		return getProfileBaseUrl() + "/" + PROJECTS_WEB_PATH + "/" + projectIdentifier + "/" + restOfUrl;

	}

	private String buildServiceProxyUrl(String projectIdentifier, String restOfUrl) {
		return getProfileBaseUrl() + getServiceProxyPath() + "/" + projectIdentifier + "/" + restOfUrl + "/";
	}
}
