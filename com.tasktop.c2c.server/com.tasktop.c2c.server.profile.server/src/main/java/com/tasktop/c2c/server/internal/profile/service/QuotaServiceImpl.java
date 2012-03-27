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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tasktop.c2c.server.common.service.AbstractJpaServiceBean;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.profile.domain.internal.QuotaSetting;
import com.tasktop.c2c.server.profile.service.QuotaEnforcer;
import com.tasktop.c2c.server.profile.service.QuotaService;

/**
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
@Service("quotaService")
@Transactional
public class QuotaServiceImpl extends AbstractJpaServiceBean implements QuotaService {

	@Autowired
	private List<QuotaEnforcer<?>> quotaEnforcers;

	@Override
	public QuotaSetting createQuota(QuotaSetting quotaSetting) throws EntityNotFoundException {
		entityManager.persist(quotaSetting);
		return quotaSetting;
	}

	@Override
	public QuotaSetting updateQuota(QuotaSetting quotaSetting) throws EntityNotFoundException {

		QuotaSetting managedQuotaSetting = entityManager.find(QuotaSetting.class, quotaSetting.getId());
		if (managedQuotaSetting == null) {
			throw new EntityNotFoundException();
		}

		if (!entityManager.contains(quotaSetting)) {
			managedQuotaSetting.setName(quotaSetting.getName());
			managedQuotaSetting.setValue(quotaSetting.getValue());
			managedQuotaSetting.setOrganization(quotaSetting.getOrganization());
			managedQuotaSetting.setProject(quotaSetting.getProject());
		}

		return managedQuotaSetting;

	}

	@Override
	public void removeQuota(QuotaSetting quotaSetting) throws EntityNotFoundException {
		QuotaSetting managedQuotaSetting = entityManager.find(QuotaSetting.class, quotaSetting.getId());
		if (managedQuotaSetting == null) {
			throw new EntityNotFoundException();
		}

		entityManager.remove(managedQuotaSetting);

	}

	@Override
	public List<QuotaSetting> findQuotaSettings(String name, String projectIdentifier, String organizationIdentifier) {
		// NOTE: Tried to do this with a single query, but tests kept failing even through the query seemed right.
		List<QuotaSetting> result = new ArrayList<QuotaSetting>(findGeneralSettings(name));

		if (projectIdentifier != null) {
			result.addAll(findProjectSettings(name, projectIdentifier));
		}
		if (organizationIdentifier != null) {
			result.addAll(findOrgSettings(name, organizationIdentifier));
		}
		return result;
	}

	private List<QuotaSetting> findGeneralSettings(String name) {
		String queryString = "SELECT q FROM " + QuotaSetting.class.getSimpleName()
				+ " q WHERE q.name = :name AND q.project IS NULL AND q.organization IS NULL";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("name", name);
		return query.getResultList();
	}

	private List<QuotaSetting> findProjectSettings(String name, String projectId) {
		String queryString = "SELECT q FROM " + QuotaSetting.class.getSimpleName()
				+ " q WHERE q.name = :name AND q.project.identifier = :projectId AND q.organization IS NULL";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("name", name);
		query.setParameter("projectId", projectId);
		return query.getResultList();
	}

	private List<QuotaSetting> findOrgSettings(String name, String orgId) {
		String queryString = "SELECT q FROM " + QuotaSetting.class.getSimpleName()
				+ " q WHERE q.name = :name AND q.project IS NULL AND q.organization.identifier = :orgId";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("name", name);
		query.setParameter("orgId", orgId);
		return query.getResultList();
	}

	@Override
	public void enforceQuota(String quotaName, Object quotaObject) throws ValidationException {
		String currentOrgId = TenancyUtil.getCurrentTenantOrganizationIdentifer();
		String currentProjectId = TenancyUtil.getCurrentTenantProjectIdentifer();

		List<QuotaSetting> quotaSettings = findQuotaSettings(quotaName, currentProjectId, currentOrgId);
		QuotaSetting quotaToCheck = getMostRelvantSetting(quotaSettings);
		if (quotaToCheck != null) {
			enforceQuota(quotaToCheck, quotaObject);
		}
	}

	protected QuotaSetting getMostRelvantSetting(List<QuotaSetting> quotaSettings) {
		if (quotaSettings == null || quotaSettings.isEmpty()) {
			return null;
		} else if (quotaSettings.size() == 1) {
			return quotaSettings.get(0);
		}

		return Collections.max(quotaSettings, new Comparator<QuotaSetting>() {

			@Override
			public int compare(QuotaSetting o1, QuotaSetting o2) {
				return score(o1) - score(o2);
			}

			private int score(QuotaSetting qs) {
				if (qs.getProject() != null) {
					return 3;
				} else if (qs.getOrganization() != null) {
					return 2;
				} else {
					return 1;
				}
			}
		});
	}

	protected void enforceQuota(QuotaSetting quota, Object quotaObject) throws ValidationException {
		for (QuotaEnforcer<?> quotaEnforcer : quotaEnforcers) {
			if (appliesToQuota(quotaEnforcer, quota.getName(), quotaObject)) {
				enforceQuota((QuotaEnforcer<Object>) quotaEnforcer, quota, quotaObject);
			}
		}
	}

	protected boolean appliesToQuota(QuotaEnforcer<?> quotaEnforcer, String quotaName, Object quotaObject) {
		return quotaEnforcer.getQuotaName().equals(quotaName)
				&& quotaEnforcer.getObjectClass().isAssignableFrom(quotaObject.getClass());
	}

	protected <T> void enforceQuota(QuotaEnforcer<T> quotaEnforcer, QuotaSetting quota, T object)
			throws ValidationException {
		quotaEnforcer.enforceQuota(quota, object);
	}

	public void setQuotaEnforcers(List<QuotaEnforcer<?>> quotaEnforcers) {
		this.quotaEnforcers = quotaEnforcers;
	}

}
