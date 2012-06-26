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
package com.tasktop.c2c.server.profile.tests.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.profile.domain.internal.Organization;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.QuotaSetting;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.QuotaService;
import com.tasktop.c2c.server.profile.tests.domain.mock.MockOrganizationFactory;
import com.tasktop.c2c.server.profile.tests.domain.mock.MockProjectFactory;

/**
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-testDisableSecurity.xml", "/applicationContext-hsql.xml" })
@Transactional
public class QuotaServiceTest {

	@Autowired
	protected QuotaService quotaService;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	protected ProfileService profileService;

	@Test
	public void testBasicCrudOps() throws EntityNotFoundException {
		QuotaSetting quota = createQuota("NAME", "Value", null, null);
		quota.setValue("VALUE2");
		quota = quotaService.updateQuota(quota);

		List<QuotaSetting> quotaList = quotaService.findQuotaSettings("NAME", null, null);
		Assert.assertEquals(1, quotaList.size());

		quotaService.removeQuota(quota);

		quotaList = quotaService.findQuotaSettings("NAME", null, null);
		Assert.assertEquals(0, quotaList.size());
	}

	private QuotaSetting createQuota(String name, String value, Project project, Organization organization)
			throws EntityNotFoundException {
		QuotaSetting quota = new QuotaSetting();
		quota.setName(name);
		quota.setValue(value);
		quota.setProject(project);
		quota.setOrganization(organization);
		return quotaService.createQuota(quota);
	}

	@Test
	public void testFindQuotaSettings() throws EntityNotFoundException {
		String quotaName = "NAME";
		Organization org = MockOrganizationFactory.create(entityManager);
		Project project = MockProjectFactory.create(entityManager);

		List<QuotaSetting> quotaList = quotaService.findQuotaSettings(quotaName, null, null);
		Assert.assertEquals(0, quotaList.size());

		createQuota(quotaName, "generalValue", null, null);
		createQuota(quotaName, "projValue", project, null);
		createQuota(quotaName, "orgValue", null, org);

		entityManager.flush();

		quotaList = quotaService.findQuotaSettings(quotaName, null, null);
		Assert.assertEquals(1, quotaList.size());

		quotaList = quotaService.findQuotaSettings(quotaName, project.getIdentifier(), null);
		Assert.assertEquals(2, quotaList.size());

		quotaList = quotaService.findQuotaSettings(quotaName, null, org.getIdentifier());
		Assert.assertEquals(2, quotaList.size());

		quotaList = quotaService.findQuotaSettings(quotaName, "XXX", null);
		Assert.assertEquals(1, quotaList.size());

		quotaList = quotaService.findQuotaSettings(quotaName, null, "YYY");
		Assert.assertEquals(1, quotaList.size());
	}

	@Test
	public void testEnforceUnDefinedQuota() throws ValidationException {
		// make sure it allows it
		quotaService.enforceQuota("XXXXXXXX", null);
	}

	@Test
	public void testEnforceMaxProjectsQuota() throws EntityNotFoundException, ValidationException {
		Organization org = MockOrganizationFactory.create(entityManager);
		TenancyUtil.setOrganizationTenancyContext(org.getIdentifier());

		// Add global quota
		QuotaSetting quota = new QuotaSetting();
		quota.setName(ProfileService.MAX_PROJECTS_QUOTA_NAME);
		quota.setValue(Integer.toString(1));
		quota = quotaService.createQuota(quota);

		Project p1 = creatOrgProject(org);
		quotaService.enforceQuota(ProfileService.MAX_PROJECTS_QUOTA_NAME, p1);
		org.getProjects().add(p1);
		entityManager.persist(p1);

		Project p2 = creatOrgProject(org);
		try {
			quotaService.enforceQuota(ProfileService.MAX_PROJECTS_QUOTA_NAME, p2);
			Assert.fail("expected validation exception");
		} catch (ValidationException e) {
			// expected
		}

		// Now add org-specific quota to override global
		quota = new QuotaSetting();
		quota.setName(ProfileService.MAX_PROJECTS_QUOTA_NAME);
		quota.setValue(Integer.toString(10));
		quota.setOrganization(org);
		quota = quotaService.createQuota(quota);

		quotaService.enforceQuota(ProfileService.MAX_PROJECTS_QUOTA_NAME, p2);

	}

	private Project creatOrgProject(Organization org) {
		Project p = MockProjectFactory.create(null);
		p.setOrganization(org);
		return p;

	}
}
