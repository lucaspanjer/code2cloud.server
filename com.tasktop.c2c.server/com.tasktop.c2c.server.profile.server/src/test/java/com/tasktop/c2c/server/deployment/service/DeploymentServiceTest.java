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
package com.tasktop.c2c.server.deployment.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.deployment.domain.CloudService;
import com.tasktop.c2c.server.deployment.domain.DeploymentActivity;
import com.tasktop.c2c.server.deployment.domain.DeploymentActivityStatus;
import com.tasktop.c2c.server.deployment.domain.DeploymentActivityType;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceTypes;
import com.tasktop.c2c.server.deployment.domain.DeploymentType;
import com.tasktop.c2c.server.internal.deployment.service.DeploymentConfigurationServiceImpl;
import com.tasktop.c2c.server.internal.deployment.service.DeploymentService;
import com.tasktop.c2c.server.internal.deployment.service.DeploymentServiceFactory;
import com.tasktop.c2c.server.profile.domain.build.BuildArtifact;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails.BuildResult;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.ProjectArtifactService;
import com.tasktop.c2c.server.profile.tests.domain.mock.MockProjectFactory;

/**
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-testDisableSecurity.xml", "/applicationContext-hsql.xml" })
@Transactional
public class DeploymentServiceTest {

	@Autowired
	protected ProfileService profileService;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private DeploymentConfigurationService deploymentConfigurationService;

	private Mockery context;

	@Before
	public void setup() throws Exception {
		Project mock = MockProjectFactory.create(entityManager);
		TenancyUtil.setProjectTenancyContext(mock.getIdentifier());

		context = new JUnit4Mockery();
		final DeploymentServiceFactory mockCloudServiceFactory = context.mock(DeploymentServiceFactory.class);
		final DeploymentService mockDeploymentService = context.mock(DeploymentService.class);
		final ProjectArtifactService mockArtifactService = context.mock(ProjectArtifactService.class);
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add("service");

		final List<CloudService> mockServices = new ArrayList<CloudService>();

		final CloudService mockService = new CloudService();
		mockService.setName("service");

		context.checking(new Expectations() {
			{
				allowing(mockCloudServiceFactory).constructService(with(any(DeploymentConfiguration.class)));
				will(returnValue(mockDeploymentService));

				allowing(mockDeploymentService).getServices();
				will(returnValue(mockServices));

				allowing(mockDeploymentService).createService(with(any(CloudService.class)));
				will(new Action() {

					@Override
					public Object invoke(Invocation invocation) throws Throwable {
						mockServices.add(mockService);
						return null;
					}

					@Override
					public void describeTo(Description description) {
					}
				});

				allowing(mockDeploymentService).populate(with(any(DeploymentConfiguration.class)));
				will(new Action() {

					@Override
					public Object invoke(Invocation invocation) throws Throwable {
						DeploymentConfiguration deploymentConfiguration = (DeploymentConfiguration) invocation
								.getParameter(0);
						deploymentConfiguration.setMemory(512);
						deploymentConfiguration.setNumInstances(1);
						deploymentConfiguration.setMappedUrls(Arrays.asList("http://example.com"));
						return null;
					}

					@Override
					public void describeTo(Description description) {
					}
				});

				allowing(mockDeploymentService).getServices();
				will(returnValue(mockServices));

				// Everything else will return null.
				allowing(mockDeploymentService);

				allowing(mockArtifactService);

			}
		});
		DeploymentServiceFactory mockDeploymentFactory = new DeploymentServiceFactory() {

			@Override
			public DeploymentService constructService(DeploymentConfiguration configuration) throws ServiceException {
				return mockDeploymentService;
			}
		};
		DeploymentConfigurationServiceImpl.getLastInstance().setDeploymentServiceFactoriesByType(
				Collections.singletonMap(DeploymentServiceTypes.CLOUD_FOUNDRY, mockDeploymentFactory));
		DeploymentConfigurationServiceImpl.getLastInstance().setProjectArtifactService(mockArtifactService);
	}

	@Test
	public void testCreateAndList() throws ValidationException, ServiceException, EntityNotFoundException {
		Profile profile = setupProfile();

		List<DeploymentConfiguration> configs = deploymentConfigurationService.listDeployments(null);
		Assert.assertEquals(0, configs.size());

		DeploymentConfiguration config = new DeploymentConfiguration();

		try {
			deploymentConfigurationService.createDeployment(config);
			Assert.fail("expected validation exception");
		} catch (ValidationException e) {
			// expected;
		}

		config.setName("name");
		config.setServiceType(DeploymentServiceTypes.CLOUD_FOUNDRY);
		config.setDeploymentType(DeploymentType.AUTOMATED);
		config.setBuildArtifactPath("buildArtifactPath");
		config.setBuildJobName("buildJobName");
		config.setBuildJobNumber("buildJobNumber");
		deploymentConfigurationService.createDeployment(config);

		configs = deploymentConfigurationService.listDeployments(null);
		Assert.assertEquals(1, configs.size());

		config = configs.get(0);
		Assert.assertEquals("name", config.getName());

		List<String> urls = config.getMappedUrls();
		Assert.assertEquals(1, urls.size());
		Assert.assertEquals("http://example.com", urls.get(0));

		Assert.assertEquals(512, config.getMemory());
		Assert.assertEquals(1, config.getNumInstances());

		Assert.assertEquals(1, config.getDeploymentActivities().size());
		DeploymentActivity deploymentActivity = config.getDeploymentActivities().get(0);
		Assert.assertEquals(DeploymentActivityType.CREATED, deploymentActivity.getType());
		Assert.assertEquals(DeploymentActivityStatus.SUCCEEDED, deploymentActivity.getStatus());
		Assert.assertEquals(profile.getUsername(), deploymentActivity.getProfile().getUsername());
		Assert.assertEquals(config.getBuildArtifactPath(), deploymentActivity.getBuildArtifactPath());
		Assert.assertEquals(config.getBuildJobName(), deploymentActivity.getBuildJobName());
		Assert.assertEquals(config.getBuildJobNumber(), deploymentActivity.getBuildJobNumber());

		config = new DeploymentConfiguration();
		config.setServiceType(DeploymentServiceTypes.CLOUD_FOUNDRY);
		config.setName("name");
		config.setDeploymentType(DeploymentType.AUTOMATED);

		try {
			deploymentConfigurationService.createDeployment(config);
			Assert.fail("expected validation exception");
		} catch (ValidationException e) {
			// expected;
		}

	}

	@Test
	public void testCreateServiceAndList() throws ValidationException, ServiceException, EntityNotFoundException {
		DeploymentConfiguration config = new DeploymentConfiguration();
		config.setName("name");
		config.setServiceType(DeploymentServiceTypes.CLOUD_FOUNDRY);
		config.setDeploymentType(DeploymentType.AUTOMATED);
		deploymentConfigurationService.createDeployment(config);

		List<CloudService> services = deploymentConfigurationService.getAvailableServices(config);
		Assert.assertEquals(0, services.size());

		CloudService service = new CloudService();
		service.setName("service");
		deploymentConfigurationService.createService(service, config);

		services = deploymentConfigurationService.getAvailableServices(config);
		Assert.assertEquals(1, services.size());
		Assert.assertEquals("service", services.get(0).getName());
	}

	@Test
	@Ignore
	public void testListServiceConfigurations() throws ServiceException {
		DeploymentConfiguration config = new DeploymentConfiguration();
		config.setName("name");
		config.setServiceType(DeploymentServiceTypes.CLOUD_FOUNDRY);
		config.setDeploymentType(DeploymentType.AUTOMATED);

		List<DeploymentServiceConfiguration> serviceConfigs = deploymentConfigurationService
				.getAvailableServiceConfigurations(config);
		Assert.assertEquals(1, serviceConfigs.size());

		DeploymentServiceConfiguration serviceConfig = serviceConfigs.get(0);
		Assert.assertEquals("description", serviceConfig.getDescription());
		Assert.assertEquals("type", serviceConfig.getType());
		Assert.assertEquals("vendor", serviceConfig.getVendor());
		Assert.assertEquals("1.0", serviceConfig.getVersion());
	}

	@Test
	public void testAutoDeployment() throws ValidationException, EntityNotFoundException {
		DeploymentConfiguration config = new DeploymentConfiguration();

		try {
			deploymentConfigurationService.createDeployment(config);
			Assert.fail("expected validation exception");
		} catch (ValidationException e) {
			// expected;
		}

		config.setName("name");
		config.setServiceType(DeploymentServiceTypes.CLOUD_FOUNDRY);
		config.setDeploymentType(DeploymentType.AUTOMATED);
		String jobName = "job";
		String jobNumber = "111";
		String artifactPath = "**/*.war";
		config.setBuildJobName(jobName);
		config.setBuildJobNumber(jobNumber);
		config.setBuildArtifactPath(artifactPath);
		deploymentConfigurationService.createDeployment(config);

		BuildDetails buildDetails = new BuildDetails();
		buildDetails.setResult(BuildResult.SUCCESS);
		buildDetails.setNumber(111);
		BuildArtifact a1 = new BuildArtifact();
		a1.setRelativePath("src/bar.txt");
		a1.setFileName("bar.txt");
		BuildArtifact a2 = new BuildArtifact();
		a2.setRelativePath("target/bar.war");
		a2.setFileName("bar.war");

		buildDetails.setArtifacts(Arrays.asList(a1));
		deploymentConfigurationService.onBuildCompleted(jobName, buildDetails);

		config = deploymentConfigurationService.listDeployments(null).get(0);
		Assert.assertNull(config.getLastDeploymentDate());

		buildDetails.setArtifacts(Arrays.asList(a1, a2));
		deploymentConfigurationService.onBuildCompleted(jobName, buildDetails);

		config = deploymentConfigurationService.listDeployments(null).get(0);
		Assert.assertNotNull(config.getLastDeploymentDate());

	}

	@Test
	public void testUpdate() throws Exception {
		DeploymentConfiguration config = new DeploymentConfiguration();
		config.setName("name");
		config.setServiceType(DeploymentServiceTypes.CLOUD_FOUNDRY);
		config.setDeploymentType(DeploymentType.AUTOMATED);
		deploymentConfigurationService.createDeployment(config);

		List<DeploymentConfiguration> configs = deploymentConfigurationService.listDeployments(null);
		Assert.assertEquals(1, configs.size());

		config = configs.get(0);
		config.setDeploymentType(DeploymentType.MANUAL);
		deploymentConfigurationService.updateDeployment(config);

		configs = deploymentConfigurationService.listDeployments(null);
		Assert.assertEquals(1, configs.size());

		config = configs.get(0);
		Assert.assertEquals(DeploymentType.MANUAL, config.getDeploymentType());
		Assert.assertTrue(config.getDeploymentActivities().size() > 0);

		// ensure transient values are kept
		Assert.assertEquals(Arrays.asList("http://example.com"), config.getMappedUrls());
		Assert.assertEquals(512, config.getMemory());

		boolean foundUpdatedActivity = false;
		for (DeploymentActivity da : config.getDeploymentActivities()) {
			if (DeploymentActivityType.UPDATED.equals(da.getType())) {
				foundUpdatedActivity = true;
			}
		}
		Assert.assertTrue(foundUpdatedActivity);
	}

	@Test
	public void testStartStopAndRestart() throws Exception {
		DeploymentConfiguration config = new DeploymentConfiguration();
		config.setName("name");
		config.setServiceType(DeploymentServiceTypes.CLOUD_FOUNDRY);
		config.setDeploymentType(DeploymentType.MANUAL);
		deploymentConfigurationService.createDeployment(config);

		List<DeploymentConfiguration> configs = deploymentConfigurationService.listDeployments(null);
		Assert.assertEquals(1, configs.size());
		config = configs.get(0);

		deploymentConfigurationService.startDeployment(config.getId());
		configs = deploymentConfigurationService.listDeployments(null);
		config = configs.get(0);
		Assert.assertEquals(2, config.getDeploymentActivities().size()); // created, started
		Assert.assertTrue(foundMatchingType(config.getDeploymentActivities(), DeploymentActivityType.STARTED));

		deploymentConfigurationService.stopDeployment(config.getId());
		configs = deploymentConfigurationService.listDeployments(null);
		config = configs.get(0);
		Assert.assertEquals(3, config.getDeploymentActivities().size()); // created, started, stopped
		Assert.assertTrue(foundMatchingType(config.getDeploymentActivities(), DeploymentActivityType.STOPPED));

		deploymentConfigurationService.restartDeployment(config.getId());
		configs = deploymentConfigurationService.listDeployments(null);
		config = configs.get(0);
		Assert.assertEquals(4, config.getDeploymentActivities().size()); // created, started, stopped, restarted
		Assert.assertTrue(foundMatchingType(config.getDeploymentActivities(), DeploymentActivityType.RESTARTED));
	}

	private boolean foundMatchingType(List<DeploymentActivity> deploymentActivities,
			DeploymentActivityType deploymentActivityType) {
		for (DeploymentActivity da : deploymentActivities) {
			if (deploymentActivityType.equals(da.getType())) {
				return true;
			}
		}
		return false;
	}

	private Profile setupProfile() throws ValidationException {
		Profile internalProfile = new Profile();
		internalProfile.setEmail("email@profile.com");
		internalProfile.setFirstName("First");
		internalProfile.setLastName("Last");
		internalProfile.setUsername(internalProfile.getEmail());
		internalProfile.setPassword("passa1word");
		internalProfile.setLanguage("en_US");
		profileService.createProfile(internalProfile);

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(internalProfile.getUsername(), internalProfile.getPassword()));

		return internalProfile;
	}

}
