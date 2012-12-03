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
package com.tasktop.c2c.server.profile.web.ui.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tasktop.c2c.server.common.service.web.AbstractRestService;
import com.tasktop.c2c.server.profile.domain.project.ProjectArtifacts;
import com.tasktop.c2c.server.profile.service.ProjectArtifactService;
import com.tasktop.c2c.server.profile.service.ProjectArtifactServiceClient;

@Controller
public class ProjectArtifactServiceController extends AbstractRestService {

	private static ProjectArtifactServiceController lastInstance = null;

	/** For testing. */
	public static void setLastInstantiatedService(ProjectArtifactService service) {
		lastInstance.projectArtifactService = service;
	}

	@Autowired
	@Qualifier("main")
	private ProjectArtifactService projectArtifactService;

	public void setProjectArtifactService(ProjectArtifactService projectArtifactService) {
		this.projectArtifactService = projectArtifactService;
	}

	public ProjectArtifactServiceController() {
		lastInstance = this;
	}

	@RequestMapping(value = ProjectArtifactServiceClient.ARTIFACT_LIST_URL, method = RequestMethod.GET)
	public List<ProjectArtifacts> getArtifacts(@PathVariable("projectIdentifier") String projectIdentifier,
			@RequestParam(value = "nameRegexp", required = false) String regExp) {
		if (regExp == null) {
			return projectArtifactService.listProjectArtifacts(projectIdentifier);
		} else {
			return projectArtifactService.listProjectArtifacts(projectIdentifier, regExp);
		}
	}

}
