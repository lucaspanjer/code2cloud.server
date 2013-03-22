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
package com.tasktop.c2c.server.profile.web.ui.server.action;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.domain.build.BuildSummary;
import com.tasktop.c2c.server.profile.domain.build.HudsonStatus;
import com.tasktop.c2c.server.profile.domain.build.JobDetails;
import com.tasktop.c2c.server.profile.domain.build.JobSummary;
import com.tasktop.c2c.server.profile.service.HudsonService;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsResult;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GetProjectBuildInformationActionHandler extends
		AbstractDeploymentActionHandler<GetProjectBuildsAction, GetProjectBuildsResult> {

	@Override
	public GetProjectBuildsResult execute(GetProjectBuildsAction action, ExecutionContext context)
			throws DispatchException {
		setTenancyContext(action.getProjectId());

		HudsonService hudsonService = hudsonServiceProvider.getHudsonService(action.getProjectId());
		List<String> buildJobNames = null;
		List<BuildDetails> builds = null;

		HudsonStatus status = hudsonServiceProvider.getHudsonService(action.getProjectId()).getStatus();
		buildJobNames = new ArrayList<String>(status.getJobs().size());
		for (JobSummary job : status.getJobs()) {
			buildJobNames.add(job.getName());
		}

		if (action.getJobName() != null) {
			JobDetails jobDetails = hudsonService.getJobDetails(action.getJobName());

			builds = new ArrayList<BuildDetails>(jobDetails.getBuilds().size());
			for (BuildSummary buildSummary : jobDetails.getBuilds()) {
				BuildDetails details = hudsonService.getBuildDetails(action.getJobName(), buildSummary.getNumber());
				if (details.getBuilding()) {
					continue; // Don't add pending builds
				}
				details.setActions(null); // Un needed

				builds.add(details);
			}
		}

		return new GetProjectBuildsResult(buildJobNames, builds);

	}

}
