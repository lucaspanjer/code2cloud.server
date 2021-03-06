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
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesResult;
import com.tasktop.c2c.server.common.web.client.event.ClearCacheEvent;
import com.tasktop.c2c.server.common.web.client.notification.OperationMessage;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.common.web.client.view.ErrorCapableView;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.ProfileEntryPoint;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.place.ProjectAdminSourcePlace;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.source.IProjectAdminSourceView;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.source.ProjectScmAdminView;
import com.tasktop.c2c.server.scm.domain.ScmRepository;

public class ProjectAdminSourceActivity extends AbstractActivity implements IProjectAdminSourceView.Presenter,
		SplittableActivity {

	private String projectIdentifier;
	private String repoBaseUrl;
	private String sshKey;
	private List<ScmRepository> repositories;
	private Project project;
	private ProjectScmAdminView view = ProjectScmAdminView.getInstance();
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public ProjectAdminSourceActivity() {
	}

	public void setPlace(Place p) {
		ProjectAdminSourcePlace place = (ProjectAdminSourcePlace) p;
		this.projectIdentifier = place.getProjectIdentifer();
		this.project = place.getProject();
		this.repositories = place.getRepositories();
		this.repoBaseUrl = place.getGitBaseUrl();
		this.sshKey = place.getPublicSshKey();
		updateView();
	}

	private void updateView() {
		view.setPresenter(this);
	}

	public String getRepoBaseUrl() {
		return repoBaseUrl;
	}

	public List<ScmRepository> getRepositories() {
		return repositories;
	}

	public Project getProject() {
		return project;
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view);
	}

	@Override
	public void onDeleteRepository(final ScmRepository repo) {
		OperationMessage message = new OperationMessage(profileMessages.deletingRepository());
		message.setSuccessText(profileMessages.repositoryDeleted());
		ProfileEntryPoint.getInstance().getProfileService()
				.deleteProjectGitRepository(projectIdentifier, repo, new AsyncCallbackSupport<Void>(message) {
					@Override
					protected void success(Void result) {
						AppGinjector.get.instance().getEventBus().fireEvent(new ClearCacheEvent());

						for (ScmRepository repository : repositories) {
							if (repository.equals(repo)) {
								repositories.remove(repository);
								break;
							}
						}
						updateView();
					}
				});
	}

	@Override
	public void onCreateRepository(ErrorCapableView errorView, ScmRepository repository) {
		OperationMessage message = new OperationMessage(profileMessages.addingRepository());
		message.setSuccessText(profileMessages.repositoryAdded());
		ProfileEntryPoint
				.getInstance()
				.getProfileService()
				.createProjectGitRepository(projectIdentifier, repository,
						new AsyncCallbackSupport<Void>(message, errorView) {
							@Override
							protected void success(final Void result) {
								AppGinjector.get.instance().getEventBus().fireEvent(new ClearCacheEvent());

								AppGinjector.get
										.instance()
										.getDispatchService()
										.execute(new GetProjectScmRepositoriesAction(projectIdentifier),
												new AsyncCallbackSupport<GetProjectScmRepositoriesResult>() {

													@Override
													protected void success(GetProjectScmRepositoriesResult result) {
														repositories = result.get();
														updateView();
													}
												});
							}

						});
	}

	@Override
	public String getSshKey() {
		return sshKey;
	}
}
