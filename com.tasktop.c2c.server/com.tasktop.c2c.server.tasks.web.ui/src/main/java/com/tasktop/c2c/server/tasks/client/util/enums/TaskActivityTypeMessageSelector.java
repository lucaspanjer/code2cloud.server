/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.tasks.client.util.enums;

import com.tasktop.c2c.server.common.web.client.util.enums.EnumClientMessageSelector;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.domain.TaskActivity.Type;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class TaskActivityTypeMessageSelector implements EnumClientMessageSelector<Type, TasksMessages> {

	@Override
	public String getInternationalizedMessage(Type type, TasksMessages tasksMessages) {
		switch (type) {
		case ATTACHED:
			return tasksMessages.attachedTo();
		case COMMENTED:
			return tasksMessages.commentedOn();
		case CREATED:
			return tasksMessages.created();
		case LOGGED_TIME:
			return tasksMessages.loggedTimeOn();
		case UPDATED:
			return tasksMessages.updated();
		default:
			return "";
		}
	}

}
