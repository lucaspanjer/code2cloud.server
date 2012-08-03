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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.common.service.WrappedCheckedException;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
@Service
@Qualifier("buildInfoServiceClient")
public class BuildInfoServiceClient extends AbstractRestServiceClient {

	@SuppressWarnings("unused")
	private static class ServiceCallResult {
		private BuildInformation buildInfo;

		public BuildInformation getBuildInformation() {
			return buildInfo;
		}

		public void setBuildInformation(BuildInformation buildInfo) {
			this.buildInfo = buildInfo;
		}
	}

	public BuildInformation getBuildInfo() {
		try {
			ServiceCallResult result = template.getForObject(computeUrl("buildInfo"), ServiceCallResult.class);
			if (result != null && result.getBuildInformation() != null) {
				return result.getBuildInformation();
			}
		} catch (WrappedCheckedException e) {
			throw e;
		}
		throw new IllegalStateException("Unexpected result");
	}

}
