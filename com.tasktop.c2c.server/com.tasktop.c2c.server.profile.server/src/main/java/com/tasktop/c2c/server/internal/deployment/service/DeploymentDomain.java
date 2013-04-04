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
package com.tasktop.c2c.server.internal.deployment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tasktop.c2c.server.deployment.domain.DeploymentActivity;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceTypes;
import com.tasktop.c2c.server.internal.profile.service.WebServiceDomain;

/**
 * Helper class for translating between public and internal data-storage objects.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public class DeploymentDomain {

	@Autowired
	WebServiceDomain webServiceDomain;

	public DeploymentConfiguration convertToPublic(
			com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration source) {
		DeploymentConfiguration result = newPublicDO(source);
		result.setId(source.getId());
		result.setApiToken(source.getApiToken());
		result.setApiBaseUrl(source.getApiBaseUrl());
		result.setName(source.getName());
		result.setServiceType(DeploymentServiceTypes.findById(source.getServiceType()));
		result.setUsername(source.getUsername());

		result.setDeploymentType(source.getDeploymentType());
		result.setBuildJobName(source.getBuildJobName());
		result.setBuildJobNumber(source.getBuildJobNumber());
		result.setBuildArtifactPath(source.getBuildArtifactPath());
		result.setDeployUnstableBuilds(source.isDeployUnstableBuilds());
		result.setLastDeploymentDate(source.getLastDeploymentDate());
		List<DeploymentActivity> deploymentActivities = new ArrayList<DeploymentActivity>();
		for (com.tasktop.c2c.server.internal.deployment.domain.DeploymentActivity deploymentActivity : source
				.getDeploymentActivities()) {
			deploymentActivities.add(convertToPublic(deploymentActivity));
		}
		result.setDeploymentActivities(deploymentActivities);
		// TODO: result.setDescription(description)

		// TODO -- other fields
		return result;
	}

	public DeploymentActivity convertToPublic(
			com.tasktop.c2c.server.internal.deployment.domain.DeploymentActivity source) {
		DeploymentActivity result = newPublicDO(source);
		result.setId(source.getId());
		result.setStatus(source.getStatus());
		result.setTime(source.getTime());
		result.setType(source.getType());
		result.setProfile(source.getProfile() != null ? webServiceDomain.copy(source.getProfile()) : null);
		result.setBuildArtifactPath(source.getBuildArtifactPath());
		result.setBuildJobName(source.getBuildJobName());
		result.setBuildJobNumber(source.getBuildJobNumber());
		return result;
	}

	protected DeploymentConfiguration newPublicDO(
			com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration source) {
		return new DeploymentConfiguration();
	}

	protected DeploymentActivity newPublicDO(com.tasktop.c2c.server.internal.deployment.domain.DeploymentActivity source) {
		return new DeploymentActivity();
	}

	public com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration convertToInternal(
			DeploymentConfiguration source) {
		com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration result = newInternalDO(source);
		updateInternal(source, result);

		return result;
	}

	protected com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration newInternalDO(
			DeploymentConfiguration source) {
		return new com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration();
	}

	public void updateInternal(DeploymentConfiguration source,
			com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration target) {
		target.setApiToken(source.getApiToken());
		target.setApiBaseUrl(source.getApiBaseUrl());
		target.setName(source.getName());
		target.setServiceType(source.getServiceType().getId());
		target.setUsername(source.getUsername());

		target.setBuildJobName(source.getBuildJobName());
		target.setBuildJobNumber(source.getBuildJobNumber());
		target.setDeploymentType(source.getDeploymentType());
		target.setBuildArtifactPath(source.getBuildArtifactPath());
		target.setDeployUnstableBuilds(source.isDeployUnstableBuilds());
		target.setLastDeploymentDate(source.getLastDeploymentDate());
	}

	public void prepareForCreate(DeploymentConfiguration deploymentConfiguration) {

	}

}
