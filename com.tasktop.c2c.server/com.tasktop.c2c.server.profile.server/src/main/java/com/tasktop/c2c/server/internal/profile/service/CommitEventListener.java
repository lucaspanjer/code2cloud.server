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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.event.domain.PushEvent;
import com.tasktop.c2c.server.event.domain.Event;
import com.tasktop.c2c.server.event.service.EventListener;
import com.tasktop.c2c.server.profile.service.provider.TaskServiceProvider;
import com.tasktop.c2c.server.tasks.service.TaskService;

/**
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
@Component
public class CommitEventListener implements EventListener {

	@Autowired
	private TaskServiceProvider taskServiceProvider;

	@Override
	public void onEvent(Event event) {
		if (!(event instanceof PushEvent)) {
			return;
		}
		PushEvent commitEvent = (PushEvent) event;

		TaskService taskService = taskServiceProvider.getTaskService(event.getProjectId());
		CommitToTaskLinker taskLinker = new CommitToTaskLinker(event.getProjectId(), taskService);
		taskLinker.process(commitEvent.getCommits());

	}
}
