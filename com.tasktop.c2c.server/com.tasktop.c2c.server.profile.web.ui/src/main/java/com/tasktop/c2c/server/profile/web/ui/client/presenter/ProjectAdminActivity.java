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
package com.tasktop.c2c.server.profile.web.ui.client.presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.place.IPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectAdminPlace;
import com.tasktop.c2c.server.common.profile.web.shared.actions.DeleteProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.DeleteProjectResult;
import com.tasktop.c2c.server.common.web.client.event.ClearCacheEvent;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.notification.OperationMessage;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.ProjectAdminMenu;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.ProjectAdminView;

public class ProjectAdminActivity extends AbstractActivity implements SplittableActivity {

	private Project project;
	private ProjectAdminView view = ProjectAdminView.getInstance();
	private ProjectAdminActivityMapper adminActivityMapper = new ProjectAdminActivityMapper();
	private ActivityManager adminActivityManager = new ActivityManager(adminActivityMapper, AppGinjector.get.instance()
			.getEventBus());

	public ProjectAdminActivity() {
		adminActivityManager.setDisplay(view.getContentContainer());
	}

	public void setPlace(Place p) {
		ProjectAdminPlace place = (ProjectAdminPlace) p;
		this.project = place.getProject();
		updateView();
		ProjectAdminMenu.getInstance().select(place);
		adminActivityManager.onPlaceChange(new PlaceChangeEvent(p));
	}

	private void updateView() {
		view.setPresenter(this);
		view.setProject(project);
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view);
	}

	/**
	 * 
	 */
	public void deleteProject() {
		AppGinjector.get
				.instance()
				.getDispatchService()
				.execute(new DeleteProjectAction(project.getIdentifier()),
						new AsyncCallbackSupport<DeleteProjectResult>(OperationMessage.create("Deleting Project")) {

							@Override
							protected void success(DeleteProjectResult result) {
								AppGinjector.get.instance().getEventBus().fireEvent(new ClearCacheEvent());

								Message message = Message
										.createSuccessMessage("Project deletion is started. This may take some time");

								IPlace p = ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace();
								p.displayOnArrival(message);
								p.go();
							}
						});
	}

}
