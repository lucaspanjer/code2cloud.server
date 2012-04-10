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
package com.tasktop.c2c.server.internal.profile.service;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public interface InternalProfileService {
	void doDeprovisionServiceAndDeleteProjectIfReady(String projectIdentifier, Long projectServiceId) throws EntityNotFoundException;

	void doDeleteProject(String projectIdentifier) throws EntityNotFoundException;

}
