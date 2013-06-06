/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.internal.profile.service.template;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class HudsonBuildServiceClonerTest {

	@Test
	public void testReplaceId() {
		String configXML = "<project>\n" + "<actions/>\n" + "<description/>\n" + "<id>qa-dev_test_Build</id>\n"
				+ "</project>\n";
		String newId = "qa-dev_target_Build";
		String expectedXML = "<project>\n" + "<actions/>\n" + "<description/>\n" + "<id>" + newId + "</id>\n"
				+ "</project>\n";
		Assert.assertEquals(expectedXML, HudsonBuildServiceCloner.replaceIdElement(configXML, newId));
	}
}
