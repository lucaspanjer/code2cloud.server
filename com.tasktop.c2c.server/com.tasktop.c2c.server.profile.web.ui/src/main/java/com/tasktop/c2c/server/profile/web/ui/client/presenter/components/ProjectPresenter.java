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

import java.util.List;

import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.gwt.place.shared.Place;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectHomePlace;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectActivityAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectActivityResult;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.common.web.shared.NoSuchEntityException;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectService;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.ProjectView;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.RetrievePageAction;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.RetrievePageResult;

public class ProjectPresenter extends AbstractProfilePresenter implements SplittableActivity {

	private Project project;
	private ProjectView view;

	public ProjectPresenter(ProjectView view) {
		super(view);
		this.view = view;
	}

	public ProjectPresenter() {
		this(ProjectView.getInstance());
	}

	public void setPlace(Place p) {
		ProjectHomePlace place = (ProjectHomePlace) p;

		this.project = place.getProject();
		view.setProject(place.getProject());
		view.activityView.clear();

		boolean hasWiki = isServiceAvailable(ServiceType.WIKI);
		view.setHasWikiService(hasWiki);
		if (hasWiki) {
			AppGinjector.get
					.instance()
					.getDispatchService()
					.execute(new RetrievePageAction(project.getIdentifier(), ProjectHomePlace.WIKI_HOME_PAGE, true),
							new AsyncCallbackSupport<RetrievePageResult>() {
								@Override
								public void success(RetrievePageResult actionResult) {
									view.setProjectWikiPage(actionResult.get());
								}

								@Override
								public void onFailure(Throwable exception) {
									// NoSuchEntity is expected if the homepage doesn't exist - if it was anything else,
									// go to the generic error handler
									if (exception instanceof DispatchException
											&& ((DispatchException) exception).getCauseClassname().equals(
													NoSuchEntityException.class.getName())) {
										view.setProjectWikiPage(null);
									} else {
										super.onFailure(exception);
									}
								}
							});
		}
		List<ProjectService> mavenServices = project.getProjectServicesOfType(ServiceType.MAVEN);
		view.setMavenService(mavenServices.isEmpty() ? null : mavenServices.get(0));

		boolean hasScm = isServiceAvailable(ServiceType.SCM);
		view.setHasScmService(hasScm);

		if (hasScm) {
			view.setScmRepositories(place.getRepositories());
		}

		AppGinjector.get
				.instance()
				.getDispatchService()
				.execute(new GetProjectActivityAction(project.getIdentifier(), true),
						new AsyncCallbackSupport<GetProjectActivityResult>() {

							@Override
							protected void success(GetProjectActivityResult result) {
								view.activityView.renderActivity(result.get());

							}
						});

	}

	private boolean isServiceAvailable(ServiceType type) {
		for (ProjectService wikiService : project.getProjectServicesOfType(type)) {
			if (wikiService.isAvailable()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void bind() {

	}
}
