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
package com.tasktop.c2c.server.tasks.client;

import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMappings;
import com.tasktop.c2c.server.tasks.client.place.ProjectAdminCustomFieldsPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectAdminIterationsPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectAdminKeywordsPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectAdminProductsPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectAdminTasksPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectEditTaskPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectNewTaskPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectTaskHistoryPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectTaskPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectTasksPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectTasksSummaryListPlace;
import com.tasktop.c2c.server.tasks.client.place.ProjectTasksSummaryPlace;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class TaskPageMappings extends PageMappings {

	public TaskPageMappings() {
		super(ProjectAdminTasksPlace.ProjectTaskAdmin, ProjectTaskPlace.ProjectTask,
				ProjectTaskHistoryPlace.ProjectTaskHistory, ProjectEditTaskPlace.ProjectEditTask,
				ProjectAdminKeywordsPlace.ProjectTaskAdminKeywords, ProjectAdminProductsPlace.ProjectTaskAdminProducts,
				ProjectAdminIterationsPlace.ProjectTaskAdminIterations,
				ProjectAdminCustomFieldsPlace.ProjectTaskAdminCustomFields, ProjectNewTaskPlace.ProjectNewTask,
				ProjectTasksPlace.ProjectTasks, ProjectTasksSummaryListPlace.ProjectTaskSummaryList,
				ProjectTasksSummaryPlace.ProjectTaskSummary);
	}

}
