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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.profile.service.InternalAuthenticationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class InternalAuthenticationServiceTest {

	@Autowired
	private InternalAuthenticationService service;

	@Test
	public void testSpecalizeAuthToken() {
		AuthenticationToken origToken = new AuthenticationToken();
		List<String> roles = new ArrayList<String>();
		roles.add("A/app1");
		roles.add("B/app1");
		roles.add("C/app2");
		origToken.setAuthorities(roles);
		origToken.setFirstName("first");
		origToken.setLastName("first");
		AuthenticationToken specToken = service.specializeAuthenticationToken(origToken, "app1", false);
		assertEquals(2, specToken.getAuthorities().size());
		assertEquals(3, origToken.getAuthorities().size());
	}
}
