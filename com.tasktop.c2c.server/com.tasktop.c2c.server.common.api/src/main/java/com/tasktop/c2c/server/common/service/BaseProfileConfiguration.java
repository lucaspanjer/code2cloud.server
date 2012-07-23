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

	public String getWebHost() {
		return webHost;
	}

	public void setWebHost(String webHost) {
		this.webHost = webHost;
	}

	public String getServiceProxyPath() {
		return serviceProxyPath;
	}

	public void setServiceProxyPath(String serviceProxyPath) {
		this.serviceProxyPath = serviceProxyPath;
		if (!this.serviceProxyPath.startsWith("/") && !this.serviceProxyPath.isEmpty()) {
			this.serviceProxyPath = "/" + this.serviceProxyPath;
		}
	}

	public String getBaseContextPath() {
		return baseContextPath;
	}

	public void setBaseContextPath(String baseContextPath) {
		this.baseContextPath = baseContextPath;
		if (!this.baseContextPath.startsWith("/") && !this.baseContextPath.isEmpty()) {
			this.baseContextPath = "/" + this.baseContextPath;
		}
	}

	public String getProfileBaseUrl() {
		if (prefixHostnameWithOrgId && TenancyUtil.getCurrentTenantOrganizationIdentifer() != null) {
			return profileApplicationProtocol + "://" + TenancyUtil.getCurrentTenantOrganizationIdentifer() + "."
					+ webHost + baseContextPath;
		}
		return profileApplicationProtocol + "://" + webHost + baseContextPath;
	}

	public String getUpdateSiteUrl() {
		return profileApplicationProtocol + "://" + webHost + "/updateSite";
	}

	public String getServiceUrlPrefix(String projectId) {
		return getProfileBaseUrl() + getServiceProxyPath() + "/" + projectId + "/";
	}

	public String getHostedScmUrlPrefix(String projectId) {
		return getServiceUrlPrefix(projectId) + "scm/";
	}

	public boolean isPrefixHostnameWithOrgId() {
		return prefixHostnameWithOrgId;
	}

	public void setPrefixHostnameWithOrgId(boolean prefixHostnameWithOrgId) {
		this.prefixHostnameWithOrgId = prefixHostnameWithOrgId;
	}

}
