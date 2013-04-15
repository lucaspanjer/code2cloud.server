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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.tasktop.c2c.server.cloud.domain.ServiceHost;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
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

			Set<String> newIPs = new HashSet<String>();
			for (ServiceHost serviceHostBean : serviceHostStartupBeans) {
				List<ServiceHost> existingHostsForIP = serviceHostService.findHostsByIp(serviceHostBean
						.getInternalNetworkAddress());
				// if the IP is new, add the ServiceHost
				if (existingHostsForIP == null || existingHostsForIP.isEmpty()
						|| newIPs.contains(serviceHostBean.getInternalNetworkAddress())) {
					serviceHostService.createServiceHost(serviceHostBean);
					newIPs.add(serviceHostBean.getInternalNetworkAddress());
					// if there's an exact match, do nothing
				} else if (serviceHostService.findHostForIpAndType(serviceHostBean.getInternalNetworkAddress(),
						serviceHostBean.getSupportedServices()) == null) {
					// otherwise look for the best match by IP and type,
					// and update the best match with the new types
					ServiceHost bestMatch = findBestServiceHostMatch(serviceHostBean, existingHostsForIP);
					bestMatch.setSupportedServices(serviceHostBean.getSupportedServices());
					serviceHostService.updateServiceHost(bestMatch);
				}
			}
		}
	}

	private ServiceHost findBestServiceHostMatch(ServiceHost serviceHostBean, List<ServiceHost> existingHostsForIP) {
		int highestScore = 0;
		ServiceHost bestMatch = null;
		for (ServiceHost serviceHost : existingHostsForIP) {
			int thisHostScore = 0;

			// increase the score for each matching ServiceType
			for (ServiceType type : serviceHost.getSupportedServices()) {
				if (serviceHostBean.getSupportedServices().contains(type)) {
					thisHostScore += 1;
				}
			}

			// build slaves need their own configurations, but there's no need to affect the score here - an exact match
			// will have been ignored in the calling method

			if (thisHostScore >= highestScore) {
				highestScore = thisHostScore;
				bestMatch = serviceHost;
			}
		}
		return bestMatch;
	}
}
