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
package com.tasktop.c2c.server.configuration.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.doc.Documentation;
import com.tasktop.c2c.server.common.service.doc.Title;
import com.tasktop.c2c.server.common.service.web.AbstractBuildInfoRestService;

@Title("Project-Service Management Service")
@Documentation("An internal service used to manage a projects services. This includes creating, getting status, and removing.")
@Controller
public class ProjectServiceManagementServiceController extends AbstractBuildInfoRestService {

	private ProjectServiceManagementService projectServiceManagementService;

	public void setProjectServiceManagementService(ProjectServiceManagementService nodeConfigurationService) {
		this.projectServiceManagementService = nodeConfigurationService;
	}

	@RequestMapping(value = "/provision", method = RequestMethod.POST)
	public void provisionService(@RequestBody ProjectServiceConfiguration configuration) {
		projectServiceManagementService.provisionService(configuration);
	}

	@RequestMapping(value = "/deprovision", method = RequestMethod.POST)
	public void deprovisionService(@RequestBody ProjectServiceConfiguration configuration) {
		projectServiceManagementService.deprovisionService(configuration);
	}

	@RequestMapping(value = "/status/{projectIdentifier}/{serviceType}", method = RequestMethod.GET)
	public ProjectServiceStatus retrieveServiceStatus(@PathVariable("projectIdentifier") String projectIdentifer,
			@PathVariable("serviceType") ServiceType serviceType) {
		return projectServiceManagementService.retrieveServiceStatus(projectIdentifer, serviceType);
	}

}
