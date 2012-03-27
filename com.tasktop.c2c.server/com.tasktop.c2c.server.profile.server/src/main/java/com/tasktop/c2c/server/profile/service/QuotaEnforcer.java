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
package com.tasktop.c2c.server.profile.service;

import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.profile.domain.internal.QuotaSetting;

/**
 * Responsible for enforcing a single quota.
 * 
 * @author clint.morgan (Tasktop Technologies Inc.)
 * 
 */
public interface QuotaEnforcer<T> {
	String getQuotaName();

	Class<T> getObjectClass();

	void enforceQuota(QuotaSetting quota, T object) throws ValidationException;
}
