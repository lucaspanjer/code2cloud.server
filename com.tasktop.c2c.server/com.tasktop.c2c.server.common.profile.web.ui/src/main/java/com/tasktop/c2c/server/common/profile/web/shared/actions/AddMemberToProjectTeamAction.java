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
package com.tasktop.c2c.server.common.profile.web.shared.actions;

import net.customware.gwt.dispatch.shared.Action;

import com.tasktop.c2c.server.profile.domain.project.Profile;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class AddMemberToProjectTeamAction implements Action<GetProjectTeamResult> {
	private String projectId;
	private Profile profile;

	public AddMemberToProjectTeamAction(String projectId, Profile profile) {
		this.projectId = projectId;
		this.profile = profile;
	}

	protected AddMemberToProjectTeamAction() {
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	public Profile getProfile() {
		return profile;
	}

}
