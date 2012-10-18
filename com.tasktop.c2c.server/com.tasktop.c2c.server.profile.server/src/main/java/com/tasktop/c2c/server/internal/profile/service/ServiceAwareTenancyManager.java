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
package com.tasktop.c2c.server.internal.profile.service;

import com.tasktop.c2c.server.common.service.web.TenancyManager;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface ServiceAwareTenancyManager extends TenancyManager {

	public void establishTenancyContext(ProjectService projectService);

}
