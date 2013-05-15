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
package com.tasktop.c2c.server.common.service;

import java.util.Collections;
import java.util.List;

public class ValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	private List<String> messages;

	public ValidationException(List<String> messages) {
		super(computeMessage(messages));
		this.messages = messages;
	}

	public ValidationException(String message) {
		super(message);
		this.messages = Collections.singletonList(message);
	}

	private static String computeMessage(List<String> messages) {
		StringBuffer message = new StringBuffer();
		for (String m : messages) {
			if (message.length() > 0) {
				message.append(", ");
			}
			message.append(m);
		}
		return message.toString();
	}

	public List<String> getMessages() {
		return messages;
	}

}
