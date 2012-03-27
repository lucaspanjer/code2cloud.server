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
package com.tasktop.c2c.server.cloud.service;

import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.cloud.domain.ServiceHost;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.profile.domain.internal.QuotaSetting;
import com.tasktop.c2c.server.profile.service.QuotaEnforcer;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class HudsonMaxConcurrentExecutorsQuotaEnforcer implements QuotaEnforcer<String> {

	@Autowired
	protected ServiceHostService serviceHostService;

	@Override
	public String getQuotaName() {
		return HudsonSlavePoolService.CONCURRENT_BUILD_QUOTA;
	}

	@Override
	public Class<String> getObjectClass() {
		return String.class;
	}

	@Override
	public void enforceQuota(QuotaSetting quota, String projectIdentifier) throws ValidationException {
		String maxSlavesString = quota.getValue();
		Integer maxSlaves = Integer.parseInt(maxSlavesString);
		List<ServiceHost> currentSlaves = serviceHostService.findHostsByTypeAndProject(
				EnumSet.of(ServiceType.BUILD_SLAVE), projectIdentifier);
		System.out.println(String.format("*****Currently have [%s] slaves", currentSlaves.size()));
		if (currentSlaves.size() >= maxSlaves) {
			throw new ValidationException("Already at concurrent build quota", null);
		}
	}

}
