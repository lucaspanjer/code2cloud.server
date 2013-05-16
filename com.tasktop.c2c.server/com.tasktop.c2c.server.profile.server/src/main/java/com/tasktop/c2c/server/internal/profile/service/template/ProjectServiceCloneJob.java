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

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.common.service.job.Job;
import com.tasktop.c2c.server.internal.profile.service.template.ProjectServiceCloner.CloneContext;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class ProjectServiceCloneJob extends Job {

	private Long userId;
	private Long sourceProjectServiceId;
	private Long targetProjectServiceId;
	private List<ProjectTemplateProperty> properties;
	private ProjectTemplateMetadata metadata;

	/**
	 * @param sourceProjectServiceId
	 * @param targetProjectServiceId
	 */
	public ProjectServiceCloneJob(CloneContext context) {
		this.sourceProjectServiceId = context.getTemplateService().getId();
		this.targetProjectServiceId = context.getTargetService().getId();
		this.userId = context.getUser().getId();
		this.properties = context.getProperties();
		this.metadata = context.getProjectTemplateMetadata();
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		applicationContext.getBean(InternalProjectTemplateService.class).doCloneProjectService(sourceProjectServiceId,
				targetProjectServiceId, userId, properties, metadata);
	}

}
