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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.presenter;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.ValidationUtils;
import com.tasktop.c2c.server.common.profile.web.shared.actions.AddMemberToProjectTeamAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectTeamResult;
import com.tasktop.c2c.server.common.web.client.event.ClearCacheEvent;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.notification.OperationMessage;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.common.web.client.widgets.chooser.person.Person;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectTeamMember;
import com.tasktop.c2c.server.profile.domain.project.ProjectTeamSummary;
import com.tasktop.c2c.server.profile.web.ui.client.ProfileEntryPoint;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.ProjectAdminMenu;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.place.ProjectAdminTeamPlace;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.team.IProjectAdminTeamView;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.team.ProjectTeamAdminView;

public class ProjectAdminTeamActivity extends AbstractActivity implements IProjectAdminTeamView.Presenter,
		SplittableActivity {

	private ProjectTeamAdminView view = ProjectTeamAdminView.getInstance();
	private ProjectTeamSummary projectTeamSummary;
	private Project project;

	public ProjectAdminTeamActivity() {
	}

	public void setPlace(Place p) {
		ProjectAdminTeamPlace place = (ProjectAdminTeamPlace) p;
		this.project = place.getProject();
		this.projectTeamSummary = place.getProjectTeamSummary();
		updateView();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view);

	}

	private void updateView() {
		view.setPresenter(this);
		ProjectAdminMenu.getInstance().updateUrls(project);
	}

	@Override
	public List<ProjectTeamMember> getTeamMembers() {
		return projectTeamSummary.getMembers();
	}

	@Override
	public void removeTeamMember(ProjectTeamMember teamMember) {
		OperationMessage message = new OperationMessage("Saving");
		message.setSuccessText("Member Removed");
		ProfileEntryPoint.getInstance().getProfileService()
				.removeTeamMember(project.getIdentifier(), teamMember, new AsyncCallbackSupport<Boolean>(message) {
					@Override
					protected void success(Boolean result) {
						// Because we are not using dispatcher here and GetProjectTeamAction is cachable
						AppGinjector.get.instance().getEventBus().fireEvent(new ClearCacheEvent());
					}
				});
	}

	@Override
	public void updateTeamMember(ProjectTeamMember teamMember) {
		OperationMessage message = new OperationMessage("Saving");
		message.setSuccessText("Member Updated");
		ProfileEntryPoint.getInstance().getProfileService()
				.updateTeamMemberRoles(project.getIdentifier(), teamMember, new AsyncCallbackSupport<Boolean>(message) {
					@Override
					protected void success(Boolean result) {
						// Because we are not using dispatcher here and GetProjectTeamAction is cachable
						AppGinjector.get.instance().getEventBus().fireEvent(new ClearCacheEvent());
					}
				});
	}

	@Override
	public void sendInvite(final String email) {
		if (email == null || email.isEmpty()) {
			ProfileGinjector.get.instance().getNotifier()
					.displayMessage(Message.createErrorMessage("Please enter an email"));
			return;
		} else if (!ValidationUtils.isValidEmail(email)) {
			ProfileGinjector.get.instance().getNotifier()
					.displayMessage(Message.createErrorMessage("Please enter a valid email"));
			return;
		}

		ProfileEntryPoint.getInstance().getProfileService()
				.inviteUserForProject(email, project.getIdentifier(), new AsyncCallbackSupport<Void>() {

					@Override
					protected void success(Void result) {
						// Set a status message indicating that this user was invited
						view.clearInput();
						ProfileGinjector.get.instance().getNotifier()
								.displayMessage(Message.createSuccessMessage("Invitation email sent to " + email));
					}
				});
	}

	@Override
	public Person getSelf() {
		return ProfileEntryPoint.getInstance().getAppState().getSelf();
	}

	@Override
	public void addToProject(final Profile user) {
		if (user == null) {
			ProfileGinjector.get.instance().getNotifier()
					.displayMessage(Message.createErrorMessage("Please select a user first"));
			return;
		}
		AppGinjector.get
				.instance()
				.getDispatchService()
				.execute(new AddMemberToProjectTeamAction(getProjectIdentifier(), user),
						new AsyncCallbackSupport<GetProjectTeamResult>() {

							@Override
							protected void success(GetProjectTeamResult result) {
								projectTeamSummary = result.get();
								view.clearInput();
								view.setPresenter(ProjectAdminTeamActivity.this);
								ProfileGinjector.get
										.instance()
										.getNotifier()
										.displayMessage(
												Message.createSuccessMessage("Added user " + user.getUsername()));

							}
						});

	}

	@Override
	public String getProjectIdentifier() {
		return project.getIdentifier();
	}
}
