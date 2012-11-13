/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.profile.web.ui.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.gwt.user.client.rpc.StatusCodeException;
import com.tasktop.c2c.server.common.profile.web.client.place.SignInPlace;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.util.ExceptionsUtil;
import com.tasktop.c2c.server.common.web.shared.AuthenticationFailedException;
import com.tasktop.c2c.server.common.web.shared.AuthenticationRequiredException;
import com.tasktop.c2c.server.common.web.shared.ValidationFailedException;

public class ExceptionMessageProvider implements AsyncCallbackSupport.ErrorInterpreter {
	@Override
	public List<String> getErrorMessages(Throwable exception) {
		// FIXME: NLS
		if (exception instanceof ValidationFailedException) {
			return ((ValidationFailedException) exception).getMessages();
		} else if (exception instanceof AuthenticationFailedException) {
			if (exception.getMessage() == null) {
				return Collections.singletonList("Invalid username or password. Please try again.");
			} else {
				return Collections.singletonList(exception.getMessage());
			}
		} else if (ExceptionsUtil.isEntityNotFound(exception)) {
			return Collections.singletonList("The referenced entity cannot be found or no longer exists. (404)");
		} else if (exception instanceof AuthenticationRequiredException) {
			SignInPlace.createPlace().go();
			return Collections.singletonList(exception.getMessage());
		} else if (exception instanceof StatusCodeException) {
			StatusCodeException statusCodeException = (StatusCodeException) exception;
			if (statusCodeException.getStatusCode() == 500) {
				return Collections.singletonList("Unexpected error (HTTP 500)");
			} else {
				return Collections.singletonList(statusCodeException.getMessage());
			}
		} else if (exception instanceof DispatchException) {
			DispatchException dispatchException = (DispatchException) exception;
			if (dispatchException.getCauseClassname() != null
					&& dispatchException.getCauseClassname().equals(ValidationFailedException.class.getName())) {
				return Arrays.asList(dispatchException.getMessage().split(","));
			}
			{
				return Collections.singletonList(dispatchException.getMessage());
			}
		}

		return Collections.singletonList(exception.getClass() + ": " + exception.getMessage());

	}
}