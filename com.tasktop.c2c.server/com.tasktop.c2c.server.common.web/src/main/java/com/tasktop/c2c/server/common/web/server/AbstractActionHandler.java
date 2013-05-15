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
package com.tasktop.c2c.server.common.web.server;

import java.util.Arrays;

import net.customware.gwt.dispatch.server.SimpleActionHandler;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;

import com.tasktop.c2c.server.common.service.ConcurrentUpdateException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.web.shared.ValidationFailedException;

/**
 * FIXME much of this is duplicated form AbstractAutowiredRemoteService
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractActionHandler<A extends Action<R>, R extends Result> extends SimpleActionHandler<A, R> {

	protected void handle(ValidationException exception) throws DispatchException {
		throw new ActionException(new ValidationFailedException(exception.getMessages()));
	}

	// TODO : handle this correctly
	protected void handle(ConcurrentUpdateException exception) throws DispatchException {
		throw new ActionException(new ValidationFailedException(
				Arrays.asList("The object has been modified since it was loaded")));
	}
}
