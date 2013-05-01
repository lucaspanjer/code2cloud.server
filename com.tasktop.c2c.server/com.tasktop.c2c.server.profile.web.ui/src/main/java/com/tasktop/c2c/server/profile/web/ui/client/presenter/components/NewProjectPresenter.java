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

import com.google.gwt.place.shared.Place;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectHomePlace;
import com.tasktop.c2c.server.common.profile.web.shared.actions.CreateProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.CreateProjectResult;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.notification.OperationMessage;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectAccessibility;
import com.tasktop.c2c.server.profile.domain.project.ProjectPreferences;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.NewProjectPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.OrganizationNewProjectPlace;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.NewProjectView;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.NewProjectView.Presenter;

public class NewProjectPresenter extends AbstractProfilePresenter implements Presenter, SplittableActivity {

	private final NewProjectView view;
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public NewProjectPresenter() {
		this(NewProjectView.getInstance());
	}

	public NewProjectPresenter(NewProjectView projectsView) {
		super(projectsView);
		this.view = projectsView;

	}

	@Override
	protected void bind() {

	}

	@Override
	public void createProject(Project project, ProjectTemplate template) {
		getDispatchService().execute(
				new CreateProjectAction(project, template, null),
				new AsyncCallbackSupport<CreateProjectResult>(new OperationMessage(profileMessages.creatingProject()),
						null, view.getCreateButton()) {
					@Override
					protected void success(final CreateProjectResult result) {
						ProjectHomePlace
								.createPlace(result.get().getIdentifier())
								.displayOnArrival(
										Message.createSuccessMessage(profileMessages.projectCreatedAndProvisioning()))
								.go();

					}

				});
	}

	@Override
	public void doCancel() {
		ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace().go();
	}

	@Override
	public void setPlace(Place aPlace) {
		NewProjectPlace place = (NewProjectPlace) aPlace;
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

		view.setProjectTemplates(place.getProjectTemplates());

	}

	@Override
	public void createProject(Project project) {
		createProject(project, null);
	}

}
