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

import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceTypes;

/**
 * Helper class for translating between public and internal data-storage objects.
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
public class DeploymentDomain {

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
		// TODO: result.setDescription(description)

		// TODO -- other fields
		return result;
	}

	protected DeploymentConfiguration newPublicDO(
			com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration source) {
		return new DeploymentConfiguration();
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
