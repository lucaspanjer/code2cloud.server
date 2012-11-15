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

import java.util.HashMap;
import java.util.Map;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public final class DeploymentServiceTypes {

	private DeploymentServiceTypes() {
		// enforce singleton
	}

	private static final Map<String, DeploymentServiceType> typesById = new HashMap<String, DeploymentServiceType>();

	public static void register(DeploymentServiceType type) {
		typesById.put(type.getId(), type);
	}

	public static DeploymentServiceType findById(String id) {
		return typesById.get(id);
	}

	public final static DeploymentServiceType CLOUD_FOUNDRY = new DeploymentServiceType("CloudFoundry", "Cloud Foundry");
	static {
		CLOUD_FOUNDRY.setSupportsCredentials(true);
		CLOUD_FOUNDRY.setSupportsServices(true);
		CLOUD_FOUNDRY.setSupportsSettings(true);
		CLOUD_FOUNDRY.setAlwaysDeleteInService(false);
		register(CLOUD_FOUNDRY);
	}

}
