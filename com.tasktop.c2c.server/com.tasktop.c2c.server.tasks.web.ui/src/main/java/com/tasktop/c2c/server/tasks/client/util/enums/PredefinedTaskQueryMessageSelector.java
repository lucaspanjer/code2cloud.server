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
import com.tasktop.c2c.server.tasks.domain.PredefinedTaskQuery;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class PredefinedTaskQueryMessageSelector implements
		EnumClientMessageSelector<PredefinedTaskQuery, TasksMessages> {

	@Override
	public String getInternationalizedMessage(PredefinedTaskQuery predefinedTaskQuery, TasksMessages tasksMessages) {
		switch (predefinedTaskQuery) {
		case ALL:
			return tasksMessages.allTasks();
		case MINE:
			return tasksMessages.assignedToMe();
		case OPEN:
			return tasksMessages.openTasks();
		case RECENT:
			return tasksMessages.recentlyChanged();
		case RELATED:
			return tasksMessages.relatedToMe();
		default:
			return "";
		}
	}

}
