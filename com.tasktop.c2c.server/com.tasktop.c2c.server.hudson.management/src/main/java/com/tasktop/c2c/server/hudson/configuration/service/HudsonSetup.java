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

import hudson.Functions;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class HudsonSetup implements InitializingBean {

	private TenantAwareRequestRootPathProvider requestRootPathProvider;

	@Override
	public void afterPropertiesSet() throws Exception {
		Functions.setRequestRootPathProvider(requestRootPathProvider);

	}

	public void setRequestRootPathProvider(TenantAwareRequestRootPathProvider requestRootPathProvider) {
		this.requestRootPathProvider = requestRootPathProvider;
	}

}
