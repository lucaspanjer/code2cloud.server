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
package com.tasktop.c2c.server.hudson.configuration.service;

import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class HudsonWarNamingStrategy {

	private boolean perOrg = false; // false => perProject
	private String pathPattern;

	public String getWarFilePath(ProjectServiceConfiguration config) {
		if (perOrg) {
			return String.format(pathPattern, config.getOrganizationIdentifier());
		} else {
			return String.format(pathPattern, config.getProjectIdentifier());
		}
	}

	public void setPerOrg(boolean perOrg) {
		this.perOrg = perOrg;
	}

	public void setPathPattern(String pathPattern) {
		this.pathPattern = pathPattern;
	}

}
