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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.AbstractJpaServiceBean;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.common.service.job.JobService;
import com.tasktop.c2c.server.internal.profile.service.InternalProfileService;
import com.tasktop.c2c.server.internal.profile.service.SecurityPolicy;
import com.tasktop.c2c.server.internal.profile.service.template.ProjectServiceCloner.CloneContext;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateOptions;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.service.ProjectTemplateService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Service
@Qualifier("main")
public class ProjectTemplateServiceImpl extends AbstractJpaServiceBean implements ProjectTemplateService,
		InternalProjectTemplateService {

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

		// Go through the source template and see what we can clone
		for (ProjectService sourceService : sourceTemplate.getProjectServiceProfile().getProjectServices()) {
			if (projectServiceCloner.canClone(sourceService)) {
				ProjectService targetProjectService = findTargetService(sourceService, targetProject);

				CloneContext cloneContext = new CloneContext();
				cloneContext.setTargetService(targetProjectService);
				cloneContext.setTemplateService(sourceService);
				cloneContext.setProperties(options.getProperties());
				cloneContext.setUser(user);

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
			List<ProjectTemplateProperty> properties) {
		AuthUtils.assumeSystemIdentity(null);

		ProjectService sourceService = entityManager.find(ProjectService.class, sourceProjectServiceId);
		ProjectService targetService = entityManager.find(ProjectService.class, targetProjectServiceId);
		Profile user = entityManager.find(Profile.class, userId);

		CloneContext context = new CloneContext();
		context.setTargetService(targetService);
		context.setTemplateService(sourceService);
		context.setUser(user);
		context.setProperties(properties);

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

		// Go through the source template and see what we can clone
		for (ProjectService sourceService : sourceTemplate.getProjectServiceProfile().getProjectServices()) {
			if (projectServiceCloner.canClone(sourceService)) {
				result.addAll(projectServiceCloner.getProperties(sourceService));
			}
		}
		return result;
	}
}
