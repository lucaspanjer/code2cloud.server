package com.tasktop.c2c.server.internal.profile.service;

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.job.Job;

public class ProjectServiceDeprovisioningJob extends Job {

	private String projectId;
	private Long projectServiceId;

	/**
	 * @param id
	 */
	public ProjectServiceDeprovisioningJob(String projectId, Long projectServiceId) {
		this.projectId = projectId;
		this.projectServiceId = projectServiceId;
	}

	@Override
	public void execute(ApplicationContext applicationContext) {
		InternalProfileService service = applicationContext.getBean(InternalProfileService.class);
		try {
			AuthUtils.assumeSystemIdentity(projectId);
			service.doDeprovisionServiceAndDeleteProjectIfReady(projectId, projectServiceId);
		} catch (EntityNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

}
