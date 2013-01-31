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

import com.tasktop.c2c.server.common.profile.web.client.util.enums.EnumClientMessageSelector;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.domain.FieldType;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class FieldTypeMessageSelector implements EnumClientMessageSelector<FieldType, TasksMessages> {

	@Override
	public String getInternationalizedMessage(FieldType fieldType, TasksMessages tasksMessages) {
		switch (fieldType) {
		case CHECKBOX:
			return tasksMessages.fieldTypeCheckbox();
		case LONG_TEXT:
			return tasksMessages.fieldTypeLongText();
		case MULTI_SELECT:
			return tasksMessages.fieldTypeMultiSelect();
		case SINGLE_SELECT:
			return tasksMessages.fieldTypeSingleSelect();
		case TASK_REFERENCE:
			return tasksMessages.fieldTypeTaskReference();
		case TEXT:
			return tasksMessages.fieldTypeText();
		case TIMESTAMP:
			return tasksMessages.fieldTypeTimestamp();
		default:
			return "";
		}
	}

}
