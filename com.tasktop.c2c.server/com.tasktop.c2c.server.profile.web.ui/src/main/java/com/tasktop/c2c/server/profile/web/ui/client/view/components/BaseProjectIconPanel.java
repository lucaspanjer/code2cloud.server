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
package com.tasktop.c2c.server.profile.web.ui.client.view.components;

import java.util.Arrays;

import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectHomePlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.common.web.client.event.ClearCacheEvent;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectService;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectDashboardPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectDeploymentPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectTeamPlace;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectTasksPlace;
import com.tasktop.c2c.server.wiki.web.ui.client.place.ProjectWikiHomePlace;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class BaseProjectIconPanel extends Composite implements IProjectIconPanel {

	@UiField
	public Anchor projectHome;
	@UiField
	public Anchor dashboard;
	@UiField
	public Anchor tasks;
	@UiField
	public Anchor source;
	@UiField
	public Anchor builds;
	@UiField
	public Anchor reviews;
	@UiField
	public Anchor deployments;
	@UiField
	public Anchor team;
	@UiField
	public Anchor wiki;
	private Project project;
	private static final String ACTIVE_STYLE = "active";

	public BaseProjectIconPanel() {
		super();
	}

	private static final String DISABLED_STYLE = "disabled working";
	private static int SERVICE_PROVISION_POLL_DELAY = 15000;

	@Override
	public void setActiveIcon(Section activeIcon) {

		// first, deactivate all of our existing icons.
		deactivateAllIcons();

		// No active icon? Bail out now that all are deactivated.
		if (activeIcon == null) {
			return;
		}

		// Then, activate the appropriate icon
		Anchor toActivate = null;
		switch (activeIcon) {
		case BUILDS:
			toActivate = builds;
			break;
		case DASHBOARD:
			toActivate = dashboard;
			break;
		case DEPLOYMENTS:
			toActivate = deployments;
			break;
		case HOME:
			toActivate = projectHome;
			break;
		case TASKS:
			toActivate = tasks;
			break;
		case TEAM:
			toActivate = team;
			break;
		case WIKI:
			toActivate = wiki;
			break;
		case SCM:
			toActivate = source;
			break;
		}
		if (toActivate != null) {
			getSelectStyleElement(toActivate).addClassName(getActiveStyle());
		}
	}

	protected void deactivateAllIcons() {
		for (Anchor a : Arrays.asList(projectHome, dashboard, tasks, builds, deployments, team, wiki, source, reviews)) {
			Element e = getSelectStyleElement(a);
			e.removeClassName(getActiveStyle());
		}
	}

	protected Element getSelectStyleElement(Anchor a) {
		return a.getElement();
	}

	protected String getActiveStyle() {
		return ACTIVE_STYLE;
	}

	@Override
	public void setProject(Project project) {
		this.project = project;
		if (project == null) {
			this.setVisible(false);
			return;
		}
		this.setVisible(true);

		projectHome.setHref(ProjectHomePlace.createPlace(project.getIdentifier()).getHref());
		team.setHref(ProjectTeamPlace.createPlace(project.getIdentifier()).getHref());

		boolean allServicesReady = true;

		hideAllServiceLinks();
		if (project.getProjectServices() != null) {
			for (ProjectService projectService : project.getProjectServices()) {

				boolean isAvailable = projectService.isAvailable();

				if (!isAvailable) {
					allServicesReady = false;
				}

				switch (projectService.getServiceType()) {
				case BUILD:
					setServiceLinkEnabled(isAvailable, builds, projectService.getUrl());
					break;

				case TASKS:
					setServiceLinkEnabled(isAvailable, tasks,
							ProjectTasksPlace.createDefaultPlace(project.getIdentifier()).getHref());
					break;
				case WIKI:
					setServiceLinkEnabled(isAvailable, wiki,
							ProjectWikiHomePlace.createDefaultPlace(project.getIdentifier()).getHref());
					break;
				case DEPLOYMENT:
					setServiceLinkEnabled(isAvailable, deployments,
							ProjectDeploymentPlace.createPlace(project.getIdentifier()).getHref());
					break;
				case SCM:
					setServiceLinkEnabled(isAvailable, source, ScmPlace.createPlace(project.getIdentifier()).getHref());
					break;

				case REVIEW:
					setServiceLinkEnabled(isAvailable, reviews, projectService.getUrl());
					break;

				default:
					break;
				}
			}
		}

		setServiceLinkEnabled(true, dashboard, ProjectDashboardPlace.createPlace(project.getIdentifier()).getHref());

		if (!allServicesReady) {
			setupProjectServiceCallback(project.getIdentifier());
		}

	}

	private void setupProjectServiceCallback(final String projectIdentifier) {

		Timer timer = new Timer() {

			@Override
			public void run() {
				if (!stillOnPage(projectIdentifier)) {
					return;
				}

				AppGinjector.get.instance().getEventBus().fireEvent(new ClearCacheEvent());
				AppGinjector.get.instance().getDispatchService()
						.execute(new GetProjectAction(projectIdentifier), new AsyncCallbackSupport<GetProjectResult>() {
							@Override
							protected void success(GetProjectResult result) {
								if (!stillOnPage(projectIdentifier)) {
									return;
								}
								// Re-set our project to trigger recalculation of current service availability.
								setProject(result.get());
							}
						});
			}
		};

		timer.schedule(SERVICE_PROVISION_POLL_DELAY);
	}

	private boolean stillOnPage(String projectId) {
		return project != null && projectId.equals(project.getIdentifier()) && isAttached() && isVisible();
	}

	private void hideAllServiceLinks() {
		tasks.setVisible(false);
		wiki.setVisible(false);
		deployments.setVisible(false);
		builds.setVisible(false);
		source.setVisible(false);
		reviews.setVisible(false);
	}

	private void setServiceLinkEnabled(boolean isEnabled, Anchor link, String href) {
		link.setVisible(true);
		if (isEnabled) {
			link.setHref(href);
			link.removeStyleName(DISABLED_STYLE);
		} else {
			// Wipe out our href attribute - setting it to null or blank causes it to redirect to the homepage.
			link.getElement().removeAttribute("href");
			link.addStyleName(DISABLED_STYLE);
		}
	}

}