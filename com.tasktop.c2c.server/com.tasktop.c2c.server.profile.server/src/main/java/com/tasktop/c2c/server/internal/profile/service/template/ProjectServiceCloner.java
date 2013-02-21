/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.internal.profile.service.template;

import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * Responsible for cloning elements from a project template's service into a target project's service
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceCloner {

	boolean canClone(ProjectService service);

	void doClone(ProjectService templateService, ProjectService targetProjectService);

	/**
	 * @param sourceService
	 * @param targetProjectService
	 */
	boolean isReadyToClone(ProjectService sourceService, ProjectService targetProjectService);

}
