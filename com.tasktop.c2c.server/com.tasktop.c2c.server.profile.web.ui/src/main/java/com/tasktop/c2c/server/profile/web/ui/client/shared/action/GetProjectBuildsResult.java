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
package com.tasktop.c2c.server.profile.web.ui.client.shared.action;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.tasktop.c2c.server.profile.domain.build.BuildDetails;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetProjectBuildsResult implements Result {

	private List<String> buildJobNames;
	private List<BuildDetails> builds;

	/**
	 * @return the buildJobNames
	 */
	public List<String> getBuildJobNames() {
		return buildJobNames;
	}

	/**
	 * @return the builds
	 */
	public List<BuildDetails> getBuilds() {
		return builds;
	}

	protected GetProjectBuildsResult() {

	}

	public GetProjectBuildsResult(List<String> buildJobNames, List<BuildDetails> builds) {
		this.buildJobNames = buildJobNames;
		this.builds = builds;
	}

}
