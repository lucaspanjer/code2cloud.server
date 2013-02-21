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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.internal.profile.service.ServiceAwareTenancyManager;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.service.provider.InternalJgitProvider;
import com.tasktop.c2c.server.profile.service.provider.ScmServiceProvider;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmType;
import com.tasktop.c2c.server.scm.service.ScmService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GitServiceCloner extends BaseProjectServiceCloner {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitServiceCloner.class);

	@Autowired
	private ScmServiceProvider scmServiceProvider;

	@Autowired
	private InternalJgitProvider jgitProvider;

	@Autowired
	private ServiceAwareTenancyManager tenancyManager;

	/**
	 * @param serviceType
	 */
	protected GitServiceCloner() {
		super(ServiceType.SCM);
	}

	@Override
	public void doClone(ProjectService templateService, ProjectService targetProjectService) {
		List<ScmRepository> templateReposToClone = getTemplateRepositoriesToClone(templateService);

		AuthUtils.assumeSystemIdentity(targetProjectService.getProjectServiceProfile().getProject().getIdentifier());
		ScmService targetScmService = scmServiceProvider.getService(targetProjectService.getProjectServiceProfile()
				.getProject().getIdentifier());

		List<ScmRepository> createdRepos = new ArrayList<ScmRepository>();
		for (ScmRepository repo : templateReposToClone) {
			try {
				ScmRepository created = targetScmService.createScmRepository(repo);
				createdRepos.add(created);
			} catch (EntityNotFoundException e) {
				LOGGER.warn(String.format("Error creating repo [%s] from template project [%s] in project [%s]",
						repo.getName(), templateService.getProjectServiceProfile().getProject().getIdentifier(),
						targetProjectService.getProjectServiceProfile().getProject().getIdentifier()), e);
			} catch (ValidationException e) {
				LOGGER.warn(String.format("Error creating repo [%s] from template project [%s] in project [%s]",
						repo.getName(), templateService.getProjectServiceProfile().getProject().getIdentifier(),
						targetProjectService.getProjectServiceProfile().getProject().getIdentifier()), e);
			}
		}

		for (ScmRepository repo : createdRepos) {
			try {
				copyRepo(repo, templateService, targetProjectService);
			} catch (Exception e) {
				LOGGER.warn(String.format("Error copying repo [%s] from template project [%s] in project [%s]",
						repo.getName(), templateService.getProjectServiceProfile().getProject().getIdentifier(),
						targetProjectService.getProjectServiceProfile().getProject().getIdentifier()), e);
			}
		}
	}

	private void copyRepo(ScmRepository scmRepo, ProjectService templateService, ProjectService targetProjectService)
			throws IOException, JGitInternalException, InvalidRemoteException {
		String cloneUrl = jgitProvider.computeRepositoryUrl(templateService.getProjectServiceProfile().getProject()
				.getIdentifier(), scmRepo.getName());

		AuthUtils.assumeSystemIdentity(templateService.getProjectServiceProfile().getProject().getIdentifier());
		tenancyManager.establishTenancyContext(templateService);

		Git git = Git.cloneRepository().setBare(true).setDirectory(createTempDirectory()).setCloneAllBranches(true)
				.setURI(cloneUrl).call();

		AuthUtils.assumeSystemIdentity(targetProjectService.getProjectServiceProfile().getProject().getIdentifier());
		tenancyManager.establishTenancyContext(targetProjectService);

		String pushUrl = jgitProvider.computeRepositoryUrl(targetProjectService.getProjectServiceProfile().getProject()
				.getIdentifier(), scmRepo.getName());

		git.getRepository().getConfig().setString("remote", "target", "url", pushUrl);

		git.push().setRemote("target").setPushAll().call();
	}

	public static File createTempDirectory() throws IOException {
		File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

		if (!temp.delete()) {
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if (!temp.mkdir()) {
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return temp;
	}

	private List<ScmRepository> getTemplateRepositoriesToClone(ProjectService templateService) {
		AuthUtils.assumeSystemIdentity(templateService.getProjectServiceProfile().getProject().getIdentifier());
		ScmService templateScmService = scmServiceProvider.getService(templateService.getProjectServiceProfile()
				.getProject().getIdentifier());

		try {
			List<ScmRepository> templateRepos = templateScmService.getScmRepositories();
			List<ScmRepository> toClone = new ArrayList<ScmRepository>();

			for (ScmRepository repo : templateRepos) {
				if (repo.getType().equals(ScmType.GIT) && repo.getScmLocation().equals(ScmLocation.CODE2CLOUD)) {
					toClone.add(repo);
				}
			}

			return toClone;
		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
