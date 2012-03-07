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
package com.tasktop.c2c.server.auth.service.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tasktop.c2c.server.auth.service.AuthUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class AuthUtilsTest {

	@Test
	public void testToCompoundRole() {
		assertEquals("A/B", AuthUtils.toCompoundRole("A", "B"));
	}

	@Test
	public void testFromCompoundRole() {
		assertEquals("A", AuthUtils.fromCompoundRole("A/B", "B"));
	}

	@Test
	public void testFromCompoundRoleNotApplicable() {
		assertNull(AuthUtils.fromCompoundRole("A/B", "C"));
	}

	@Test
	public void testFromCompoundRoleMany() {
		List<String> roles = new ArrayList<String>();
		roles.add("A/B");
		roles.add("C/D");
		List<String> expected = new ArrayList<String>();
		expected.add("A");
		assertEquals(expected, AuthUtils.fromCompoundRole(roles, "B"));
	}

}
