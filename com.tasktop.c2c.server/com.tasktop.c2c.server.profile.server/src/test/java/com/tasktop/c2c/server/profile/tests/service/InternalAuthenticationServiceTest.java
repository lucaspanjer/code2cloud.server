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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.internal.profile.service.InternalAuthenticationServiceBean;
import com.tasktop.c2c.server.profile.domain.internal.Organization;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectAccessibility;
import com.tasktop.c2c.server.profile.service.InternalAuthenticationService;
import com.tasktop.c2c.server.profile.tests.domain.mock.MockOrganizationFactory;
import com.tasktop.c2c.server.profile.tests.domain.mock.MockProfileFactory;

//@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class InternalAuthenticationServiceTest {

	private InternalAuthenticationService service = new InternalAuthenticationServiceBean();
	private AuthenticationToken origToken;
	private Project project1;

	@Before
	public void setup() {
		origToken = new AuthenticationToken();
		project1 = new Project();
		project1.setIdentifier("p1");
		List<String> roles = new ArrayList<String>();
		roles.add(AuthUtils.toCompoundProjectRole(Role.User, project1.getIdentifier()));
		roles.add(AuthUtils.toCompoundProjectRole(Role.Admin, project1.getIdentifier()));
		roles.add(AuthUtils.toCompoundProjectRole(Role.Admin, "otherIdentifier"));
		origToken.setAuthorities(roles);
		origToken.setFirstName("first");
		origToken.setLastName("last");
		origToken.setUsername("username");
		assertEquals(3, origToken.getAuthorities().size());
	}

	@Test
	public void testSpecalizeAuthTokenWithPrivateProject() {
		project1.setAccessibility(ProjectAccessibility.PRIVATE);
		AuthenticationToken specToken = service.specializeAuthenticationToken(origToken, project1);
		assertEquals(2, specToken.getAuthorities().size());
		Assert.assertTrue(specToken.getAuthorities().contains(Role.User));
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Admin));
	}

	@Test
	public void testSpecalizeAuthTokenWithPublicProject() {
		project1.setAccessibility(ProjectAccessibility.PUBLIC);
		AuthenticationToken specToken = service.specializeAuthenticationToken(origToken, project1);
		assertEquals(4, specToken.getAuthorities().size());
		Assert.assertTrue(specToken.getAuthorities().contains(Role.User));
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Admin));
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Community));
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Observer));
	}

	@Test
	public void testSpecalizeAnonAuthTokenWithPublicProject() {
		project1.setAccessibility(ProjectAccessibility.PUBLIC);
		AuthenticationToken specToken = service.specializeAuthenticationToken(null, project1);
		assertEquals(2, specToken.getAuthorities().size());
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Observer));
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Anonymous));
	}

	@Test
	public void testSpecalizeAuthTokenWithOrgPrivateProjectAndMember() {
		project1.setAccessibility(ProjectAccessibility.ORGANIZATION_PRIVATE);
		Profile p = MockProfileFactory.create(null);
		p.setUsername(origToken.getUsername());
		Organization org = MockOrganizationFactory.create(null);
		project1.setOrganization(org);

		origToken.getAuthorities().clear();
		origToken.getAuthorities().add(AuthUtils.toCompoundOrganizationRole(Role.User, org.getIdentifier()));
		AuthenticationToken specToken = service.specializeAuthenticationToken(origToken, project1);
		assertEquals(2, specToken.getAuthorities().size());
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Community));
		Assert.assertTrue(specToken.getAuthorities().contains(Role.Observer));
	}
}
