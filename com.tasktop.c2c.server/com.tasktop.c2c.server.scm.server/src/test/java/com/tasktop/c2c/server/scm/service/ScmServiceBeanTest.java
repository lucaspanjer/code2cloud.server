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
package com.tasktop.c2c.server.profile.service.provider;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmType;
import com.tasktop.c2c.server.scm.service.ScmService;
import com.tasktop.c2c.server.scm.service.ScmServiceConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-test.xml" })
public class ScmServiceBeanTest {

	@Value("${git.root}")
	private String gitRoot;

	@Resource(name = "scmService")
	private ScmService scmService;

	@Autowired
	private ScmServiceConfiguration profileServiceConfiguration;

	private String projId = "projid";

	@Before
	public void setup() throws IOException {
		File gitRootFile = new File(gitRoot);
		if (!gitRootFile.exists()) {
			gitRootFile.mkdirs();
		} else {
			FileUtils.deleteDirectory(gitRootFile);
		}

		TenancyContextHolder.createEmptyContext();
		TenancyUtil.setProjectTenancyContext(projId);
	}

	@Test
	public void testCreateInternalRepo() throws Exception {

		List<ScmRepository> repoSet = scmService.getScmRepositories();
		assertEquals(0, repoSet.size());

		String repositoryName = "code2cloud.git";
		String repo = profileServiceConfiguration.getHostedScmUrlPrefix(projId) + repositoryName;
		String repoSsh = "ssh://localhost/" + projId + "/" + repositoryName;

		ScmRepository newRepo = new ScmRepository();
		newRepo.setScmLocation(ScmLocation.CODE2CLOUD);
		newRepo.setType(ScmType.GIT);
		newRepo.setName(repositoryName);
		newRepo.setUrl(repo);

		scmService.createScmRepository(newRepo);

		repoSet = scmService.getScmRepositories();

		assertEquals(1, repoSet.size());
		ScmRepository retrievedRepo = repoSet.iterator().next();
		assertEquals(newRepo.getType(), retrievedRepo.getType());
		assertEquals(newRepo.getScmLocation(), retrievedRepo.getScmLocation());
		assertEquals(newRepo.getUrl(), retrievedRepo.getUrl());
		Assert.assertNotNull(retrievedRepo.getAlternateUrl());
		assertEquals(repoSsh, retrievedRepo.getAlternateUrl());
	}

	@Test
	public void testCreateExternalScmRepository() throws Exception {

		String repo = "http://q.tasktop.com/alm/s/code2cloud/scm/code2cloud.git";

		List<ScmRepository> repoSet = scmService.getScmRepositories();
		assertEquals(0, repoSet.size());

		scmService.createScmRepository(createExternalRepo(repo));

		repoSet = scmService.getScmRepositories();
		assertEquals(1, repoSet.size());
		ScmRepository retrievedRepo = repoSet.iterator().next();
		assertEquals(ScmType.GIT, retrievedRepo.getType());
		assertEquals(ScmLocation.EXTERNAL, retrievedRepo.getScmLocation());
		assertEquals(repo, retrievedRepo.getUrl());
		assertNull(retrievedRepo.getAlternateUrl());
	}

	private ScmRepository createExternalRepo(String externalUrl) {
		ScmRepository repo = new ScmRepository();
		repo.setType(ScmType.GIT);
		repo.setScmLocation(ScmLocation.EXTERNAL);
		repo.setUrl(externalUrl);
		return repo;
	}

	@Test(expected = ValidationException.class)
	public void testCreateExternalScmRepository_BlankURL() throws Exception {
		String repositoryURL = "";

		List<ScmRepository> repoSet = scmService.getScmRepositories();
		assertEquals(0, repoSet.size());

		scmService.createScmRepository(createExternalRepo(repositoryURL));
		// should throw exception
		Assert.fail();
	}

	@Test(expected = ValidationException.class)
	public void testCreateExternalScmRepository_NullURL() throws Exception {
		String repositoryURL = null;

		List<ScmRepository> repoSet = scmService.getScmRepositories();
		assertEquals(0, repoSet.size());

		scmService.createScmRepository(createExternalRepo(repositoryURL));
		// should throw exception
		Assert.fail();
	}

	@Test
	public void testDeleteScmRepository() throws Exception {
		String repo = "http://q.tasktop.com/alm/s/code2cloud/scm/code2cloud.git";

		List<ScmRepository> repoSet = scmService.getScmRepositories();
		assertEquals(0, repoSet.size());

		scmService.createScmRepository(createExternalRepo(repo));

		repoSet = scmService.getScmRepositories();
		assertEquals(1, repoSet.size());
		ScmRepository retrievedRepo = repoSet.iterator().next();
		assertEquals(ScmType.GIT, retrievedRepo.getType());
		assertEquals(ScmLocation.EXTERNAL, retrievedRepo.getScmLocation());
		assertEquals(repo, retrievedRepo.getUrl());

		scmService.deleteScmRepository(retrievedRepo);

		repoSet = scmService.getScmRepositories();
		assertEquals(0, repoSet.size());
	}

	@Test(expected = EntityNotFoundException.class)
	public void testDeleteScmRepository_nonexistentProject() throws Exception {

		// This should blow up.
		scmService.deleteScmRepository(createExternalRepo("http://q.tasktop.com/alm/s/code2cloud/scm/code2cloud.git"));
	}
}
