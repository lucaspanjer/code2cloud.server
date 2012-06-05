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
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.web.ui.client.shared.action.GetScmCommitAction;
import com.tasktop.c2c.server.scm.web.ui.client.shared.action.GetScmCommitResult;

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
public class ScmCommitPlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, SectionPlace, WindowTitlePlace {
	public static String REPO_ARG = "repo";
	public static String COMMIT_ARG = "commit";
	public static PageMapping SCM_COMMIT = new PageMapping(new Tokenizer(), Path.PROJECT_BASE + "/{" + Path.PROJECT_ID
			+ "}/scm/{" + REPO_ARG + "}/{" + COMMIT_ARG + "}");

	public static class Tokenizer implements PlaceTokenizer<ScmCommitPlace> {

		@Override
		public ScmCommitPlace getPlace(String token) {
			Args pathArgs = PageMapping.getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID), pathArgs.getString(REPO_ARG),
					pathArgs.getString(COMMIT_ARG));
		}

		@Override
		public String getToken(ScmCommitPlace place) {
			return place.getToken();
		}
	}

	public static ScmCommitPlace createPlace(String projectId, String repoName, String commitId) {
		return new ScmCommitPlace(projectId, repoName, commitId);
	}

	private ScmCommitPlace(String projectId, String repoName, String commitId) {
		this.projectId = projectId;
		this.repositoryName = repoName;
		this.commitId = commitId;
	}

	private String commitId;
	private String repositoryName;
	protected String projectId;
	protected Project project;
	protected Commit commit;

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
		addAction(new GetScmCommitAction(repositoryName, commitId, projectId));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();
		commit = getResult(GetScmCommitResult.class).get();
		super.onPlaceDataFetched();
	}

	@Override
	public String getWindowTitle() {
		return "Commit " + commitId + " of " + repositoryName + " - " + project.getName() + " - "
				+ WindowTitleBuilder.PRODUCT_NAME;

	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(ScmPlace.createPlace(projectId).getHistoryToken(), "Source"));
		breadcrumbs.add(new Breadcrumb(ScmRepoPlace.createPlace(projectId, repositoryName).getHistoryToken(),
				repositoryName));
		breadcrumbs.add(new Breadcrumb(getHistoryToken(), commit == null ? commitId : commit.getMinimizedCommitId()));
		return breadcrumbs;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);
		tokenMap.put(REPO_ARG, repositoryName);
		tokenMap.put(COMMIT_ARG, commitId);

		return SCM_COMMIT.getUrlForNamedArgs(tokenMap);
	}

	public Commit getCommit() {
		return commit;
	}

}
