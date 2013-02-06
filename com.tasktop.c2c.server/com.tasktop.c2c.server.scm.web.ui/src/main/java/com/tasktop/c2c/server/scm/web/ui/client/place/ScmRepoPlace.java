package com.tasktop.c2c.server.scm.web.ui.client.place;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
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
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectScmRepositoriesResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmMessages;

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
public class ScmRepoPlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace, BreadcrumbPlace,
		SectionPlace, WindowTitlePlace {
	private static String REPO_ARG = "repo";
	private static String BRANCH_ARG = "branch";

	public static PageMapping SCM_LOG = new PageMapping(new Tokenizer(), Path.PROJECT_BASE + "/{" + Path.PROJECT_ID
			+ "}/scm/{" + REPO_ARG + "}/commits", Path.PROJECT_BASE + "/{" + Path.PROJECT_ID + "}/scm/{" + REPO_ARG
			+ "}/commits/{" + BRANCH_ARG + "}");

	private static class Tokenizer extends AbstractPlaceTokenizer<ScmRepoPlace> {

		@Override
		public ScmRepoPlace getPlace(String token) {
			Args pathArgs = getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID), pathArgs.getString(REPO_ARG),
					pathArgs.getString(BRANCH_ARG));
		}

	}

	public static ScmRepoPlace createPlace(String projectId, String repoName) {
		return new ScmRepoPlace(projectId, repoName, null);
	}

	public static ScmRepoPlace createPlace(String projectId, String repoName, String branchName) {
		return new ScmRepoPlace(projectId, repoName, branchName);
	}

	private ScmRepoPlace(String projectId, String repoName, String branchName) {
		this.projectId = projectId;
		this.repositoryName = repoName;
		this.branchName = branchName;
	}

	private String repositoryName;
	protected String projectId;
	private String branchName;
	protected Project project;
	private ScmRepository repository;
	private ScmMessages scmMessages = GWT.create(ScmMessages.class);

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
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectAction(projectId));
		addAction(new GetProjectScmRepositoriesAction(projectId));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();

		for (ScmRepository repo : getResult(GetProjectScmRepositoriesResult.class).get()) {
			if (repo.getName().equals(repositoryName)) {
				this.repository = repo;
			}
		}
		super.onPlaceDataFetched();
	}

	@Override
	public String getWindowTitle() {
		return scmMessages.repoTitle(repositoryName, project.getName(), WindowTitleBuilder.PRODUCT_NAME);

	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(ScmPlace.createPlace(projectId).getHref(), scmMessages.source()));
		breadcrumbs.add(new Breadcrumb(getHref(), repositoryName));
		return breadcrumbs;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);
		tokenMap.put(REPO_ARG, repositoryName);
		if (branchName != null) {
			tokenMap.put(BRANCH_ARG, branchName);
		}

		return SCM_LOG.getUrlForNamedArgs(tokenMap);
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public ScmRepository getRepository() {
		return repository;
	}

	public String getBranchName() {
		return branchName;
	}

}
