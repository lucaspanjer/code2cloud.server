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
package com.tasktop.c2c.server.profile.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.domain.build.HudsonStatus;
import com.tasktop.c2c.server.profile.domain.build.JobDetails;

public interface HudsonService {
	HudsonStatus getStatus();

	JobDetails getJobDetails(String jobName);

	BuildDetails getBuildDetails(String jobName, int buildNumber);

	/** Job summaries with the build detail filled in. */
	HudsonStatus getStatusWithBuildHistory();

	/**
	 * Download an artifact from hudson. If this is invoked on the hub node, then we will download directly from the
	 * internal service.
	 * 
	 * @param artifactUrl
	 * @param saveLocation
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	void downloadBuildArtifact(String artifactUrl, File saveLocation) throws IOException, URISyntaxException;

	String getJobConfigXml(String jobName);

	void createNewJob(String jobName, String configXml);

	/**
	 * @return
	 */
	boolean isHudsonReady();
}
