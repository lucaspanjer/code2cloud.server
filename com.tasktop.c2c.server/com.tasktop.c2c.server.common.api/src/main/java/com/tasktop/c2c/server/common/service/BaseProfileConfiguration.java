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
package com.tasktop.c2c.server.common.service;

import com.tasktop.c2c.server.common.service.web.TenancyUtil;

/**
 * Simple container class for the profile configuration. Mainly about where services are publicly exposed.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class BaseProfileConfiguration {

	public static final String PROJECTS_WEB_PATH = "#projects";

	private String profileApplicationProtocol;
	private String webHost;
	private String serviceProxyPath;
	private String baseContextPath;
	private boolean prefixHostnameWithOrgId = false;

	public String getProfileApplicationProtocol() {
		return profileApplicationProtocol;
	}

	public void setProfileApplicationProtocol(String profileApplicationProtocol) {
		this.profileApplicationProtocol = profileApplicationProtocol;
	}

	public String getBaseWebHost() {
		return webHost;
	}

	public String getWebHost() {
		return getOrganizationHostPrefix() + webHost;
	}

	public void setWebHost(String webHost) {
		this.webHost = webHost;
	}

	public String getServiceProxyPath() {
		return serviceProxyPath;
	}

	public void setServiceProxyPath(String serviceProxyPath) {
		this.serviceProxyPath = prefixPathIfMissing(serviceProxyPath);
	}

	public String getBaseContextPath() {
		return baseContextPath;
	}

	public void setBaseContextPath(String baseContextPath) {
		this.baseContextPath = prefixPathIfMissing(baseContextPath);
	}

	public String getProfileBaseUrl() {
		String baseUrl = profileApplicationProtocol + "://";
		baseUrl += getWebHost() + baseContextPath;
		return baseUrl;
	}

	private String getOrganizationHostPrefix() {
		String orgHostPrefix = "";
		if (prefixHostnameWithOrgId && TenancyUtil.getCurrentTenantOrganizationIdentifer() != null) {
			orgHostPrefix += TenancyUtil.getCurrentTenantOrganizationIdentifer() + ".";
		}
		return orgHostPrefix;
	}

	public String getUpdateSiteUrl() {
		return profileApplicationProtocol + "://" + webHost + "/updateSite";
	}

	public String getServiceUrlPrefix(String projectIdentifier) {
		return getProfileBaseUrl() + getServiceProxyUrlPath(projectIdentifier);
	}

	public String getHostedScmUrlPrefix(String projectId) {
		return getProfileBaseUrl() + getHostedScmUrlPath(projectId);
	}

	public String getHostedScmUrlPath(String projectId) {
		return getServiceProxyUrlPath(projectId) + "scm/";
	}

	public String getServiceProxyUrlPath(String projectIdentifier) {
		return getServiceProxyPath() + "/" + projectIdentifier + "/";
	}

	public boolean isPrefixHostnameWithOrgId() {
		return prefixHostnameWithOrgId;
	}

	public void setPrefixHostnameWithOrgId(boolean prefixHostnameWithOrgId) {
		this.prefixHostnameWithOrgId = prefixHostnameWithOrgId;
	}

	private String prefixPathIfMissing(String path) {
		if (!path.startsWith("/") && !path.isEmpty()) {
			path = "/" + path;
		}
		return path;
	}
}
