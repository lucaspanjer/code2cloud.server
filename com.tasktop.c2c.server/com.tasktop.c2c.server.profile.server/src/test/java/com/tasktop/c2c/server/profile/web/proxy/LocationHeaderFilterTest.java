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
package com.tasktop.c2c.server.profile.web.proxy;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class LocationHeaderFilterTest {

	protected ProjectService projectService;
	protected LocationHeaderFilter locationHeaderFilter = new LocationHeaderFilter();
	protected MockHttpServletRequest request;
	protected ProfileServiceConfiguration config;

	@Before
	public void setup() {
		ServiceHost serviceHost = new ServiceHost();
		serviceHost.setInternalNetworkAddress("10.0.0.34");

		projectService = new ProjectService();
		projectService.setServiceHost(serviceHost);
		serviceHost.getProjectServices().add(projectService);
		projectService.setAjpPort(123);
		projectService.setInternalPort(8080);
		projectService.setInternalUriPrefix("/hudson");
		projectService.setUriPattern("/hudson(.*)");

		request = new MockHttpServletRequest("POST", "/s/project1/hudson/job/foo");
		request.setAttribute(ApplicationServiceProxyFilter.ATTR_APPLICATION_SERVICE, projectService);
		ServletRequestAttributes reqAttributes = new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(reqAttributes);

		config = new ProfileServiceConfiguration();
		config.setWebHost("c2c.dev");
		config.setProfileApplicationProtocol("http");
		config.setServiceProxyPath("s");
		config.setBaseContextPath("/");

		locationHeaderFilter.setConfiguration(config);

	}

	@Test
	public void testForHudson() {
		String uri = "/hudson/job/foo";
		request.setAttribute(ApplicationServiceProxyFilter.ATTR_APPLICATION_SERVICE_URI, uri);

		String headerValue = "http://c2c.dev/hudson/job/foo";
		String resultValue = locationHeaderFilter.processRequestHeader("Location", headerValue);
		Assert.assertEquals("Should not touch request headers", headerValue, resultValue);

		resultValue = locationHeaderFilter.processResponseHeader("Location", headerValue);
		Assert.assertEquals("http://c2c.dev/s/project1/hudson/job/foo", resultValue);

	}

	@Test
	public void testForHudsonPerOrg() {
		projectService.setInternalUriPrefix("/s2/qa-dev/hudson");

		String uri = "/hudson/job/foo";
		request.setAttribute(ApplicationServiceProxyFilter.ATTR_APPLICATION_SERVICE_URI, uri);

		String headerValue = "http://c2c.dev/s2/qa-dev/hudson/job/foo";
		String resultValue = locationHeaderFilter.processRequestHeader("Location", headerValue);
		Assert.assertEquals("Should not touch request headers", headerValue, resultValue);

		resultValue = locationHeaderFilter.processResponseHeader("Location", headerValue);
		Assert.assertEquals("http://c2c.dev/s/project1/hudson/job/foo", resultValue);

	}

	@Test
	public void testForHudsonNonRootContext() {
		config.setBaseContextPath("/alm/");
		request.setRequestURI("/alm/s/project1/hudson/job/foo");

		String uri = "/hudson/job/foo";
		request.setAttribute(ApplicationServiceProxyFilter.ATTR_APPLICATION_SERVICE_URI, uri);

		String headerValue = "http://c2c.dev/hudson/job/foo";
		String resultValue = locationHeaderFilter.processRequestHeader("Location", headerValue);
		Assert.assertEquals("Should not touch request headers", headerValue, resultValue);

		resultValue = locationHeaderFilter.processResponseHeader("Location", headerValue);
		Assert.assertEquals("http://c2c.dev/alm/s/project1/hudson/job/foo", resultValue);

	}

}
