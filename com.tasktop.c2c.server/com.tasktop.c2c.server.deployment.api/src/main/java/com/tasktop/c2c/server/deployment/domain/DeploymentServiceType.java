/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

import java.io.Serializable;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DeploymentServiceType implements Serializable {

	private String id;
	private String name;
	private boolean supportsCredentials = false;
	private boolean supportsServices = false;
	private boolean supportsSettings = false;
	private boolean alwaysDeleteInService = true;

	protected DeploymentServiceType() {
		// For serialization
	}

	public DeploymentServiceType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeploymentServiceType other = (DeploymentServiceType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean isSupportsCredentials() {
		return supportsCredentials;
	}

	public void setSupportsCredentials(boolean requiresAuth) {
		this.supportsCredentials = requiresAuth;
	}

	public boolean isSupportsServices() {
		return supportsServices;
	}

	public void setSupportsServices(boolean supportsServices) {
		this.supportsServices = supportsServices;
	}

	public boolean isSupportsSettings() {
		return supportsSettings;
	}

	public void setSupportsSettings(boolean supportsSettings) {
		this.supportsSettings = supportsSettings;
	}

	@Override
	public String toString() {
		return "DeploymentServiceType [id=" + id + ", name=" + name + "]";
	}

	public boolean isAlwaysDeleteInService() {
		return alwaysDeleteInService;
	}

	public void setAlwaysDeleteInService(boolean alwaysDeleteInService) {
		this.alwaysDeleteInService = alwaysDeleteInService;
	}
}
