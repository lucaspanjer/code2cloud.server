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
package com.tasktop.c2c.server.tasks.client.place;

import java.util.LinkedHashMap;
import java.util.List;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.tasks.domain.RepositoryConfiguration;
import com.tasktop.c2c.server.tasks.domain.Task;
import com.tasktop.c2c.server.tasks.shared.action.GetRepositoryConfigurationAction;
import com.tasktop.c2c.server.tasks.shared.action.GetRepositoryConfigurationResult;
import com.tasktop.c2c.server.tasks.shared.action.GetTaskAction;
import com.tasktop.c2c.server.tasks.shared.action.GetTaskResult;

/**
 * @author jtyrrell
 * @author jhickey
 * 
 */
public class ProjectTaskPlace extends AbstractProjectTaskBatchingPlace {

	private static final String TASK = "taskId";

	public static PageMapping ProjectTask = new PageMapping(new ProjectTaskPlace.Tokenizer(), Path.PROJECT_BASE + "/{"
			+ Path.PROJECT_ID + "}/task/{" + TASK + ":Integer}");

	private static class Tokenizer extends AbstractPlaceTokenizer<ProjectTaskPlace> {

		@Override
		public ProjectTaskPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID), pathArgs.getInteger(TASK));
		}

	}

	private Integer taskId;
	private Task task;
	private RepositoryConfiguration repositoryConfiguration;
	private List<Breadcrumb> breadcrumbs;

	public static ProjectTaskPlace createPlace(String projectId, Integer taskId) {
		return new ProjectTaskPlace(projectId, taskId);
	}

	public static ProjectTaskPlace createPlaceWithTask(String projectId, Task task) {
		return new ProjectTaskPlace(projectId, task);
	}

	protected ProjectTaskPlace(String projectId, Integer taskId) {
		super(projectId);
		this.taskId = taskId;
	}

	protected ProjectTaskPlace(String projectId, Task task) {
		super(projectId);
		this.taskId = task.getId();
		this.task = task;
	}

	@Override
	public String getWindowTitle() {
		return task.getTaskType() + " " + task.getId() + " - " + task.getShortDescription() + " - " + project.getName()
				+ " - " + WindowTitleBuilder.PRODUCT_NAME;
	}

	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);
		tokenMap.put(TASK, String.valueOf(taskId));

		return ProjectTask.getUrlForNamedArgs(tokenMap);
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public Task getTask() {
		return task;
	}

	public RepositoryConfiguration getRepositoryConfiguration() {
		return repositoryConfiguration;
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetRepositoryConfigurationAction(projectId));
		if (task == null) {
			addAction(new GetTaskAction(projectId, taskId));
		}
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		repositoryConfiguration = getResult(GetRepositoryConfigurationResult.class).get();
		if (task == null) {
			task = getResult(GetTaskResult.class).get();
		}
		createBreadcrumbs(project, task);
		onPlaceDataFetched();
	}

	private void createBreadcrumbs(Project project, Task task) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(ProjectTasksPlace.createDefaultPlace(project.getIdentifier()).getHistoryToken(),
				"Tasks"));
		breadcrumbs.add(new Breadcrumb(getHistoryToken(), task.getTaskType() + " #" + task.getId()));
	}

}
