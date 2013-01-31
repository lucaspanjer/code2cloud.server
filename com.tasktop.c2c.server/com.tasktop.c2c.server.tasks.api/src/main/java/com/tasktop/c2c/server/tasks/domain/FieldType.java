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
package com.tasktop.c2c.server.tasks.domain;

public enum FieldType {
	/**
	 * a single-line text field
	 */
	TEXT,
	/**
	 * a single-select field
	 */
	SINGLE_SELECT,
	/**
	 * multi-select
	 */
	MULTI_SELECT,
	/**
	 * long text
	 */
	LONG_TEXT,
	/**
	 * for date/time
	 */
	TIMESTAMP,
	/**
	 * a reference to a task (ie: it's id)
	 */
	TASK_REFERENCE,
	/**
	 * a checkbox field
	 */
	CHECKBOX;

}
