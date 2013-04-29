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
package com.tasktop.c2c.server.tasks.client.widgets.tasklist;

import static com.tasktop.c2c.server.common.web.client.widgets.Format.stringValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.domain.Task;
import com.tasktop.c2c.server.tasks.domain.TaskFieldConstants;

public class OwnerColumn extends TaskColumnDescriptor {

	private TasksMessages tasksMessages = GWT.create(TasksMessages.class);

	@Override
	public String getLabel() {
		return tasksMessages.owner();
	}

	@Override
	public String getTaskField() {
		return TaskFieldConstants.ASSIGNEE_FIELD;
	}

	@Override
	protected Column<Task, ?> createColumn() {
		return new TextColumn<Task>() {
			@Override
			public String getValue(Task task) {
				return stringValue(task.getAssignee());
			}
		};
	}

}
