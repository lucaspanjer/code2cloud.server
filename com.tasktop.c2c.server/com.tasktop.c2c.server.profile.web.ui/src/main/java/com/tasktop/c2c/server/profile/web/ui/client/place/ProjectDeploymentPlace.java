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
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsResult;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectDeploymentsAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectDeploymentsResult;

public class ProjectDeploymentPlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, SectionPlace, WindowTitlePlace {

	public static PageMapping ProjectDeployment = new PageMapping(new ProjectDeploymentPlace.Tokenizer(),
			Path.PROJECT_BASE + "/{" + Path.PROJECT_ID + "}/deployments");

	public static class Tokenizer extends AbstractPlaceTokenizer<ProjectDeploymentPlace> {

		@Override
		public ProjectDeploymentPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID));
		}

	}

	private String projectId;
	private Project project;
	private List<Breadcrumb> breadcrumbs;
	private List<DeploymentConfiguration> deploymentConfigurations;
	private GetProjectBuildsResult buildInformation;

	public Project getProject() {
		return project;
	}

	@Override
	public String getHeading() {
		return project.getName();
	}

	@Override
	public Section getSection() {
		return Section.DEPLOYMENTS;
	}

	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	public List<DeploymentConfiguration> getDeploymentConfigurations() {
		return deploymentConfigurations;
	}

	public GetProjectBuildsResult getBuildInformation() {
		return buildInformation;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);

		return ProjectDeployment.getUrlForNamedArgs(tokenMap);
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectAction(projectId));
		addAction(new GetProjectDeploymentsAction(projectId));
		addAction(new GetProjectBuildsAction(projectId, null));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();
		deploymentConfigurations = getResult(GetProjectDeploymentsResult.class).get();
		buildInformation = getResult(GetProjectBuildsResult.class);
		createBreadCrumbs(project);
		onPlaceDataFetched();
	}

	public static ProjectDeploymentPlace createPlace(String projectId) {
		return new ProjectDeploymentPlace(projectId);
	}

	private ProjectDeploymentPlace(String projectId) {
		this.projectId = projectId;
	}

	private void createBreadCrumbs(Project project) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(ProjectDeploymentPlace.createPlace(project.getIdentifier()).getHref(),
				"Deployments"));
	}

	@Override
	public String getWindowTitle() {
		return "Deployments - " + project.getName() + " - " + WindowTitleBuilder.PRODUCT_NAME;
	}
}
