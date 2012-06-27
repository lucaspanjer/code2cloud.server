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

import java.util.List;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.profile.domain.internal.QuotaSetting;

/**
 * Internal Service for interacting with quotas. This includes basic CRUD operations as well as enforcement operations.
 * The enforcement operations use {@link QuotaEnforcer}s for extensibility.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface QuotaService {
	QuotaSetting createQuota(QuotaSetting quotaSetting) throws EntityNotFoundException;

	QuotaSetting updateQuota(QuotaSetting quotaSetting) throws EntityNotFoundException;

	void removeQuota(QuotaSetting quotaSetting) throws EntityNotFoundException;

	List<QuotaSetting> findQuotaSettings(String name, String projectIdentifier, String organizationIdentifier);

	void enforceQuota(String quotaName, Object quotaObject) throws ValidationException;

	QuotaSetting findMostRelevantQuotaSetting(String name, String projectIdentifier, String organizationIdentifier);
}
