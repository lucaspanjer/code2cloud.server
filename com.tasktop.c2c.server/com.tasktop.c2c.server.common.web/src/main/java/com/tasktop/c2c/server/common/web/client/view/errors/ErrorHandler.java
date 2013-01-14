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
package com.tasktop.c2c.server.common.web.client.view.errors;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public interface ErrorHandler {

	/**
	 * Determine if we want to handle this error by looking at the Throwable.
	 * 
	 * @param t
	 * @return
	 */
	public boolean overrideDefaultOnFailure(Throwable t);

	/**
	 * Handle the error, and if for some reason we cannot, invoke the default handler.
	 * 
	 * @param t
	 * @param defaultHandler
	 */
	public <T> void handleError(Throwable t, AsyncCallback<T> defaultHandler);
}
