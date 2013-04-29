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
package com.tasktop.c2c.server.tasks.client.widgets.chooser.task;

import com.google.gwt.core.client.GWT;
import com.tasktop.c2c.server.common.web.client.widgets.chooser.AbstractValueChooser;
import com.tasktop.c2c.server.common.web.client.widgets.chooser.AbstractValueComposite;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.domain.Task;

/**
 * @author Jennifer Hickey
 * 
 */
public class TaskValueComposite extends AbstractValueComposite<Task> {

	private TasksMessages tasksMessages = GWT.create(TasksMessages.class);

	public TaskValueComposite(AbstractValueChooser<Task> chooser) {
		super(chooser);
	}

	@Override
	protected String computeValueLabel() {
		// TODO use task type?
		return tasksMessages.taskWithIdAndShortDescription(value.getId(), value.getShortDescription());
	}

	@Override
	protected String computeItemStyleName() {
		return "person " + super.computeItemStyleName();
	}

}
