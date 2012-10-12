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
package com.tasktop.c2c.server.event.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.tasktop.c2c.server.common.service.BaseProfileConfiguration;

/**
 * EventService client that will setup to target the hub.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class InternalEventServiceClient extends EventServiceClient implements InitializingBean {
	@Autowired
	private BaseProfileConfiguration configuration;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		String url = "http://" + configuration.getBaseWebHost() + configuration.getBaseContextPath() + "/api/event";
		setBaseUrl(url);
	}

}
