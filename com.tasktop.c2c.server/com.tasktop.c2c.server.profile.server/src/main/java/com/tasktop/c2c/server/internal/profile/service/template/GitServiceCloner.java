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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.internal.profile.service.ServiceAwareTenancyManager;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata.GitFileReplacement;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;
import com.tasktop.c2c.server.profile.service.provider.InternalJgitProvider;
import com.tasktop.c2c.server.profile.service.provider.ScmServiceProvider;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmType;
import com.tasktop.c2c.server.scm.service.ScmService;

/**
 * Clones the master branch of each repository in the template project, drops the git history, creates a single commit
 * for the target project.
 * 
 * Also does any rewriting specified by the projectTemplateMetadata.
 * 
 * 
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

	@Value("${project.template.git.commiterEmail}")
	private String committerEmail;

	@Value("${project.template.git.commiterName}")
	private String committerName;

	@Autowired
	private MessageSource messageSource;

	/**
	 * @param serviceType
	 */
	protected GitServiceCloner() {
		super(ServiceType.SCM);
	}

	@Override
	public void doClone(CloneContext context) {
		List<ScmRepository> templateReposToClone = getTemplateRepositoriesToClone(context.getTemplateService());

		AuthUtils.assumeSystemIdentity(context.getTargetService().getProjectServiceProfile().getProject()
				.getIdentifier());
		ScmService targetScmService = scmServiceProvider.getService(context.getTargetService()
				.getProjectServiceProfile().getProject().getIdentifier());

		List<ScmRepository> createdRepos = new ArrayList<ScmRepository>();
		for (ScmRepository repo : templateReposToClone) {
			try {
				ScmRepository created = targetScmService.createScmRepository(repo);
				createdRepos.add(created);
			} catch (EntityNotFoundException e) {
				LOGGER.warn(
						String.format("Error creating repo [%s] from template project [%s] in project [%s]",
								repo.getName(), context.getTemplateService().getProjectServiceProfile().getProject()
										.getIdentifier(), context.getTargetService().getProjectServiceProfile()
										.getProject().getIdentifier()), e);
			} catch (ValidationException e) {
				LOGGER.warn(
						String.format("Error creating repo [%s] from template project [%s] in project [%s]",
								repo.getName(), context.getTemplateService().getProjectServiceProfile().getProject()
										.getIdentifier(), context.getTargetService().getProjectServiceProfile()
										.getProject().getIdentifier()), e);
			}
		}

		for (ScmRepository repo : createdRepos) {
			try {
				copyRepo(repo, context);
			} catch (Exception e) {
				LOGGER.warn(
						String.format("Error copying repo [%s] from template project [%s] in project [%s]",
								repo.getName(), context.getTemplateService().getProjectServiceProfile().getProject()
										.getIdentifier(), context.getTargetService().getProjectServiceProfile()
										.getProject().getIdentifier()), e);
			}
		}
	}

	private void copyRepo(ScmRepository scmRepo, CloneContext context) throws IOException, JGitInternalException,
			InvalidRemoteException, NoFilepatternException, NoHeadException, NoMessageException,
			ConcurrentRefUpdateException, WrongRepositoryStateException {

		File workDirectory = null;
		try {
			Project templateProject = context.getTemplateService().getProjectServiceProfile().getProject();
			String cloneUrl = jgitProvider.computeRepositoryUrl(templateProject.getIdentifier(), scmRepo.getName());

			AuthUtils.assumeSystemIdentity(templateProject.getIdentifier());
			tenancyManager.establishTenancyContext(context.getTemplateService());

			workDirectory = createTempDirectory();
			Git git = Git.cloneRepository().setDirectory(workDirectory).setBranch(Constants.R_HEADS + Constants.MASTER)
					.setURI(cloneUrl).call();

			AuthUtils.assumeSystemIdentity(context.getTargetService().getProjectServiceProfile().getProject()
					.getIdentifier());
			tenancyManager.establishTenancyContext(context.getTargetService());

			FileUtils.deleteDirectory(git.getRepository().getDirectory());

			git = Git.init().setDirectory(git.getRepository().getDirectory().getParentFile()).call();

			maybeRewriteRepo(workDirectory, context);

			String pushUrl = jgitProvider.computeRepositoryUrl(context.getTargetService().getProjectServiceProfile()
					.getProject().getIdentifier(), scmRepo.getName());

			// FIXME: User's locale is not defined here
			String commitMessage = messageSource.getMessage("project.template.git.commitMessage",
					new Object[] { templateProject.getName() }, null);

			git.add().addFilepattern(".").call();
			git.commit().setCommitter(committerName, committerEmail).setMessage(commitMessage).call();
			git.getRepository().getConfig().setString("remote", "target", "url", pushUrl);
			git.push().setRemote("target").setPushAll().call();
		} finally {
			if (workDirectory != null) {
				FileUtils.deleteDirectory(workDirectory);
			}
		}
	}

	/**
	 * @param directory
	 * @param context
	 * @throws IOException
	 */
	private void maybeRewriteRepo(File directory, CloneContext context) throws IOException {
		if (context.getProjectTemplateMetadata() == null
				|| context.getProjectTemplateMetadata().getFileReplacements() == null) {
			return;
		}
		for (GitFileReplacement fileReplacement : context.getProjectTemplateMetadata().getFileReplacements()) {

			ProjectTemplateProperty property = context.getProperty(fileReplacement.getProperty().getId());

			if (property == null || property.getValue() == null) {
				throw new IllegalStateException("Missing property: " + fileReplacement.getProperty().getId());
			}

			IOFileFilter fileFilter;

			if (fileReplacement.getFileName() != null) {
				fileFilter = new NameFileFilter(fileReplacement.getFileName());
			} else if (fileReplacement.getFilePattern() != null) {
				fileFilter = new RegexFileFilter(fileReplacement.getFilePattern());
			} else {
				fileFilter = TrueFileFilter.INSTANCE;
			}

			IOFileFilter dirFilter = new NotFileFilter(new NameFileFilter(".git"));
			for (File file : FileUtils.listFiles(directory, fileFilter, dirFilter)) {
				// REVIEW, this rewrites the content, by streaming the entire process into memory.
				String content = FileUtils.readFileToString(file);
				String rewrittenContent = content.replace(fileReplacement.getPatternToReplace(), property.getValue());
				FileUtils.write(file, rewrittenContent);
			}
		}

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
