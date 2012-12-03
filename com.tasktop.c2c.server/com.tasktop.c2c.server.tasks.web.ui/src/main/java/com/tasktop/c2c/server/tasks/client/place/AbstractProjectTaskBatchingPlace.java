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
package com.tasktop.c2c.server.tasks.client.place;

import java.util.List;

import net.customware.gwt.dispatch.shared.Action;

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

/**
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractProjectTaskBatchingPlace extends AbstractBatchFetchingPlace implements HeadingPlace,
		HasProjectPlace, BreadcrumbPlace, SectionPlace, WindowTitlePlace {

	protected String projectId;
	protected Project project;

	/**
	 * @param roles
	 */
	public AbstractProjectTaskBatchingPlace(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * 
	 */
	public AbstractProjectTaskBatchingPlace() {
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
		return Section.TASKS;
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
