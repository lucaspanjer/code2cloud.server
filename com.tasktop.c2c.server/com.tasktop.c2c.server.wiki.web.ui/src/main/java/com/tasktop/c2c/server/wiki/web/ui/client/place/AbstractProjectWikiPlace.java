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
package com.tasktop.c2c.server.wiki.web.ui.client.place;

import com.google.gwt.core.client.GWT;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.place.AbstractBatchFetchingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HasProjectPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.client.place.SectionPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.wiki.web.ui.client.WikiMessages;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractProjectWikiPlace extends AbstractBatchFetchingPlace implements HeadingPlace,
		HasProjectPlace, BreadcrumbPlace, SectionPlace, WindowTitlePlace {

	protected String projectId;
	protected Project project;
	protected CommonProfileMessages commonProfileMessages = GWT.create(CommonProfileMessages.class);
	protected WikiMessages wikiMessages = GWT.create(WikiMessages.class);

	/**
	 * @param roles
	 */
	public AbstractProjectWikiPlace(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * 
	 */
	public AbstractProjectWikiPlace() {
		super();
	}

	public Project getProject() {
		return project;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	@Override
	public String getHeading() {
		return project.getName();
	}

	@Override
	public Section getSection() {
		return Section.WIKI;
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
	}

}
