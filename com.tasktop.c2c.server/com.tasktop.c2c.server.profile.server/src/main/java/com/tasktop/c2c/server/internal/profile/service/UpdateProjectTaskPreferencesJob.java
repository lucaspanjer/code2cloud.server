/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.service.provider.TaskServiceProvider;
import com.tasktop.c2c.server.tasks.service.TaskService;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class UpdateProjectTaskPreferencesJob extends AbstractUpdateProjectPreferencesJob {

	private static final long serialVersionUID = 1L;

	public UpdateProjectTaskPreferencesJob(Project project, String key) {
		super(project, key);
	}

	@Override
	public void execute(final ApplicationContext applicationContext) {
		executeAsSystem(applicationContext, getProjectIdentifier(), new Runnable() {
			@Override
			public void run() {
				TaskServiceProvider taskServiceProvider = applicationContext.getBean("taskServiceProvider",
						TaskServiceProvider.class);
				TaskService taskService = taskServiceProvider.getTaskService(getProjectIdentifier());
				taskService.setConfigurationProperty(getKey(), getValue());
			}
		});
	}

}
