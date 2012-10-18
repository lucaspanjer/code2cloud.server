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

import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * This interface should be implemented if a service is to be provisioned by the profile service instead of by the
 * service itself. For example, it may be desired that the wiki service database is setup by the profile service instead
 * of by the wiki service.
 * 
 * <p>
 * The provisioner can then be added to the {@link ProjectServiceServiceBean} projectServiceProvisionerList
 * </p>
 * 
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceProvisioner {

	/**
	 * @param projectService
	 *            the service to be provisioned
	 */
	public void provision(ProjectService projectService);

	/**
	 * @param projectService
	 *            the service to be provisioned
	 * @return true if the type of ProjectService is supported by the provisioner
	 */
	public boolean supports(ProjectService projectService);
}
