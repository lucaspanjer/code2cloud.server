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

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HasProjectPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectsPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.client.place.SectionPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.Credentials;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.event.LogonEvent;
import com.tasktop.c2c.server.profile.web.ui.client.event.LogonEventHandler;
import com.tasktop.c2c.server.profile.web.ui.client.event.LogoutEvent;
import com.tasktop.c2c.server.profile.web.ui.client.event.LogoutEventHandler;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.Header;

public class HeaderPresenter extends AbstractProfilePresenter implements Header.Presenter {

	private final Header view;

	public HeaderPresenter(final Header view) {
		super(view);
		this.view = view;

		view.setPresenter(this);

		addAuthListeners();
	}

	public Header getView() {
		return view;
	}

	private void addAuthListeners() {
		final AppGinjector injector = AppGinjector.get.instance();

		EventBus eventBus = injector.getEventBus();
		eventBus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {
			@Override
			public void onLogout() {
				view.setCredentials(null);
			}
		});
		eventBus.addHandler(LogonEvent.TYPE, new LogonEventHandler() {
			@Override
			public void onLogon(Credentials credentials) {
				injector.getAppState().setCredentials(credentials);
				view.setCredentials(credentials);
			}
		});
	}

	public void setPlace(Place place) {
		String heading = "";
		List<Breadcrumb> breadcrumbs = Collections.EMPTY_LIST;
		Project project = null;
		Section section = null;
		String windowTitle = WindowTitleBuilder.PRODUCT_NAME;

		if (place instanceof HasProjectPlace) {
			project = ((HasProjectPlace) place).getProject();
		}
		if (place instanceof HeadingPlace) {
			heading = ((HeadingPlace) place).getHeading();
		}
		if (place instanceof BreadcrumbPlace) {
			breadcrumbs = ((BreadcrumbPlace) place).getBreadcrumbs();
		}
		if (place instanceof SectionPlace) {
			section = ((SectionPlace) place).getSection();
		}
		if (place instanceof WindowTitlePlace) {
			windowTitle = ((WindowTitlePlace) place).getWindowTitle();
		}
		String currentQuery = null;
		if (place instanceof ProjectsPlace) {
			currentQuery = ((ProjectsPlace) place).getQuery();
		}
		view.setSearchText(currentQuery);

		view.setProject(project);
		view.setPageTitle(heading);
		view.setBreadcrumbs(breadcrumbs);
		view.setSection(section);
		if (windowTitle != null) {
			Window.setTitle(windowTitle);
		}

		view.setCredentials(getAppState().getCredentials());
		view.setGravatarHash(getAppState().getCredentials() == null ? null : getAppState().getCredentials()
				.getProfile().getGravatarHash());

	}

	@Override
	public void bind() {

	}

	public void doSearch(String queryText) {
		if (queryText == null || queryText.trim().length() == 0) {
			return;
		} else {
			ProjectsPlace.createPlaceForQuery(queryText).go();
		}
	}
}
