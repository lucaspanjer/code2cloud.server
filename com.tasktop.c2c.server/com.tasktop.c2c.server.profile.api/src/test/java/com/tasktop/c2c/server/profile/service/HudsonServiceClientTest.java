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
package com.tasktop.c2c.server.profile.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.tasktop.c2c.server.profile.domain.build.HudsonStatus;

/**
 * Manual test the client against a running hudson.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@ContextConfiguration({ "/META-INF/spring/applicationContext-multiUserRestClient.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class HudsonServiceClientTest {

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void test() {
		HudsonServiceClient client = new HudsonServiceClient();
		client.setRestTemplate(restTemplate);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("clint.morgan@tasktop.com", "XXX"));
		client.setBaseUrl("https://q.tasktop.com/alm/s/c2c/hudson/");

		long start = System.currentTimeMillis();
		HudsonStatus stat = client.getStatus();
		System.out.println("Status took " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		HudsonStatus newHistory = client.getStatusWithBuildHistory();
		System.out.println("Status with history history took " + (System.currentTimeMillis() - start));

	}
}
