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
package com.tasktop.c2c.server.common.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class AbstractBuildInfoRestService extends AbstractRestService {
	@Autowired
	private BuildInformation buildInformation;

	@RequestMapping(value = "buildInfo", method = RequestMethod.GET)
	public BuildInformation getBuildInformation() {
		return buildInformation;
	}
}
