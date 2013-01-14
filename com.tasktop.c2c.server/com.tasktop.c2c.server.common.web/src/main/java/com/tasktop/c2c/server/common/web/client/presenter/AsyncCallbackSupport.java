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
package com.tasktop.c2c.server.common.web.client.presenter;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasEnabled;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.notification.Notifier;
import com.tasktop.c2c.server.common.web.client.notification.OperationMessage;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;
import com.tasktop.c2c.server.common.web.client.view.CommonGinjector;
import com.tasktop.c2c.server.common.web.client.view.ErrorCapableView;
import com.tasktop.c2c.server.common.web.client.view.errors.ErrorHandler;

public abstract class AsyncCallbackSupport<T> implements AsyncCallback<T> {

	public static final Message LOADING_MESSSAGE = new Message(0, "Loading...", Message.MessageType.PROGRESS);

	public interface ErrorInterpreter {
		List<String> getErrorMessages(Throwable exception);
	}

	private static ErrorInterpreter errorInterpreter;

	public static void setErrorInterpreter(ErrorInterpreter interpreter) {
		errorInterpreter = interpreter;

	}

	private OperationMessage operationMessage;
	private ErrorCapableView overrideErrorView;
	private Notifier notifier = CommonGinjector.get.instance().getNotifier();
	private ErrorHandler errorHandler = CommonGinjector.get.instance().getErrorHandler();

	public AsyncCallbackSupport() {
		setup(null, null, null);
	}

	public AsyncCallbackSupport(Message loadingMessage) {
		setup(new OperationMessage(loadingMessage), null, null);
	}

	public AsyncCallbackSupport(OperationMessage operationMessage) {
		setup(operationMessage, null, null);
	}

	public AsyncCallbackSupport(ErrorCapableView overrideView) {
		setup(null, overrideView, null);
	}

	public AsyncCallbackSupport(OperationMessage operationMessage, ErrorCapableView overrideView) {
		setup(operationMessage, overrideView, null);
	}

	public AsyncCallbackSupport(OperationMessage operationMessage, ErrorCapableView overrideView,
			HasEnabled buttonToEnable) {
		setup(operationMessage, overrideView, buttonToEnable);
	}

	private void setup(OperationMessage operationMessage, ErrorCapableView overrideView, HasEnabled buttonsToEnable) {
		this.operationMessage = operationMessage;
		this.overrideErrorView = overrideView;
		this.hasEnabled = buttonsToEnable;
		operationStart();
	}

	private void operationStart() {
		setButtonToEnableState(false);
		if (operationMessage != null && operationMessage.getProgressMessage() != null) {
			notifier.displayMessage(operationMessage.getProgressMessage());
		}
	}

	private HasEnabled hasEnabled;

	private void setButtonToEnableState(boolean enabled) {
		if (hasEnabled != null) {
			hasEnabled.setEnabled(enabled);
		}
	}

	private void operationEnd() {
		if (operationMessage != null && operationMessage.getProgressMessage() != null) {
			notifier.removeMessage(operationMessage.getProgressMessage());
		}
	}

	private void operationEndSuccess() {
		operationEnd();
		if (operationMessage != null && operationMessage.getSuccessMessage() != null) {
			notifier.displayMessage(operationMessage.getSuccessMessage());
		}

	}

	@Override
	public final void onSuccess(T result) {
		operationEndSuccess();
		success(result);
		setButtonToEnableState(true);
	}

	protected abstract void success(T result);

	@Override
	public final void onFailure(final Throwable exception) {
		operationEnd();
		if (errorHandler.overrideDefaultOnFailure(exception)) {
			errorHandler.handleError(exception, new AsyncCallback<T>() {

				@Override
				public void onFailure(Throwable arg0) {
					AsyncCallbackSupport.this.failure(exception);
				}

				@Override
				public void onSuccess(T arg0) {
					onSuccess(arg0);
				}
			});
		} else {
			failure(exception);
		}
		setButtonToEnableState(true);
	}

	protected void failure(Throwable exception) {
		if (overrideErrorView != null) {
			overrideErrorView.displayErrors(getErrorMessages(exception));
		} else {
			String errorMsg = StringUtils.concatenate(getErrorMessages(exception));
			Message message = new Message(0, errorMsg, Message.MessageType.ERROR);
			notifier.displayMessage(message);
		}
	}

	protected List<String> getErrorMessages(Throwable exception) {
		return errorInterpreter.getErrorMessages(exception);
	}

	/**
	 * @return the errorHandler
	 */
	public static ErrorInterpreter getErrorHandler() {
		return errorInterpreter;
	}
}
