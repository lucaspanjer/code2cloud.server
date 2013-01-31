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
package com.tasktop.c2c.server.tasks.client.widgets.admin.customfields;

import com.google.gwt.core.client.GWT;
import com.google.gwt.text.shared.AbstractRenderer;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.client.util.enums.FieldTypeMessageSelector;
import com.tasktop.c2c.server.tasks.domain.FieldType;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public final class FieldTypeRender extends AbstractRenderer<FieldType> {
	private static FieldTypeRender instance = null;

	public static FieldTypeRender getInstance() {
		if (instance == null) {
			instance = new FieldTypeRender();
		}
		return instance;
	}

	@Override
	public String render(FieldType fieldType) {
		if (fieldType == null) {
			return "";
		}
		TasksMessages tasksMessages = GWT.create(TasksMessages.class);
		return new FieldTypeMessageSelector().getInternationalizedMessage(fieldType, tasksMessages);
	}
}
