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

import org.apache.commons.io.FileUtils;

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
	private String metricName;

	@Override
	public void collect(ProjectServiceStatus status) {
		String actualPath = path.replace(PROJECT_ID_VAR, status.getProjectIdentifier());

		File directory = new File(actualPath);

		if (!directory.exists()) {
			// TODO set something on status
			return;
		}

		if (!directory.isDirectory()) {
			// TODO set something on status
			return;
		}

		long totalByteSize = FileUtils.sizeOfDirectory(directory);

		status.getMetrics().put(metricName, totalByteSize + "");
		status.getMetrics().put(metricName + ".humanReadable", FileUtils.byteCountToDisplaySize(totalByteSize));
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

}
