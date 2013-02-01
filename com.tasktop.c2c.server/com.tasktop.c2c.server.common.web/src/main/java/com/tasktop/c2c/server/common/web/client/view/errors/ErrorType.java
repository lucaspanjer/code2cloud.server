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
package com.tasktop.c2c.server.common.web.client.view.errors;

public enum ErrorType {

	ERROR404("four-oh-four"), ERROR500("five-hundred"), ERROR503("five-oh-three");

	private String errorImageClass;

	ErrorType(String errorImageClass) {
		this.errorImageClass = errorImageClass;
	}

	public String getErrorImageClass() {
		return errorImageClass;
	}
}
