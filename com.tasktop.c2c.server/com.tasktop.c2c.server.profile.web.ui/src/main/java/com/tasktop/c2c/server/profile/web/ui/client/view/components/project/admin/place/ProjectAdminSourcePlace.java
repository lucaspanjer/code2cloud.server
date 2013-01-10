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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.place;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectAdminPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectAdminSettingsPlace;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.scm.domain.ScmRepository;

public class ProjectAdminSourcePlace extends ProjectAdminPlace implements BreadcrumbPlace {

	public static PageMapping ProjectAdminSCM = new PageMapping(new ProjectAdminSourcePlace.Tokenizer(),
			Path.PROJECT_BASE + "/{" + Path.PROJECT_ID + "}/admin/scm");

	private static class Tokenizer extends AbstractPlaceTokenizer<ProjectAdminSourcePlace> {

		@Override
		public ProjectAdminSourcePlace getPlace(String token) {
			Args pathArgs = getPathArgsForUrl(token);

			String projId = pathArgs.getString(Path.PROJECT_ID);

			return createPlace(projId);
		}

	}

	public static ProjectAdminSourcePlace createPlace(String projectId) {
		return new ProjectAdminSourcePlace(projectId);
	}

	private List<ScmRepository> repositories;
	private String gitBaseUrl;
	private String publicSshKey;

	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	private ProjectAdminSourcePlace(String projectId) {
		super(projectId);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectIdentifer);

		return ProjectAdminSCM.getUrlForNamedArgs(tokenMap);
	}

	@Override
	public Project getProject() {
		return project;
	}

	public List<ScmRepository> getRepositories() {
		return repositories;
	}

	public String getGitBaseUrl() {
		return gitBaseUrl;
	}

	@Override
	public String getHeading() {
		return project.getName();
	}

	@Override
	public Section getSection() {
		return Section.ADMIN;
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectScmRepositoriesAction(projectIdentifer));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		repositories = getResult(GetProjectScmRepositoriesResult.class).get();
		gitBaseUrl = getResult(GetProjectScmRepositoriesResult.class).getGitRepositoryBaseUrl();
		publicSshKey = getResult(GetProjectScmRepositoriesResult.class).getPublicSshKey();

		createBreadcrumbs(project);
		onPlaceDataFetched();
	}

	private void createBreadcrumbs(Project project) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(ProjectAdminSettingsPlace.createPlace(project.getIdentifier()).getHref(),
				profileMessages.settings()));
		breadcrumbs.add(new Breadcrumb(getHref(), profileMessages.source()));
	}

	/**
	 * @return the publicSshKey
	 */
	public String getPublicSshKey() {
		return publicSshKey;
	}

}
