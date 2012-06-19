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
import java.util.LinkedHashMap;
import java.util.List;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
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
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;

/**
 * This is used by hudson to hook into the GWT header and footer.
 * 
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class AppSectionPlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, SectionPlace, WindowTitlePlace {
	static {
		AppGinjector.get.instance().getAppResources().appCss().ensureInjected();
	}

	public static PageMapping AppSectionMapping = new PageMapping(new AppSectionPlace.Tokenizer(), Path.PROJECT_BASE
			+ "/{" + Path.PROJECT_ID + "}/section/{" + AppSectionPlace.SECTION + "}");

	public enum AppSection {
		HEADER("header-wrapper", AppGinjector.get.instance().getAppResources().appCss().headerWrapper()), //
		FOOTER("footer-stretch", AppGinjector.get.instance().getAppResources().appCss().footerStretch());

		private final String styleName;
		private final String pathName;

		private AppSection(String pathName, String styleName) {
			this.styleName = styleName;
			this.pathName = pathName;
		}

		public String getStyleName() {
			return styleName;
		}

		public String getPathName() {
			return pathName;
		}
	}

	public static final String SECTION = "displaySection";

	public static class Tokenizer extends AbstractPlaceTokenizer<AppSectionPlace> {

		@Override
		public AppSectionPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID), pathArgs.getString(SECTION));
		}
	}

	private final String projectId;
	private Project project;
	private List<Breadcrumb> breadcrumbs = Collections.EMPTY_LIST;
	private AppSection appSection;

	public static AppSectionPlace createPlace(String projectId, String sectionStyle) {
		return new AppSectionPlace(projectId, sectionStyle);
	}

	private AppSectionPlace(String projectId, String sectionPathName) {
		this.projectId = projectId;
		for (AppSection s : AppSection.values()) {
			if (s.getPathName().equals(sectionPathName)) {
				this.appSection = s;
			}
		}
		if (AppSection.FOOTER.equals(appSection)) {
			super.requiresUserInfo = false;
		}
	}

	public AppSection getSectionToShow() {
		return this.appSection;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);
		tokenMap.put(SECTION, appSection.getPathName());

		return AppSectionMapping.getUrlForNamedArgs(tokenMap);
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
	public Section getSection() {
		return Section.BUILDS;
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	protected void addActions() {
		super.addActions();
		if (AppSection.HEADER.equals(appSection)) {
			addAction(new GetProjectAction(projectId));
		}
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		if (AppSection.HEADER.equals(appSection)) {
			project = getResult(GetProjectResult.class).get();
			createBreadcrumbs(project);
		}
		onPlaceDataFetched();
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
