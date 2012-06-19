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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HasProjectPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.SectionPlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.service.domain.QueryRequest;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.wiki.domain.Page;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.FindPagesAction;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.FindPagesResult;

public class ProjectWikiHomePlace extends AbstractProjectWikiPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, SectionPlace {

	public static String QUERY_P = "query";

	public static PageMapping ProjectWiki = new PageMapping(new ProjectWikiHomePlace.Tokenizer(), Path.PROJECT_BASE
			+ "/{" + Path.PROJECT_ID + "}/wiki/", Path.PROJECT_BASE + "/{" + Path.PROJECT_ID + "}/wiki/q/{"
			+ ProjectWikiHomePlace.QUERY_P + "}");

	public static class Tokenizer extends AbstractPlaceTokenizer<ProjectWikiHomePlace> {

		@Override
		public ProjectWikiHomePlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);
			String projectId = pathArgs.getString(Path.PROJECT_ID);
			String query = pathArgs.getString(QUERY_P);

			if (query == null) {
				return new ProjectWikiHomePlace(projectId, "");
			} else {
				return createQueryPlace(projectId, query);
			}
		}

	}

	private String query;
	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
	private List<Page> pages;

	public ProjectWikiHomePlace(String projectIdentifier, String query) {
		super(projectIdentifier);
		this.query = query;
	}

	public static ProjectWikiHomePlace createDefaultPlace(String projectIdentifier) {
		return new ProjectWikiHomePlace(projectIdentifier, "");
	}

	public static ProjectWikiHomePlace createQueryPlace(String projectIdentifier, String query) {
		return new ProjectWikiHomePlace(projectIdentifier, query);
	}

	public List<Page> getPages() {
		return pages;
	}

	public String getSearchTerm() {
		return query;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);
		if (query != null && !query.isEmpty()) {
			tokenMap.put(QUERY_P, query);
		}

		String base = ProjectWiki.getUrlForNamedArgs(tokenMap);

		return base;
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	protected void addActions() {
		super.addActions();
		QueryRequest queryRequest = new QueryRequest();
		queryRequest.setPageInfo(new Region(0, 5000));
		addAction(new FindPagesAction(projectId, query, queryRequest));

	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		pages = getResult(FindPagesResult.class).get().getResultPage();
		createBreadcrumbs(project);
		onPlaceDataFetched();
	}

	private void createBreadcrumbs(Project project) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(getHistoryToken(), "Wiki"));
	}

	@Override
	public String getWindowTitle() {
		return "Wiki - " + project.getName() + " - " + WindowTitleBuilder.PRODUCT_NAME;
	}

	public Boolean isTreeView() {
		return query == null || query.isEmpty();
	}
}
