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
package com.tasktop.c2c.server.configuration.service;


/**
 * Service that runs on internal nodes to configure for a given project. This service should be used before the node
 * becomes available.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceManagementService {

	/**
	 * Start the node configuring. Can return before the node is configured.
	 * 
	 * @param configuration
	 */
	void configureNode(ProjectServiceConfiguration configuration);
}
