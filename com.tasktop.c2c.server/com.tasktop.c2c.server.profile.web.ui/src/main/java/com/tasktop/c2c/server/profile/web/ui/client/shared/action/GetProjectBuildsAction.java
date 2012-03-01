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

import net.customware.gwt.dispatch.shared.Action;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetProjectBuildsAction implements Action<GetProjectBuildsResult> {
	private String projectId;
	private String jobName;

	public GetProjectBuildsAction(String projectId, String jobName) {
		this.projectId = projectId;
		this.jobName = jobName;
	}

	protected GetProjectBuildsAction() {
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	public String getJobName() {
		return jobName;
	}

}
