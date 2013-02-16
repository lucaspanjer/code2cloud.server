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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus.ServiceState;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.MetricCollector;

/**
 * Generic metric which collects total disk size for a given directory.
 * 
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public class FileSystemMetricCollector implements MetricCollector {

	public static final String PROJECT_ID_VAR = "{projectIdentifier}";

	private String path;

	@Override
	public void collect(ProjectServiceStatus status) {
		String actualPath = path.replace(PROJECT_ID_VAR, status.getProjectIdentifier());

		File directory = new File(actualPath);

		if (!directory.exists()) {
			status.setServiceState(ServiceState.UNAVAILABLE);
			return;
		}

		if (!directory.isDirectory()) {
			status.setServiceState(ServiceState.UNAVAILABLE);
			return;
		}

		long totalByteSize;
		try {
			totalByteSize = com.tasktop.c2c.server.configuration.service.FileUtils.size(directory);
			status.setServiceState(ServiceState.RUNNING);
			status.getMetrics().put(ProjectServiceStatus.DISK_USAGE_METRICS_KEY, totalByteSize + "");
			status.getMetrics().put(ProjectServiceStatus.DISK_USAGE_HR_METRICS_KEY,
					FileUtils.byteCountToDisplaySize(totalByteSize));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setPath(String path) {
		this.path = path;
	}
}
