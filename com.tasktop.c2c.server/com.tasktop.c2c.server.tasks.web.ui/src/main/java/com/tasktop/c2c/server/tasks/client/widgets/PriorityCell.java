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
package com.tasktop.c2c.server.tasks.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.domain.Task;

public class PriorityCell extends AbstractCell<Task> {

	private TasksMessages tasksMessages = GWT.create(TasksMessages.class);

	public PriorityCell() {
	}

	@Override
	public void render(Context context, Task task, SafeHtmlBuilder sb) {
		Integer id = task.getPriority().getId();
		switch (id) {
		case 1:
			sb.append(tasksMessages.highestP5Div());
			break;
		case 2:
			sb.append(tasksMessages.highP4Div());
			break;
		case 3:
			sb.append(tasksMessages.normalP3Div());
			break;
		case 4:
			sb.append(tasksMessages.lowP2Div());
			break;
		case 5:
			sb.append(tasksMessages.lowestP1Div());
			break;
		default:
			sb.append(tasksMessages.normalP3Div());
			break;
		}
	}
}
