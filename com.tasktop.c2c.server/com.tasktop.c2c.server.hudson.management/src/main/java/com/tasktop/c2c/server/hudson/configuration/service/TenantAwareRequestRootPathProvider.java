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

import hudson.RequestRootPathProvider;

import javax.servlet.http.HttpServletRequest;

import com.tasktop.c2c.server.common.service.web.TenancyUtil;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class TenantAwareRequestRootPathProvider implements RequestRootPathProvider {

	private String pattern = "/s/%s/hudson";

	@Override
	public String getRootPath(HttpServletRequest req) {
		String projectId = TenancyUtil.getCurrentTenantProjectIdentifer();
		if (projectId == null) {
			if (req != null) {
				return req.getContextPath();
			} else {
				return null;
			}
		}
		return String.format(pattern, projectId);
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
