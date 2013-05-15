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
package com.tasktop.c2c.server.scm.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.tasktop.c2c.server.common.service.AbstactServiceBean;
import com.tasktop.c2c.server.common.service.BaseProfileConfiguration;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.scm.domain.Blame;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.domain.ScmType;
import com.tasktop.c2c.server.scm.domain.Trees;

@Service("scmService")
@Qualifier("main")
public class ScmServiceBean extends AbstactServiceBean implements ScmService {

	@Autowired
	private BaseProfileConfiguration profileServiceConfiguration;

	@Autowired
	private GitService gitService;

	@Override
	public List<ScmRepository> getScmRepositories() {
		return gitService.listRepositories();
	}

	@Secured(Role.Admin)
	@Override
	public ScmRepository createScmRepository(ScmRepository repository) throws EntityNotFoundException,
			ValidationException {

		String internalUrlPrefix = profileServiceConfiguration.getHostedScmUrlPrefix(TenancyUtil
				.getCurrentTenantProjectIdentifer());

		// Provide Defaults
		if (ScmLocation.CODE2CLOUD.equals(repository.getScmLocation()) && repository.getName() != null) {
			if (!repository.getName().endsWith(".git")) {
				Errors errors = createErrors(repository);
				errors.reject("scmrepo.internal.nameMustEndWithGit");
				super.throwValidationException(errors);
			} else if (repository.getName().equals(".git")) {
				Errors errors = createErrors(repository);
				errors.reject("scmrepo.internal.nameEmpty");
				super.throwValidationException(errors);
			}
			repository.setUrl(internalUrlPrefix + repository.getName());
		}

		// Validate the internal object.
		validate(repository);

		if (findRepositoryByUrl(repository.getUrl()) != null) {
			Errors errors = createErrors(repository);
			errors.reject("scmrepo.urlExists");
			super.throwValidationException(errors);
		}
		if (ScmLocation.EXTERNAL.equals(repository.getScmLocation())
				&& repository.getUrl().startsWith(internalUrlPrefix)) {
			Errors errors = createErrors(repository);
			errors.reject("scmrepo.external.url.isInternal");
			super.throwValidationException(errors);
		}
		if (ScmLocation.CODE2CLOUD.equals(repository.getScmLocation())
				&& !repository.getUrl().startsWith(internalUrlPrefix)) {
			Errors errors = createErrors(repository);
			errors.reject("scmrepo.internal.url.isExternal");
			super.throwValidationException(errors);
		}
		if (!ScmType.GIT.equals(repository.getType())) {
			throw new IllegalStateException("only git repos supported");
		}

		if (repository.getScmLocation().equals(ScmLocation.EXTERNAL)) {
			gitService.addExternalRepository(repository.getUrl());
		} else {
			gitService.createEmptyRepository(repository.getName());
		}

		return repository;
	}

	/**
	 * @param url
	 * @param project
	 * @return
	 */
	private ScmRepository findRepositoryByUrl(String url) {
		for (ScmRepository repo : getScmRepositories()) {
			if (repo.getUrl().equals(url)) {
				return repo;
			}
		}
		return null;
	}

	@Secured(Role.Admin)
	@Override
	public void deleteScmRepository(ScmRepository repo) throws EntityNotFoundException {

		ScmRepository curRepo = findRepositoryByUrl(repo.getUrl()); // FIXME change API?

		if (curRepo == null) {
			throw new EntityNotFoundException();
		}

		if (curRepo.getScmLocation().equals(ScmLocation.EXTERNAL)) {
			gitService.removeExternalRepository(curRepo.getUrl());
		} else {
			String repoUrlPrefix = profileServiceConfiguration.getHostedScmUrlPrefix(TenancyUtil
					.getCurrentTenantProjectIdentifer());
			if (!curRepo.getUrl().startsWith(repoUrlPrefix)) {
				throw new IllegalStateException("Could not remove internal repo, its url is in unexpected format");
			}
			String repoName = curRepo.getUrl().substring(repoUrlPrefix.length());
			gitService.removeInternalRepository(repoName);
		}

	}

	@Override
	public Map<Profile, Integer> getNumCommitsByAuthor(int numDays) {
		return gitService.getNumCommitsByAuthor(numDays);
	}

	@Override
	public List<ScmSummary> getScmSummary(int numDays) {
		return gitService.getScmSummary(numDays);
	}

	@Override
	public List<Commit> getLog(Region region) {
		return gitService.getLog(region);
	}

	@Override
	public Commit getCommit(String repoName, String commitId, Integer context) throws EntityNotFoundException {
		return gitService.getCommitWithDiff(repoName, commitId, context);
	}

	@Override
	public List<Commit> getLog(String repoName, Region region) throws EntityNotFoundException {
		return gitService.getLog(repoName, region);
	}

	@Override
	public List<Commit> getLog(String repoName, String revision, String path, Region region)
			throws EntityNotFoundException {
		return gitService.getLog(repoName, revision, path, region);
	}

	@Override
	public List<Commit> getLogForBranch(String repoName, String branchName, Region region)
			throws EntityNotFoundException {
		return gitService.getLogForBranch(repoName, branchName, region);
	}

	@Override
	public String createBranch(String repoName, String branchName) throws EntityNotFoundException {
		return gitService.createBranch(repoName, branchName);
	}

	@Override
	public void deleteBranch(String repoName, String branchName) throws EntityNotFoundException {
		gitService.deleteBranch(repoName, branchName);
	}

	@Override
	public Trees getTrees(String repoName, String revision, String path, boolean history, int recursion)
			throws EntityNotFoundException {
		return gitService.getTrees(repoName, revision, path, history, recursion);
	}

	@Override
	public Blob getBlob(String repoName, String revision, String path) throws EntityNotFoundException {
		return gitService.getBlob(repoName, revision, path);
	}

	@Override
	public Blame getBlame(String repoName, String revision, String path) throws EntityNotFoundException {
		return gitService.getBlame(repoName, revision, path);
	}

	@Override
	public Item getItem(String repoName, String revision, String path) throws EntityNotFoundException {
		return gitService.getItem(repoName, revision, path);
	}

	@Override
	public String getPublicSshKey() {
		return gitService.getPublicSshKey();
	}

}
