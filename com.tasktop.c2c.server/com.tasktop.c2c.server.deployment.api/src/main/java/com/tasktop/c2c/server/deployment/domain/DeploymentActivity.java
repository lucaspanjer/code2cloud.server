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
package com.tasktop.c2c.server.deployment.domain;

import java.util.Date;

import com.tasktop.c2c.server.common.service.domain.AbstractEntity;
import com.tasktop.c2c.server.profile.domain.project.Profile;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class DeploymentActivity extends AbstractEntity {

	private static final long serialVersionUID = -2366734783010277071L;

	private Date time;
	private DeploymentActivityType type;
	private DeploymentActivityStatus status;

	// These properties store a snapshot of the same properties of the DeploymentConfiguration at the time of the
	// activity
	private Profile profile;
	private String buildJobName;
	private String buildJobNumber;
	private String buildArtifactPath;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public DeploymentActivityStatus getStatus() {
		return status;
	}

	public void setStatus(DeploymentActivityStatus status) {
		this.status = status;
	}

	public DeploymentActivityType getType() {
		return type;
	}

	public void setType(DeploymentActivityType type) {
		this.type = type;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public String getBuildJobName() {
		return buildJobName;
	}

	public void setBuildJobName(String buildJobName) {
		this.buildJobName = buildJobName;
	}

	public String getBuildJobNumber() {
		return buildJobNumber;
	}

	public void setBuildJobNumber(String buildJobNumber) {
		this.buildJobNumber = buildJobNumber;
	}

	public String getBuildArtifactPath() {
		return buildArtifactPath;
	}

	public void setBuildArtifactPath(String buildArtifactPath) {
		this.buildArtifactPath = buildArtifactPath;
	}
}
