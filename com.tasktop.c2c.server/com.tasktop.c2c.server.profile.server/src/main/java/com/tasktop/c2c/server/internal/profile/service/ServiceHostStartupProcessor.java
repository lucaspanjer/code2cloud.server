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
package com.tasktop.c2c.server.internal.profile.service;

import java.io.File;
import java.util.ArrayList;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.tasktop.c2c.server.cloud.domain.ServiceHost;
import com.tasktop.c2c.server.cloud.service.ServiceHostService;

/**
 * On startup, this looks for ServiceHost configuration values and then applies them additively (preserving existing
 * ServiceHost configuration) to the database.
 * 
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class ServiceHostStartupProcessor implements InitializingBean {

	private String configFile;

	private ServiceHostService serviceHostService;

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public void setServiceHostService(ServiceHostService serviceHostService) {
		this.serviceHostService = serviceHostService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		if (new File(configFile).exists()) {
			ApplicationContext appContext = new FileSystemXmlApplicationContext("file:" + configFile);
			ArrayList<ServiceHost> serviceHostStartupBeans = (ArrayList<ServiceHost>) appContext
					.getBean("serviceHostStartupBeans");
			for (ServiceHost serviceHostBean : serviceHostStartupBeans) {
				if (serviceHostService.findHostForIpAndType(serviceHostBean.getInternalNetworkAddress(),
						serviceHostBean.getSupportedServices()) == null) {
					serviceHostService.createServiceHost(serviceHostBean);
				}
			}
		}
	}
}
