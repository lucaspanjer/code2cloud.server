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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.domain.Task;

public class SeverityCell extends AbstractCell<Task> {

	private TasksMessages tasksMessages = GWT.create(TasksMessages.class);

	private final SafeHtml[] htmlBySeverity;

	public SeverityCell() {
		super();
		htmlBySeverity = new SafeHtml[] {
				null, // no 0
				tasksMessages.criticalS5Div(), tasksMessages.criticalS5Div(), tasksMessages.majorS4Div(),
				tasksMessages.normalS3Div(), tasksMessages.trivialS2Div(), tasksMessages.trivialS2Div(),
				tasksMessages.enhancementS1Div() };
	}

	@Override
	public void render(Context context, Task task, SafeHtmlBuilder sb) {
		Integer id = task.getSeverity().getId();
		if (id < htmlBySeverity.length) {
			sb.append(htmlBySeverity[id]);
		}
	}
}
