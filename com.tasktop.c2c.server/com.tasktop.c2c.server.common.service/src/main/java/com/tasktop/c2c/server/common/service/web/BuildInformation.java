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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class BuildInformation {
	@Value("${git.commit.id:}")
	private String gitCommitId;

	@Value("${git.commit.message.full:}")
	private String gitCommitMessage;

	@Value("${git.build.time:}")
	private String buildTime;

	@Value("${git.build.user.name:}")
	private String buildUser;

	/**
	 * @return the gitCommitId
	 */
	public String getGitCommitId() {
		return gitCommitId;
	}

	/**
	 * @param gitCommitId
	 *            the gitCommitId to set
	 */
	public void setGitCommitId(String gitCommitId) {
		this.gitCommitId = gitCommitId;
	}

	/**
	 * @return the gitCommitMessage
	 */
	public String getGitCommitMessage() {
		return gitCommitMessage;
	}

	/**
	 * @param gitCommitMessage
	 *            the gitCommitMessage to set
	 */
	public void setGitCommitMessage(String gitCommitMessage) {
		this.gitCommitMessage = gitCommitMessage;
	}

	/**
	 * @return the buildTime
	 */
	public String getBuildTime() {
		return buildTime;
	}

	/**
	 * @param buildTime
	 *            the buildTime to set
	 */
	public void setBuildTime(String buildTime) {
		this.buildTime = buildTime;
	}

	/**
	 * @return the buildUser
	 */
	public String getBuildUser() {
		return buildUser;
	}

	/**
	 * @param buildUser
	 *            the buildUser to set
	 */
	public void setBuildUser(String buildUser) {
		this.buildUser = buildUser;
	}

}
