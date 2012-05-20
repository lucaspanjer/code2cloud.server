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
package com.tasktop.c2c.server.scm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.tenancy.context.TenancyContextHolder;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Configurator;

@Component
public class GitRepositoryConfigurator implements Configurator {

	private static final Logger LOG = LoggerFactory.getLogger(GitRepositoryConfigurator.class.getName());

	@Autowired
	private GitService gitService;

	@Override
	public void configure(ProjectServiceConfiguration configuration) {
		String gitRepositoryName = configuration.getProperties().get(
				ProjectServiceConfiguration.APPLICATION_GIT_PROPERTY);
		if (gitRepositoryName == null) {
			return;
		}
		try {
			TenancyUtil.setProjectTenancyContext(configuration.getProjectIdentifier());
			AuthUtils.assumeSystemIdentity(null);

			gitService.createEmptyRepository(gitRepositoryName);
		} finally {
			TenancyContextHolder.clearContext();
		}
	}

}