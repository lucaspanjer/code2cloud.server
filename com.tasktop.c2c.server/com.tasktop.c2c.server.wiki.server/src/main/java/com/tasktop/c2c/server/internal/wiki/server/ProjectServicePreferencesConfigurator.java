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
package com.tasktop.c2c.server.internal.wiki.server;

import org.springframework.beans.factory.annotation.Autowired;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Configurator;
import com.tasktop.c2c.server.wiki.service.WikiService;
public class ProjectServicePreferencesConfigurator implements Configurator {

	@Autowired
	WikiService wikiService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.configuration.service.NodeConfigurationServiceBean.Configurator#configure(org.
	 * cloudfoundry.code.server.configuration.service.NodeConfigurationService.NodeConfiguration)
	 */
	@Override
	public void configure(ProjectServiceConfiguration configuration) {
		String markupLanguage = configuration.getProperties().get(ProjectServiceConfiguration.MARKUP_LANGUAGE);

		TenancyUtil.setProjectTenancyContext(configuration.getProjectIdentifier());
		AuthUtils.assumeSystemIdentity(null);

		wikiService.setConfigurationProperty(WikiService.MARKUP_LANGUAGE_DB_KEY, markupLanguage);
	}
}
