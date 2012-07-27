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
package com.tasktop.c2c.server.common.service.web;

import org.springframework.tenancy.core.Tenant;
import org.springframework.tenancy.provider.DefaultTenant;

/**
 * Represents a tenant of the profile/hub node. This can be an organization or a project within an organization.
 * 
 * @author clint.morgan (Tasktop Technologies Inc.)
 * 
 */
public class ProfileHubTenant extends DefaultTenant implements Tenant {

	private String projectIdentifier;
	private String organizationIdentifier;
	private String shortProjectIdentifier;

	public String getOrganizationIdentifier() {
		return organizationIdentifier;
	}

	public void setOrganizationIdentifier(String organizationIdentifier) {
		this.organizationIdentifier = organizationIdentifier;
	}

	public String getProjectIdentifier() {
		return projectIdentifier;
	}

	public void setProjectIdentifier(String projectIdentifier) {
		this.projectIdentifier = projectIdentifier;
	}

	public String getShortProjectIdentifier() {
		return shortProjectIdentifier;
	}

	public void setShortProjectIdentifier(String shrotProjectIdentifier) {
		this.shortProjectIdentifier = shrotProjectIdentifier;
	}
}
