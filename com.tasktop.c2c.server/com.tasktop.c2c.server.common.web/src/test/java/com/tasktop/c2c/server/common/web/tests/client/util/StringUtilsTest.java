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
package com.tasktop.c2c.server.common.web.tests.client.util;

import junit.framework.Assert;

import org.junit.Test;

import com.tasktop.c2c.server.common.web.client.util.StringUtils;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class StringUtilsTest {

	@Test
	public void testNoSpaceChop() {
		testChop5Chars("", "");
		testChop5Chars("12\n12345\n123456\n\n", "12\n12345\n12345\n6\n\n");
		testChop5Chars("12\n12345\n123456789012\n\n1234\n\n", "12\n12345\n12345\n67890\n12\n\n1234\n\n");
		testChop5Chars("123456", "12345\n6");
	}

	@Test
	public void testSpaceChop() {
		testChop5Chars("1234 6", "1234\n6");
		testChop5Chars("12345 ", "12345\n ");
		testChop5Chars("12 456", "12\n456");
		testChop5Chars("1 3 56", "1 3\n56");
	}

	private void testChop5Chars(String orig, String expected) {
		String chopped = StringUtils.chopLongLinesAtSpaces(5, orig);
		Assert.assertEquals(expected, chopped);
	}
}
