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
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.wiki.domain.Attachment;
import com.tasktop.c2c.server.wiki.domain.Page;
import com.tasktop.c2c.server.wiki.service.WikiService;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.ListAttachmentsAction;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.ListAttachmentsResult;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.RetrieveConfigurationPropertyAction;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.RetrieveConfigurationPropertyResult;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.RetrievePageAction;
import com.tasktop.c2c.server.wiki.web.ui.shared.action.RetrievePageResult;

public class ProjectWikiEditPagePlace extends AbstractProjectWikiPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, SectionPlace {

	public static PageMapping ProjectWikiEditPage = new PageMapping(new ProjectWikiEditPagePlace.Tokenizer(),
			Path.PROJECT_BASE + "/{" + Path.PROJECT_ID + "}/wiki/edit/{" + ProjectWikiViewPagePlace.PAGE + ":*}",
			Path.PROJECT_BASE + "/{" + Path.PROJECT_ID + "}/wiki/new");

	private static class Tokenizer extends AbstractPlaceTokenizer<ProjectWikiEditPagePlace> {

		@Override
		public ProjectWikiEditPagePlace getPlace(String token) {
			Args pathArgs = getPathArgsForUrl(token);

			String projId = pathArgs.getString(Path.PROJECT_ID);
			String pageName = pathArgs.getString(ProjectWikiViewPagePlace.PAGE);
			if (StringUtils.hasText(pageName)) {
				return createPlaceForPath(projId, pageName);
			} else {
				return createPlaceForNewPage(projId);
			}
		}
	}

	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
	private boolean isNew;
	private String pagePath;
	private String markupLanguage;
	private Page page;
	private List<Attachment> attachements;

	public static ProjectWikiEditPagePlace createPlaceForPath(String projectIdentifier, String path) {
		return new ProjectWikiEditPagePlace(projectIdentifier, path);
	}

	public static ProjectWikiEditPagePlace createPlaceForNewPage(String projectIdentifier) {
		return new ProjectWikiEditPagePlace(projectIdentifier, null);
	}

	private ProjectWikiEditPagePlace(String projectIdentifier, String pagePath) {
		super(projectIdentifier);
		this.pagePath = pagePath;
	}

	public String getMarkupLanguage() {
		return markupLanguage;
	}

	public String getHeading() {
		return project.getName();
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);

		if (StringUtils.hasText(pagePath)) {
			tokenMap.put(ProjectWikiViewPagePlace.PAGE, pagePath);
		}

		return ProjectWikiEditPage.getUrlForNamedArgs(tokenMap);
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	protected void addActions() {
		super.addActions();
		addActionAndIgnoreFailure(new RetrievePageAction(projectId, pagePath, false));
		addAction(new ListAttachmentsAction(projectId, pagePath));
		addAction(new RetrieveConfigurationPropertyAction(projectId, WikiService.MARKUP_LANGUAGE_DB_KEY));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		RetrievePageResult retrievePageResult = getResult(RetrievePageResult.class);
		isNew = retrievePageResult == null;
		if (!isNew) {
			page = retrievePageResult.get();
			attachements = getResult(ListAttachmentsResult.class).get();
		}
		RetrieveConfigurationPropertyResult retrieveConfigurationPropertyResult = getResult(RetrieveConfigurationPropertyResult.class);
		markupLanguage = retrieveConfigurationPropertyResult.get();

		createBreadcrumbs(project);
		onPlaceDataFetched();
	}

	private void createBreadcrumbs(Project project) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(ProjectWikiHomePlace.createDefaultPlace(projectId).getHref(),
				super.commonProfileMessages.wiki()));

		if (!isNew) {
			breadcrumbs.add(new Breadcrumb(ProjectWikiViewPagePlace.createPlaceForPage(projectId, pagePath).getHref(),
					pagePath));
			breadcrumbs.add(new Breadcrumb(getHref(), super.commonProfileMessages.edit()));
		} else {
			breadcrumbs.add(new Breadcrumb(getHref(), super.wikiMessages.newPage()));
		}
	}

	public String getPath() {
		return pagePath;
	}

	/**
	 * @return the page
	 */
	public Page getPage() {
		return page;
	}

	@Override
	public String getWindowTitle() {
		if (pagePath != null) {
			return super.wikiMessages.editWikiWindowTitle(pagePath, project.getName(), WindowTitleBuilder.PRODUCT_NAME);
		} else {
			return super.wikiMessages.newWikiWindowTitle(project.getName(), WindowTitleBuilder.PRODUCT_NAME);
		}
	}

	/**
	 * @return
	 */
	public List<Attachment> getAttachements() {
		return attachements;
	}

	public boolean isNew() {
		return isNew;
	}

}
