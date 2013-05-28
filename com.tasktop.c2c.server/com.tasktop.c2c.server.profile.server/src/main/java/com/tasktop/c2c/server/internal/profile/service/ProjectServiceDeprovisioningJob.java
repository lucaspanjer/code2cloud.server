package com.tasktop.c2c.server.internal.profile.service;

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.job.Job;
import com.tasktop.c2c.server.common.service.job.JobService;

@SuppressWarnings("serial")
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
		InternalProjectServiceService service = applicationContext.getBean(InternalProjectServiceService.class);
		try {
			AuthUtils.assumeSystemIdentity(projectId);
			service.doDeprovisionService(projectServiceId);
			JobService jobService = applicationContext.getBean("jobService", JobService.class);
			jobService.schedule(new ProjectDeletionJob(projectId));
		} catch (EntityNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

}
