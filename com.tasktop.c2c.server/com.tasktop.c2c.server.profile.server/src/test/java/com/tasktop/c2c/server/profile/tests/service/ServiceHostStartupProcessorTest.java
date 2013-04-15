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

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.cloud.service.ServiceHostService;
import com.tasktop.c2c.server.internal.profile.service.ServiceHostStartupProcessor;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-testDisableSecurity.xml", "/applicationContext-hsql.xml" })
@Transactional
public class ServiceHostStartupProcessorTest {

	@Autowired
	protected ServiceHostService serviceHostService;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	public void testInitialThenUpdatedSetup() throws Exception {
		ServiceHostStartupProcessor serviceHostStartupProcessor = new ServiceHostStartupProcessor();
		serviceHostStartupProcessor.setConfigFile(System.getProperty("user.dir")
				+ "/src/test/resources/applicationContext-serviceHosts.xml");
		serviceHostStartupProcessor.setServiceHostService(serviceHostService);
		serviceHostStartupProcessor.afterPropertiesSet();
		List<ServiceHost> serviceHosts = entityManager.createQuery("SELECT sh FROM ServiceHost sh", ServiceHost.class)
				.getResultList();
		Assert.assertEquals(2, serviceHosts.size());
		boolean foundFirstExpectedHost = false;
		boolean foundSecondExpectedHost = false;
		for (ServiceHost host : serviceHosts) {
			Set<ServiceType> supportedServices = host.getServiceHostConfiguration().getSupportedServices();
			if (supportedServices.contains(ServiceType.BUILD) && supportedServices.contains(ServiceType.TASKS)
					&& supportedServices.contains(ServiceType.WIKI) && supportedServices.contains(ServiceType.SCM)
					&& supportedServices.contains(ServiceType.MAVEN)) {
				foundFirstExpectedHost = true;
			} else if (supportedServices.contains(ServiceType.BUILD_SLAVE)) {
				foundSecondExpectedHost = true;
			}
		}
		Assert.assertTrue(foundFirstExpectedHost);
		Assert.assertTrue(foundSecondExpectedHost);

		// now we try an updated configuration
		serviceHostStartupProcessor.setConfigFile(System.getProperty("user.dir")
				+ "/src/test/resources/applicationContext-serviceHosts-updated.xml");
		serviceHostStartupProcessor.afterPropertiesSet();
		serviceHosts = entityManager.createQuery("SELECT sh FROM ServiceHost sh", ServiceHost.class).getResultList();
		Assert.assertEquals(2, serviceHosts.size());
		foundFirstExpectedHost = false;
		foundSecondExpectedHost = false;
		for (ServiceHost host : serviceHosts) {
			Set<ServiceType> supportedServices = host.getServiceHostConfiguration().getSupportedServices();
			if (supportedServices.contains(ServiceType.BUILD) && supportedServices.contains(ServiceType.TASKS)
					&& supportedServices.contains(ServiceType.WIKI) && supportedServices.contains(ServiceType.SCM)
					&& supportedServices.contains(ServiceType.MAVEN) && supportedServices.contains(ServiceType.REVIEWS)) {
				foundFirstExpectedHost = true;
			} else if (supportedServices.contains(ServiceType.BUILD_SLAVE)) {
				foundSecondExpectedHost = true;
			}
		}
		Assert.assertTrue(foundFirstExpectedHost);
		Assert.assertTrue(foundSecondExpectedHost);
	}
}
