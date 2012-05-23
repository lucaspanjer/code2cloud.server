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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.team;

import java.util.List;

import com.tasktop.c2c.server.common.web.client.widgets.chooser.person.Person;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.domain.project.ProjectTeamMember;

public interface IProjectAdminTeamView<T extends IProjectAdminTeamView.Presenter> {

	void setPresenter(T presenter);

	public static interface Presenter {
		List<ProjectTeamMember> getTeamMembers();

		void removeTeamMember(ProjectTeamMember teamMember);

		void updateTeamMember(ProjectTeamMember teamMember);

		void sendInvite(String email);

		void addToProject(Profile profile);

		Person getSelf();

		String getProjectIdentifier();
	}
}
