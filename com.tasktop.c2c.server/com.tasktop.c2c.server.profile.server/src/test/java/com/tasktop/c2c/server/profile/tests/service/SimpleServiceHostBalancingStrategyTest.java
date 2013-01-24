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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.internal.profile.service.ProjectServiceMigrationStrategy;
import com.tasktop.c2c.server.internal.profile.service.SimpleServiceHostBalancingStrategy;
import com.tasktop.c2c.server.internal.profile.service.StatelessServiceMigrationStrategy;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.ProjectServiceProfile;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHostConfiguration;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class SimpleServiceHostBalancingStrategyTest {

	private SimpleServiceHostBalancingStrategy balancingStrategy;
	private ProjectServiceMigrationStrategy migrationStrategy;
	private ServiceHostConfiguration serviceHostConfig;

	private ProjectServiceProfile psProfile;

	@Before
	public void setup() {
		balancingStrategy = new SimpleServiceHostBalancingStrategy();

		migrationStrategy = new StatelessServiceMigrationStrategy();
		balancingStrategy.setMigrationStrategy(migrationStrategy);

		serviceHostConfig = new ServiceHostConfiguration();
		serviceHostConfig.setSupportedServices(Collections.singleton(ServiceType.TASKS));

		psProfile = new ProjectServiceProfile();
		psProfile.setProject(new Project());
		psProfile.getProject().setIdentifier("project-id");

	}

	static int nextIp = 1;

	private ServiceHost createNewHost(int numProjects) {
		ServiceHost result = new ServiceHost();
		result.setInternalNetworkAddress("10.0.0." + nextIp++);
		result.setAvailable(true);
		result.setServiceHostConfiguration(serviceHostConfig);

		for (int i = 0; i < numProjects; i++) {
			ProjectService ps = new ProjectService();
			ps.setServiceHost(result);
			ps.setType(ServiceType.TASKS);
			ps.setProjectServiceProfile(psProfile);
			result.getProjectServices().add(ps);
		}
		return result;
	}

	@Test
	public void testBalanceOntoEmptyHost_1otherHost() {
		ServiceHost ontoHost = createNewHost(0);
		List<ServiceHost> otherHosts = Arrays.asList(createNewHost(10));

		balancingStrategy.balance(ontoHost, otherHosts);

		Assert.assertEquals(5, ontoHost.getProjectServices().size());
		Assert.assertEquals(5, otherHosts.get(0).getProjectServices().size());
	}

	@Test
	public void testBalanceOntoEmptyHost_2otherHosts() {
		ServiceHost ontoHost = createNewHost(0);
		List<ServiceHost> otherHosts = Arrays.asList(createNewHost(4), createNewHost(5));

		balancingStrategy.balance(ontoHost, otherHosts);

		Assert.assertEquals(3, ontoHost.getProjectServices().size());
		Assert.assertEquals(3, otherHosts.get(0).getProjectServices().size());
		Assert.assertEquals(3, otherHosts.get(1).getProjectServices().size());
	}

	@Test
	public void testBalanceOntoEmptyHost_2otherHostsOneIsDown() {
		ServiceHost ontoHost = createNewHost(0);
		ServiceHost downHost = createNewHost(5);
		downHost.setAvailable(false);
		List<ServiceHost> otherHosts = Arrays.asList(createNewHost(5), downHost);

		balancingStrategy.balance(ontoHost, otherHosts);

		Assert.assertEquals(5, ontoHost.getProjectServices().size());
		Assert.assertEquals(5, otherHosts.get(0).getProjectServices().size());
		Assert.assertEquals(0, otherHosts.get(1).getProjectServices().size());
	}

	@Test
	public void testBalanceOntoEmptyHost_2otherHostsOneIsDown_uneven() {
		ServiceHost ontoHost = createNewHost(0);
		ServiceHost downHost = createNewHost(3);
		downHost.setAvailable(false);
		List<ServiceHost> otherHosts = Arrays.asList(createNewHost(5), downHost);

		balancingStrategy.balance(ontoHost, otherHosts);

		Assert.assertEquals(4, ontoHost.getProjectServices().size());
		Assert.assertEquals(4, otherHosts.get(0).getProjectServices().size());
		Assert.assertEquals(0, otherHosts.get(1).getProjectServices().size());
	}
}
