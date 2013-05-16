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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.AbstractJpaServiceBean;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.JacksonJsonObjectMapper;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.common.service.job.JobService;
import com.tasktop.c2c.server.internal.profile.service.InternalProfileService;
import com.tasktop.c2c.server.internal.profile.service.SecurityPolicy;
import com.tasktop.c2c.server.internal.profile.service.template.ProjectServiceCloner.CloneContext;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata.BuildJobConfigReplacement;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata.GitFileReplacement;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateOptions;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.service.ProjectTemplateService;
import com.tasktop.c2c.server.profile.service.provider.ScmServiceProvider;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmType;
import com.tasktop.c2c.server.scm.service.ScmService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Service
@Qualifier("main")
public class ProjectTemplateServiceImpl extends AbstractJpaServiceBean implements ProjectTemplateService,
		InternalProjectTemplateService {

	/**
	 * Location of the template metadata file in the default git repository ({projectId}.git) of the template project.
	 * 
	 */
	public static final String TEMPLATE_METADATA_FILENAME = "/templateMetadata.json";

	/**
	 * Branch containing the template metadata file.
	 * 
	 */
	public static final String TEMPLATE_METADATA_BRANCH = "project-template-metadata";

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTemplateServiceImpl.class);

	@Autowired
	private InternalProfileService profileService;

	@Autowired
	@Qualifier("projectServiceClonerList")
	private ProjectServiceCloner projectServiceCloner;

	@Autowired
	private JobService jobService;

	@Autowired
	private SecurityPolicy securityPolicy;

	@Override
	public List<ProjectTemplate> listTemplates(ProjectsQuery projectsQuery) {

		QueryResult<Project> templateProjects = profileService.findProjects(projectsQuery, "project.template = true");

		List<ProjectTemplate> result = new ArrayList<ProjectTemplate>();
		for (Project p : templateProjects.getResultPage()) {
			ProjectTemplate template = new ProjectTemplate();
			template.setProjectId(p.getIdentifier());
			template.setName(p.getName());
			template.setDescription(p.getDescription());
			result.add(template);
		}

		return result;
	}

	@Override
	public void applyTemplateToProject(ProjectTemplateOptions options) throws EntityNotFoundException {

		Project targetProject = profileService.getProjectByIdentifier(options.getTargetProjectIdentifier());
		securityPolicy.modify(targetProject);

		Project sourceTemplate = profileService.getProjectByIdentifier(options.getTemplate().getProjectId());

		if (!sourceTemplate.isTemplate()) {
			throw new IllegalArgumentException("Project is not a template");
		}

		Profile user = profileService.getCurrentUserProfile();
		ProjectTemplateMetadata metadata = getTemplateMetadata(sourceTemplate);

		// Go through the source template and see what we can clone
		for (ProjectService sourceService : sourceTemplate.getProjectServiceProfile().getProjectServices()) {
			if (projectServiceCloner.canClone(sourceService)) {
				ProjectService targetProjectService = findTargetService(sourceService, targetProject);

				CloneContext cloneContext = new CloneContext();
				cloneContext.setTargetService(targetProjectService);
				cloneContext.setTemplateService(sourceService);
				cloneContext.setProperties(options.getProperties());
				cloneContext.setUser(user);
				cloneContext.setProjectTemplateMetadata(metadata);

				ProjectServiceCloneJob job = new ProjectServiceCloneJob(cloneContext);

				if (!projectServiceCloner.isReadyToClone(cloneContext)) {
					job.setDeliveryDelayInMilliseconds(10 * 1000l);
				}
				jobService.schedule(job);

			}
		}
	}

	private ProjectService findTargetService(ProjectService sourceService, Project targetProject) {
		for (ProjectService service : targetProject.getProjectServiceProfile().getProjectServices()) {
			if (service.getType().equals(sourceService.getType())) {
				return service;
			}
		}
		return null; // FIXME handle
	}

	@Override
	public void doCloneProjectService(Long sourceProjectServiceId, Long targetProjectServiceId, Long userId,
			List<ProjectTemplateProperty> properties, ProjectTemplateMetadata metadata) {
		AuthUtils.assumeSystemIdentity(null);

		ProjectService sourceService = entityManager.find(ProjectService.class, sourceProjectServiceId);
		ProjectService targetService = entityManager.find(ProjectService.class, targetProjectServiceId);
		Profile user = entityManager.find(Profile.class, userId);

		CloneContext context = new CloneContext();
		context.setTargetService(targetService);
		context.setTemplateService(sourceService);
		context.setUser(user);
		context.setProperties(properties);
		context.setProjectTemplateMetadata(metadata);

		if (!projectServiceCloner.isReadyToClone(context)) {
			ProjectServiceCloneJob job = new ProjectServiceCloneJob(context);

			job.setDeliveryDelayInMilliseconds(10 * 1000l);
			jobService.schedule(job);
			return;
		}

		projectServiceCloner.doClone(context);
	}

	@Override
	public List<ProjectTemplateProperty> getPropertiesForTemplate(ProjectTemplate template)
			throws EntityNotFoundException {
		List<ProjectTemplateProperty> result = new ArrayList<ProjectTemplateProperty>();

		Project sourceTemplate = profileService.getProjectByIdentifier(template.getProjectId());

		if (!sourceTemplate.isTemplate()) {
			throw new IllegalArgumentException("Project is not a template");
		}

		result.addAll(getMetadataProperties(sourceTemplate));

		// Go through the source template and see what we can clone
		for (ProjectService sourceService : sourceTemplate.getProjectServiceProfile().getProjectServices()) {
			if (projectServiceCloner.canClone(sourceService)) {
				result.addAll(projectServiceCloner.getProperties(sourceService));
			}
		}

		// TODO unique check. It could be possible that some cloners/metadata define the same properties (in terms of
		// id).
		// In this case, which one to use? (they could have different values, defaults, etc)

		return result;
	}

	@Autowired
	private ScmServiceProvider scmServiceProvider;

	@Autowired
	private JacksonJsonObjectMapper objectMapper;

	protected ProjectTemplateMetadata getTemplateMetadata(Project sourceTemplate) {

		ScmService service = scmServiceProvider.getService(sourceTemplate.getIdentifier());

		String repoName = computeRepoName(sourceTemplate, service);

		try {
			Blob jsonBlob = service.getBlob(repoName, TEMPLATE_METADATA_BRANCH, TEMPLATE_METADATA_FILENAME);
			String json = jsonBlob.getFullContent();
			ProjectTemplateMetadata metadata = parse(json);
			return metadata;

		} catch (EntityNotFoundException e) {
			LOGGER.trace("No project metadata found", e);
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error parsing project metadata ", e);
		} catch (IOException e) {
			LOGGER.warn("Error parsing project metadata ", e);
		}
		return null;
	}

	protected String computeRepoName(Project templateProject, ScmService service) {

		try {
			List<String> possibleRepoNames = new ArrayList<String>();
			List<ScmRepository> repos = service.getScmRepositories();
			for (ScmRepository repo : repos) {
				if (!repo.getType().equals(ScmType.GIT)) {
					continue;
				}
				if (!repo.getScmLocation().equals(ScmLocation.CODE2CLOUD)) {
					continue;
				}
				possibleRepoNames.add(repo.getName());
			}

			if (possibleRepoNames.size() == 1) {
				return possibleRepoNames.get(0);
			} else {
				String repoName = templateProject.getIdentifier() + ".git";
				if (possibleRepoNames.contains(repoName)) {
					return repoName;
				} else {
					return null;
				}
			}

		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected List<ProjectTemplateProperty> getMetadataProperties(Project sourceTemplate) {
		ProjectTemplateMetadata metadata = getTemplateMetadata(sourceTemplate);
		if (metadata == null) {
			return Collections.emptyList();
		}

		List<ProjectTemplateProperty> result = new ArrayList<ProjectTemplateProperty>();

		if (metadata.getFileReplacements() != null) {
			for (GitFileReplacement fileReplacement : metadata.getFileReplacements()) {
				result.add(fileReplacement.getProperty());
			}
		}

		if (metadata.getBuildJobReplacements() != null) {
			for (BuildJobConfigReplacement buildJobReplacement : metadata.getBuildJobReplacements()) {
				result.add(buildJobReplacement.getProperty());
			}
		}

		return result;
	}

	ProjectTemplateMetadata parse(String jsonContent) throws JsonProcessingException, IOException {
		return objectMapper.reader(ProjectTemplateMetadata.class).readValue(jsonContent);
	}
}
