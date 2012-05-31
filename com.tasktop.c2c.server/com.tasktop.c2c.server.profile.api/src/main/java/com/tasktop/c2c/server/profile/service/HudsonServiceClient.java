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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import com.tasktop.c2c.server.common.service.web.AbstractRestServiceClient;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.domain.build.HudsonStatus;
import com.tasktop.c2c.server.profile.domain.build.JobDetails;

public class HudsonServiceClient extends AbstractRestServiceClient implements HudsonService {

	public HudsonStatus getStatus() {
		return template.getForObject(computeUrl("/api/json"), HudsonStatus.class);
	}

	public JobDetails getJobDetails(String jobName) {
		String url = computeUrl("/job/" + jobName + "/api/json");
		return template.getForObject(url, JobDetails.class);
	}

	public BuildDetails getBuildDetails(String jobName, int buildNumber) {
		String url = computeUrl("/job/" + jobName + "/" + buildNumber + "/api/json");
		return template.getForObject(url, BuildDetails.class);
	}

	private static final String buildHistoryTreeFilter = "?tree=jobs[name,url,color,builds[url,duration,timestamp,result,number,actions[causes[shortDescription]]]]";

	public HudsonStatus getStatusWithBuildHistory() {
		String url = computeUrl("api/json" + buildHistoryTreeFilter);
		return template.getForObject(url, HudsonStatus.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.service.HudsonService#downloadBuildArtifact(java.lang.String, java.io.File)
	 */
	public void downloadBuildArtifact(String url, File saveLocation) throws IOException, URISyntaxException {
		ClientHttpRequest request = template.getRequestFactory().createRequest(new URI(url), HttpMethod.GET);
		ClientHttpResponse response = request.execute();
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new IOException("Unexpected return code [" + response.getStatusCode() + "] when getting [" + url
					+ "]");
		}
		InputStream responseStream = response.getBody();
		FileOutputStream fileStream = new FileOutputStream(saveLocation);
		byte[] buffer = new byte[10 * 1024];
		int bytesRead;
		while ((bytesRead = responseStream.read(buffer)) != -1) {
			fileStream.write(buffer, 0, bytesRead);
		}
		responseStream.close();
		fileStream.close();
	}

}
