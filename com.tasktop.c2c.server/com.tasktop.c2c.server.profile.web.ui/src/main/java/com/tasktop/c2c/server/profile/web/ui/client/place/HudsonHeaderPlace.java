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
package com.tasktop.c2c.server.profile.web.ui.client.place;

import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.tasktop.c2c.server.common.profile.web.client.place.AbstractBatchFetchingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HasProjectPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.components.HeaderPresenter;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.Header;

/**
 * This is used by hudson to hook into the GWT header. It is used differently from other places in that it is created
 * using javascript defined in HudsonAPI.java. On go() it will instantiate its own view/presenter and attache to the
 * given elementId
 * 
 * @author straxus (Tasktop Technologies Inc.)
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 */
public class HudsonHeaderPlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, WindowTitlePlace {

	private final String projectId;
	private Project project;
	private List<Breadcrumb> breadcrumbs = Collections.EMPTY_LIST;
	private String elementId;

	public static HudsonHeaderPlace createPlace(String projectId, String elementId) {
		return new HudsonHeaderPlace(projectId, elementId);
	}

	private HudsonHeaderPlace(String projectId, String elementId) {
		this.elementId = elementId;
		this.projectId = projectId;
	}

	@Override
	public String getPrefix() {
		return "";
	}

	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public String getHeading() {
		if (project != null) {
			return project.getName();
		}
		return "";
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectAction(projectId));

	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();
		createBreadcrumbs(project);

		HeaderPresenter headerPresenter = new HeaderPresenter((Header) GWT.create(Header.class));
		headerPresenter.setPlace(this);
		Header header = headerPresenter.getView();

		HTMLPanel container = HTMLPanel.wrap(Document.get().getElementById(elementId));
		container.add(header);
	}

	private void createBreadcrumbs(Project project) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
	}

	@Override
	public String getWindowTitle() {
		if (project != null) {
			return WindowTitleBuilder.createWindowTitle(Section.BUILDS, project.getName());
		}
		return null; // Don't use this title.
	}

}
