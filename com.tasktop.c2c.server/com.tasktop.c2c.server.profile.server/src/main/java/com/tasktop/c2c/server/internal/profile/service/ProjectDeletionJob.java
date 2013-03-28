package com.tasktop.c2c.server.internal.profile.service;

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.job.Job;

public class ProjectDeletionJob extends Job {

	private String projectId;

	/**
	 * @param id
	 */
	public ProjectDeletionJob(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		InternalProfileService service = applicationContext.getBean(InternalProfileService.class);
		AuthUtils.assumeSystemIdentity(projectId);
		service.doDeleteProjectIfReady(projectId);

	}

}
