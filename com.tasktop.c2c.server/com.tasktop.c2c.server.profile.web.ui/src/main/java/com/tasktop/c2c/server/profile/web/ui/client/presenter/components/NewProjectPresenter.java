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
package com.tasktop.c2c.server.profile.web.ui.client.presenter.components;

import com.tasktop.c2c.server.common.profile.web.client.place.ProjectHomePlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectsPlace;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.notification.OperationMessage;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectAccessibility;
import com.tasktop.c2c.server.profile.domain.project.ProjectPreferences;
import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;
import com.tasktop.c2c.server.profile.web.ui.client.place.NewProjectPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.OrganizationNewProjectPlace;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.NewProjectView;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.NewProjectView.Presenter;

public class NewProjectPresenter extends AbstractProfilePresenter implements Presenter {

	private final NewProjectView view;

	public NewProjectPresenter(NewProjectView projectsView, NewProjectPlace place) {
		super(projectsView);
		this.view = projectsView;
		Project newProject = new Project();
		newProject.setAccessibility(ProjectAccessibility.PRIVATE);
		ProjectPreferences prefs = new ProjectPreferences();
		prefs.setWikiLanguage(WikiMarkupLanguage.TEXTILE);
		if (place instanceof OrganizationNewProjectPlace) {
			Organization organization = ((OrganizationNewProjectPlace) place).getOrganization();
			newProject.setOrganization(organization);
			prefs.setWikiLanguage(organization.getProjectPreferences().getWikiLanguage());
		}
		newProject.setProjectPreferences(prefs);

		view.setPresenter(this);
		view.setProject(newProject);
		// Set the createAvailable flag on our view
		view.displayMaxProjectsMessage(!(place.isCreateAvailable()));

	}

	@Override
	protected void bind() {

	}

	@Override
	public void createProject(Project project) {
		getProfileService().createProject(
				getAppState().getCredentials(),
				project,
				new AsyncCallbackSupport<String>(new OperationMessage("Creating project..."), null, view
						.getCreateButton()) {
					@Override
					protected void success(final String projectIdentifier) {
						ProjectHomePlace
								.createPlace(projectIdentifier)
								.displayOnArrival(
										Message.createSuccessMessage("Project created! Provisioning project services..."))
								.go();

					}

				});
	}

	@Override
	public void doCancel() {
		ProjectsPlace.createPlace().go();
	}

}
