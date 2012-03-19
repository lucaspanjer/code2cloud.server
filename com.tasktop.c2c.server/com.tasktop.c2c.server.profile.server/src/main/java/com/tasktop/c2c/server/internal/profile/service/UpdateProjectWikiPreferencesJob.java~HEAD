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
package com.tasktop.c2c.server.internal.profile.service;

import org.springframework.context.ApplicationContext;

import com.tasktop.c2c.server.auth.service.job.SystemJob;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.service.provider.WikiServiceProvider;
import com.tasktop.c2c.server.wiki.service.WikiService;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class UpdateProjectWikiPreferencesJob extends SystemJob {

	private static final long serialVersionUID = 1L;

	private final String value;
	private final String projectIdentifier;
	private final String key;

	public UpdateProjectWikiPreferencesJob(Project project, String key) {
		value = project.getProjectPreferences().getWikiLanguage().toString();
		projectIdentifier = project.getIdentifier();
		this.key = key;
		setType(Type.SHORT);
	}

	@Override
	public void execute(final ApplicationContext applicationContext) {
		executeAsSystem(applicationContext, projectIdentifier, new Runnable() {
			@Override
			public void run() {
				WikiServiceProvider wikiServiceProvider = applicationContext.getBean("wikiServiceProvider",
						WikiServiceProvider.class);
				WikiService wikiService = wikiServiceProvider.getWikiService(projectIdentifier);
				wikiService.setConfigurationProperty(key, value);

			}
		});
	}

}
