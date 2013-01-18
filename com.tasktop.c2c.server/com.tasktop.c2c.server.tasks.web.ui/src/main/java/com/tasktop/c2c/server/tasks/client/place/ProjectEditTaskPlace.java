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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HasProjectPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.client.place.SectionPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.tasks.client.TasksMessages;
import com.tasktop.c2c.server.tasks.domain.RepositoryConfiguration;
import com.tasktop.c2c.server.tasks.domain.Task;
import com.tasktop.c2c.server.tasks.shared.action.GetRepositoryConfigurationAction;
import com.tasktop.c2c.server.tasks.shared.action.GetRepositoryConfigurationResult;
import com.tasktop.c2c.server.tasks.shared.action.GetTaskAction;
import com.tasktop.c2c.server.tasks.shared.action.GetTaskResult;

public class ProjectEditTaskPlace extends AbstractProjectTaskBatchingPlace implements HeadingPlace, HasProjectPlace,
		BreadcrumbPlace, SectionPlace, WindowTitlePlace {

	private static final String TASK = "taskId";
	public static PageMapping ProjectEditTask = new PageMapping(new ProjectEditTaskPlace.Tokenizer(), Path.PROJECT_BASE
			+ "/{" + Path.PROJECT_ID + "}/task/{" + TASK + ":Integer}/edit");

	private static class Tokenizer extends AbstractPlaceTokenizer<ProjectEditTaskPlace> {

		@Override
		public ProjectEditTaskPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID), pathArgs.getInteger(TASK));
		}

	}

	private Integer taskId;
	private RepositoryConfiguration repositoryConfiguration;
	private Task task;
	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
	private CommonProfileMessages commonProfileMessages = GWT.create(CommonProfileMessages.class);
	private TasksMessages tasksMessages = GWT.create(TasksMessages.class);

	protected ProjectEditTaskPlace(String projectId, Integer taskId) {
		super(projectId);
		this.taskId = taskId;
	}

	public static ProjectEditTaskPlace createPlace(String projectId, Integer taskId) {
		return new ProjectEditTaskPlace(projectId, taskId);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectId);
		tokenMap.put(TASK, String.valueOf(taskId));

		return ProjectEditTask.getUrlForNamedArgs(tokenMap);
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetRepositoryConfigurationAction(projectId));
		addAction(new GetTaskAction(projectId, taskId));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		repositoryConfiguration = getResult(GetRepositoryConfigurationResult.class).get();
		task = getResult(GetTaskResult.class).get();
		createBreadcrumbs(project, task);
		onPlaceDataFetched();
	}

	@Override
	public Project getProject() {
		return project;
	}

	public RepositoryConfiguration getRepositoryConfiguration() {
		return repositoryConfiguration;
	}

	public Task getTask() {
		return task;
	}

	@Override
	public String getHeading() {
		if (project == null) {
			throw new IllegalStateException(tasksMessages.placeDataNotAvailable(this.getClass().getName()));
		}
		return project.getName();
	}

	@Override
	public String getWindowTitle() {
		return tasksMessages.editTaskWindowTitle(task.getTaskType(), task.getId(), task.getShortDescription(),
				project.getName(), WindowTitleBuilder.PRODUCT_NAME);
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	public Section getSection() {
		return Section.TASKS;
	}

	private void createBreadcrumbs(Project project, Task task) {
		breadcrumbs = Breadcrumb.getProjectSpecficBreadcrumbs(project);
		breadcrumbs.add(new Breadcrumb(ProjectTasksPlace.createDefaultPlace(project.getIdentifier()).getHref(),
				commonProfileMessages.tasks()));
		breadcrumbs.add(new Breadcrumb(ProjectTaskPlace.createPlace(project.getIdentifier(), task.getId()).getHref(),
				task.getTaskType() + " #" + task.getId()));
		breadcrumbs.add(new Breadcrumb(getHref(), commonProfileMessages.edit()));
	}
}
