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
package com.tasktop.c2c.server.profile.tests.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.tasktop.c2c.server.profile.service.provider.WebDavServiceClient;

/**
 * Manual test the client against a running hudson.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@ContextConfiguration({ "/META-INF/spring/applicationContext-multiUserRestClient.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class WebDavServiceClientTest {

	@Autowired
	private RestTemplate restTemplate;
	private WebDavServiceClient client = new WebDavServiceClient();

	@Before
	public void setup() {
		client.setRestTemplate(restTemplate);

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("jdoe", "Welcome1"));
		client.setBaseUrl("http://localhost:8081/s/test/maven/");
	}

	@Test
	public void testListAllFiles() throws Exception {

		List<String> files = client.listAllFiles();

		for (String file : files) {
			System.out.println(file);
		}
	}

	@Test
	public void testGet() throws Exception {
		InputStream content = client.getFileContent("dir1/date.txt");
		StringWriter writer = new StringWriter();
		IOUtils.copy(content, writer);
		System.out.println(writer.toString());
	}

	@Test
	public void testPut() throws Exception {
		InputStream content = new ByteArrayInputStream(
				("THIS IS MY TEST CONTENT AT " + System.currentTimeMillis()).getBytes());
		String path = "dir1/newdir/anotherdir/anotherdir/anotherdir/put";
		client.writeFile(path, content);
	}
}
