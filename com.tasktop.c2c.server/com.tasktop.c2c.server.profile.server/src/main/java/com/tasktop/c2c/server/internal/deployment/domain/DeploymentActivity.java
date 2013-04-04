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
package com.tasktop.c2c.server.internal.deployment.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.tasktop.c2c.server.deployment.domain.DeploymentActivityStatus;
import com.tasktop.c2c.server.deployment.domain.DeploymentActivityType;
import com.tasktop.c2c.server.profile.domain.internal.BaseEntity;
import com.tasktop.c2c.server.profile.domain.internal.Profile;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
@Entity
public class DeploymentActivity extends BaseEntity {

	private DeploymentConfiguration deploymentConfiguration;
	private Date time;
	private DeploymentActivityType type;
	private DeploymentActivityStatus status;

	// These properties store a snapshot of the same properties of the DeploymentConfiguration at the time of the
	// activity
	private Profile profile;
	private String buildJobName;
	private String buildJobNumber;
	private String buildArtifactPath;

	public DeploymentActivity() {
	}

	public DeploymentActivity(DeploymentActivityType type, DeploymentActivityStatus status,
			DeploymentConfiguration deploymentConfiguration, Profile currentUser) {
		if (DeploymentActivityType.DEPLOYED.equals(type)) {
			this.time = deploymentConfiguration.getLastDeploymentDate();
		} else {
			this.time = new Date();
		}
		this.type = type;
		this.status = status;
		this.profile = currentUser;
		this.buildArtifactPath = deploymentConfiguration.getBuildArtifactPath();
		this.buildJobName = deploymentConfiguration.getBuildJobName();
		this.buildJobNumber = deploymentConfiguration.getBuildJobNumber();
	}

	@ManyToOne
	public DeploymentConfiguration getDeploymentConfiguration() {
		return deploymentConfiguration;
	}

	public void setDeploymentConfiguration(DeploymentConfiguration deploymentConfiguration) {
		this.deploymentConfiguration = deploymentConfiguration;
	}

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public DeploymentActivityType getType() {
		return type;
	}

	public void setType(DeploymentActivityType type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public DeploymentActivityStatus getStatus() {
		return status;
	}

	public void setStatus(DeploymentActivityStatus status) {
		this.status = status;
	}

	@ManyToOne
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Column
	public String getBuildJobName() {
		return buildJobName;
	}

	public void setBuildJobName(String buildJobName) {
		this.buildJobName = buildJobName;
	}

	@Column
	public String getBuildJobNumber() {
		return buildJobNumber;
	}

	public void setBuildJobNumber(String buildJobNumber) {
		this.buildJobNumber = buildJobNumber;
	}

	@Column
	public String getBuildArtifactPath() {
		return buildArtifactPath;
	}

	public void setBuildArtifactPath(String buildArtifactPath) {
		this.buildArtifactPath = buildArtifactPath;
	}
}
