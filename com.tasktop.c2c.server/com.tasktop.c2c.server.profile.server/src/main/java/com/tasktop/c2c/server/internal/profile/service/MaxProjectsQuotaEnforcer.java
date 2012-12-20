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
package com.tasktop.c2c.server.internal.profile.service;


import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.domain.Quota;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.profile.domain.internal.Organization;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.QuotaSetting;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.QuotaEnforcer;

/**
 * Enforce a max project per organization quota.
 * 
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
@Component
public class MaxProjectsQuotaEnforcer implements QuotaEnforcer<Project> {

	@Inject
	private ProfileService profileService;

	@Override
	public String getQuotaName() {
		return Quota.MAX_PROJECTS_QUOTA_NAME;
	}

	@Override
	public Class<Project> getObjectClass() {
		return Project.class;
	}

	@Override
	public void enforceQuota(QuotaSetting quota, Project project) throws ValidationException {
		String orgId = TenancyUtil.getCurrentTenantOrganizationIdentifer();
		if (orgId == null) {
			return;
		}
		Organization org;
		try {
			org = profileService.getOrganizationByIdentfier(orgId);
		} catch (EntityNotFoundException e) {
			throw new IllegalStateException();
		}

		if (org == null) {
			throw new IllegalStateException();
		}

		Integer maxProjects = Integer.parseInt(quota.getValue());

		if (org.getProjects().size() >= maxProjects) {
			throw new ValidationException("Maximum number of Projects (" + quota.getValue() + ") already reached.",
					null); // TODO Internationalize this
		}
	}
}
