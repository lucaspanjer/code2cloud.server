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
package com.tasktop.c2c.server.common.web.client.notification;

public class OperationMessage {

	private Message progress;
	private Message success;

	public OperationMessage(String inProgressText) {
		setInProgressText(inProgressText);
	}

	public OperationMessage(Message inProgressMessage) {
		progress = inProgressMessage;
	}

	public static OperationMessage create(String inProgressText) {
		return new OperationMessage(inProgressText);
	}

	public OperationMessage setInProgressText(String inProgressText) {
		this.progress = new Message(0, inProgressText, Message.MessageType.PROGRESS);
		return this;
	}

	public OperationMessage setSuccessText(String successText) {
		this.success = new Message(10, successText, Message.MessageType.SUCCESS);
		return this;
	}

	public Message getSuccessMessage() {
		return success;
	}

	public Message getProgressMessage() {
		return progress;
	}
}
