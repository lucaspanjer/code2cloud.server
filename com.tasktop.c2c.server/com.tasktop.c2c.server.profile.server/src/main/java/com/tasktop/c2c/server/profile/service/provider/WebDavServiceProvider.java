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
package com.tasktop.c2c.server.profile.service.provider;

import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.tasks.service.TaskService;
import com.tasktop.c2c.server.tasks.service.TaskServiceClient;

@Service("webdavServiceProvider")
public class WebDavServiceProvider extends AbstractPreAuthServiceProvider<WebDavServiceClient> {

	public WebDavServiceProvider() {
		super("/maven/");
	}

	@Override
	protected WebDavServiceClient getNewService() {
		return new WebDavServiceClient();
	}

	public WebDavService getWebdavService(String projectIdentifier) {
		return getService(projectIdentifier);
	}

}
