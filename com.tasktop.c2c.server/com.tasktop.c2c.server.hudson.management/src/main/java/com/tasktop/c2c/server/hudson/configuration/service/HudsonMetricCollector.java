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

import hudson.model.Hudson;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus.ServiceState;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.MetricCollector;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class HudsonMetricCollector implements MetricCollector {

	private static final Logger LOG = LoggerFactory.getLogger(HudsonMetricCollector.class.getName());

	@Override
	public void collect(ProjectServiceStatus status) {
		File hudsonHome = Hudson.getInstance().getRootDir();
		String projectId = status.getProjectIdentifier();

		File teamsDirectory = new File(hudsonHome, "teams");

		if (!teamsDirectory.exists() || !teamsDirectory.isDirectory()) {
			status.setServiceState(ServiceState.UNAVAILABLE);
		}

		File teamDirectory = new File(teamsDirectory, projectId);
		File jobsDirectory = new File(teamDirectory, "jobs");

		if (!jobsDirectory.exists()) {
			// This happens when there are no jobs.
			setDisUsage(0, status);
			return;
		}

		if (!jobsDirectory.isDirectory()) {
			status.setServiceState(ServiceState.UNAVAILABLE);
			return;
		}

		try {
			long totalByteSize = com.tasktop.c2c.server.configuration.service.FileUtils.size(jobsDirectory);
			setDisUsage(totalByteSize, status);

		} catch (IOException e) {
			LOG.warn("Error computing metrics", e);
		}

	}

	private void setDisUsage(long totalByteSize, ProjectServiceStatus status) {
		status.setServiceState(ServiceState.RUNNING);
		status.getMetrics().put(ProjectServiceStatus.DISK_USAGE_METRICS_KEY, totalByteSize + "");
		status.getMetrics().put(ProjectServiceStatus.DISK_USAGE_HR_METRICS_KEY,
				FileUtils.byteCountToDisplaySize(totalByteSize));
	}

}
