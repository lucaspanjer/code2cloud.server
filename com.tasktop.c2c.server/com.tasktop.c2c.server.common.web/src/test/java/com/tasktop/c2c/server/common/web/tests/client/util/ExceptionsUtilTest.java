/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.web.client.util.ExceptionsUtil;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ExceptionsUtilTest {

	@Test
	public void testENFE() {
		Assert.assertEquals(EntityNotFoundException.class.getName(),
				ExceptionsUtil.ENITITY_NOT_FOUND_EXCEPTION_CLASSNAME);
	}
}
