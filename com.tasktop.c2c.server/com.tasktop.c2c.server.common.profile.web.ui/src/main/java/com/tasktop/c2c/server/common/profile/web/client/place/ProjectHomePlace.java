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

import java.util.LinkedHashMap;
import java.util.List;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.scm.domain.ScmRepository;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class ProjectHomePlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, SectionPlace, WindowTitlePlace {

	public static final String WIKI_HOME_PAGE = "Home";

	private static final String NO_ID = "create";

	public static PageMapping ProjectHome = new PageMapping(new ProjectHomePlace.Tokenizer(), Path.PROJECT_BASE + "/{"
			+ Path.PROJECT_ID + "}");

	private static class Tokenizer extends AbstractPlaceTokenizer<ProjectHomePlace> {

		@Override
		public ProjectHomePlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID));
		}
	}

	private String projectId;
	private Project project;
	private List<ScmRepository> repositories;
	private List<Breadcrumb> breadcrumbs;

	public static ProjectHomePlace createPlace(String projectId) {
		if (projectId == null || projectId.length() == 0 || projectId.equals("")) {
			projectId = NO_ID;
		}
		return new ProjectHomePlace(projectId);
	}

	private ProjectHomePlace() {
		this(NO_ID);
	}

	private ProjectHomePlace(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);

		return ProjectHome.getUrlForNamedArgs(tokenMap);
	}

	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public String getHeading() {
		return project.getName();
	}

	@Override
	public Section getSection() {
		return Section.HOME;
	}

	public List<ScmRepository> getRepositories() {
		return repositories;
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectAction(projectId));
		addActionAndIgnoreFailure(new GetProjectScmRepositoriesAction(projectId));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();

		GetProjectScmRepositoriesResult getReposResult = getResult(GetProjectScmRepositoriesResult.class);
		if (getReposResult != null) {
			repositories = getReposResult.get();
		}
		createBreadcrumbs(project);
		onPlaceDataFetched();
	}

	private void createBreadcrumbs(Project project) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(Section.HOME, project.getName());
	}

}
