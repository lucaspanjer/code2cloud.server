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
package com.tasktop.c2c.server.common.profile.web.client.place;

import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.profile.domain.project.Project;

/**
 * @author cmorgan
 * 
 */
public abstract class ProjectAdminPlace extends LoggedInPlace implements HeadingPlace, SectionPlace, WindowTitlePlace,
		HasProjectPlace, BreadcrumbPlace {

	protected String projectIdentifer;
	protected Project project;

	protected ProjectAdminPlace(String projectIdentifer) {
		this.projectIdentifer = projectIdentifer;
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(Section.ADMIN, project.getName());
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectAction(projectIdentifer));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();
	}

	@Override
	public String getHeading() {
		return super.commonProfileMessages.projectAdmin();
	}

	@Override
	public Section getSection() {
		return Section.ADMIN;
	}

	/**
	 * @return the projectIdentifer
	 */
	public String getProjectIdentifer() {
		return projectIdentifer;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

}
