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
package com.tasktop.c2c.server.common.service.web;

/**
 * This makes client calls to get the version of versioned services.
 * 
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractVersionedRestServiceClient extends AbstractRestServiceClient implements
		VersionedServiceClient {

	private static class ServiceCallResult {

		private String version;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	public String getServiceVersion() {
		ServiceCallResult callResult = template.getForObject(computeUrl("version"), ServiceCallResult.class);
		if (callResult.getVersion() != null) {
			return callResult.getVersion();
		}
		throw new IllegalStateException("Illegal result from call");
	}
}
