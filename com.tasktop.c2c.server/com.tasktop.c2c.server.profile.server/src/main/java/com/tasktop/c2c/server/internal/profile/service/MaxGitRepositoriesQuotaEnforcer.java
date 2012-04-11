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
import com.tasktop.c2c.server.profile.domain.internal.Organization;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.QuotaSetting;
import com.tasktop.c2c.server.profile.domain.internal.ScmRepository;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.QuotaEnforcer;

/**
 * Enforce a max git repositories per organization quota.
 * 
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
@Component
public class MaxGitRepositoriesQuotaEnforcer implements QuotaEnforcer<Project> {

	@Inject
	private ProfileService profileService;

	@Override
	public String getQuotaName() {
		return ProfileService.CREATE_PROJECT_QUOTA_NAME;
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

		Integer maxRepos = Integer.parseInt(quota.getValue());

		int numRepos = 0;
		for (Project p : org.getProjects()) {
			for (ScmRepository repo : p.getRepositories()) {
				numRepos++;
			}
		}

		if (numRepos >= maxRepos) {
			throw new ValidationException("Quota Violation", null);
		}

	}
}