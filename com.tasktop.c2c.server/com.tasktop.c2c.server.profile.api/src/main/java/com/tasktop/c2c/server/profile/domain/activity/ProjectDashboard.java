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
package com.tasktop.c2c.server.profile.domain.activity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.tasktop.c2c.server.profile.domain.build.HudsonStatus;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.tasks.domain.TaskSummary;

@SuppressWarnings("serial")
public class ProjectDashboard implements Serializable {

	private List<TaskSummary> taskSummaries;
	private List<ScmSummary> scmSummaries;
	private HudsonStatus buildStatus;
	private Map<com.tasktop.c2c.server.scm.domain.Profile, Integer> commitsByAuthor;

	public void setTaskSummaries(List<TaskSummary> taskSummaries) {
		this.taskSummaries = taskSummaries;
	}

	public List<TaskSummary> getTaskSummaries() {
		return taskSummaries;
	}

	public void setScmSummaries(List<ScmSummary> scmSummaries) {
		this.scmSummaries = scmSummaries;
	}

	public List<ScmSummary> getScmSummaries() {
		return scmSummaries;
	}

	public void setBuildStatus(HudsonStatus buildStatus) {
		this.buildStatus = buildStatus;
	}

	public HudsonStatus getBuildStatus() {
		return buildStatus;
	}

	public void setCommitsByAuthor(Map<com.tasktop.c2c.server.scm.domain.Profile, Integer> commitsByAuthor) {
		this.commitsByAuthor = commitsByAuthor;
	}

	public Map<com.tasktop.c2c.server.scm.domain.Profile, Integer> getCommitsByAuthor() {
		return commitsByAuthor;
	}

}
