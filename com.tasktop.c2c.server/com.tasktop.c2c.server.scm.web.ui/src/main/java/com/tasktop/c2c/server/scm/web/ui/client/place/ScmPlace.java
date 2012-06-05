package com.tasktop.c2c.server.scm.web.ui.client.place;

import java.util.LinkedHashMap;
import java.util.List;

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.AbstractBatchFetchingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HasProjectPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.client.place.SectionPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.scm.domain.ScmRepository;

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

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmPlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace, BreadcrumbPlace,
		SectionPlace, WindowTitlePlace {
	public static PageMapping SCM_PLACE = new PageMapping(new Tokenizer(), Path.PROJECT_BASE + "/{" + Path.PROJECT_ID
			+ "}/scm/");

	public static class Tokenizer implements PlaceTokenizer<ScmPlace> {

		@Override
		public ScmPlace getPlace(String token) {
			Args pathArgs = PageMapping.getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID));
		}

		@Override
		public String getToken(ScmPlace place) {
			return place.getToken();
		}
	}

	public static ScmPlace createPlace(String projectId) {
		return new ScmPlace(projectId);
	}

	private ScmPlace(String projectId) {
		this.projectId = projectId;
	}

	protected String projectId;
	protected Project project;
	private List<ScmRepository> repositories;

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
		return Section.SCM;
	}

	@Override
	public String getToken() {
		return "";
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectAction(projectId));
		addAction(new GetProjectScmRepositoriesAction(projectId));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();
		repositories = getResult(GetProjectScmRepositoriesResult.class).get();

		super.onPlaceDataFetched();
	}

	@Override
	public String getWindowTitle() {
		return "Source repositories - " + project.getName() + " - " + WindowTitleBuilder.PRODUCT_NAME;

	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(getHistoryToken(), "Source"));
		return breadcrumbs;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);

		return SCM_PLACE.getUrlForNamedArgs(tokenMap);
	}

	public List<ScmRepository> getRepositories() {
		return repositories;
	}

}
