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
package com.tasktop.c2c.server.profile.tests.service;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import com.tasktop.c2c.server.cloud.domain.ServiceHost;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.cloud.service.ServiceHostService;
import com.tasktop.c2c.server.internal.profile.service.ServiceHostStartupProcessor;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class ServiceHostStartupProcessorTest {

	Mockery context = new Mockery();
	final ServiceHostService mockServiceHostService = context.mock(ServiceHostService.class);

	private ServiceHostStartupProcessor processor = new ServiceHostStartupProcessor();

	public ServiceHostStartupProcessorTest() {
		// This is the test Spring configuration file; please ensure it
		// is kept in sync with values we test with below
		processor.setConfigFile(System.getProperty("user.dir")
				+ "/src/test/resources/applicationContext-serviceHosts.xml");
		processor.setServiceHostService(mockServiceHostService);
	}

	@Test
	public void testCreateServiceHosts() throws Exception {
		final Set<ServiceType> types = new HashSet<ServiceType>();
		types.add(ServiceType.TASKS);
		types.add(ServiceType.WIKI);
		context.checking(new Expectations() {
			{
				oneOf(mockServiceHostService).findHostForIpAndType("127.0.0.1", types);
				oneOf(mockServiceHostService).findHostForIpAndType("127.0.0.2", types);
				exactly(2).of(mockServiceHostService).createServiceHost(with(any(ServiceHost.class)));
			}
		});
		processor.afterPropertiesSet();
		context.assertIsSatisfied();
	}
}
