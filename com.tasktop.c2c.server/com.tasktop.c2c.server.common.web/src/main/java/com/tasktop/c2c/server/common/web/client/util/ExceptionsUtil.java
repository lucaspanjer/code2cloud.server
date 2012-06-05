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
package com.tasktop.c2c.server.common.web.client.util;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.tasktop.c2c.server.common.web.shared.NoSuchEntityException;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public final class ExceptionsUtil {
	public static final String ENITITY_NOT_FOUND_EXCEPTION_CLASSNAME = "com.tasktop.c2c.server.common.service.EntityNotFoundException";

	public static boolean isEntityNotFound(Throwable t) {
		if (t instanceof DispatchException) {
			return isEntityNotFound((DispatchException) t);
		} else if (t instanceof NoSuchEntityException) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEntityNotFound(DispatchException e) {
		return e.getCauseClassname().equals(ENITITY_NOT_FOUND_EXCEPTION_CLASSNAME);
	}

	private ExceptionsUtil() {
		// static helper
	}
}
