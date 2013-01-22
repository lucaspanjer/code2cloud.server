/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
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

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

@GenerateKeys("com.google.gwt.i18n.server.keygen.MethodNameKeyGenerator")
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface TasksMessages extends Messages {

	@DefaultMessage("Advanced Search")
	String advancedSearch();

	@DefaultMessage("Tasks Admin - {0} - {1}")
	String adminWindowTitle(String projectName, String productName);

	@DefaultMessage("Attachments ({0})")
	String attachmentsHeader(int attachmentCount);

	@DefaultMessage("By Product and Release")
	String byProductAndRelease();

	@DefaultMessage("Comment Saved")
	String commentSaved();

	@DefaultMessage("Comments <span> ({0})</span>")
	SafeHtml commentsWithTotal(Integer commentsTotal);

	@DefaultMessage("Comment text is required.")
	String commentTextRequired();

	@DefaultMessage("Are you sure you want to delete this component? This operation cannot be undone.")
	String componentDeleteConfirmation();

	@DefaultMessage("Component")
	String component();

	@DefaultMessage("Component Deleted.")
	String componentDeleted();

	@DefaultMessage("Confirm Delete")
	String confirmDelete();

	@DefaultMessage("Created iteration {0}.")
	String createdIteration(String iterationName);

	@DefaultMessage("Creating Field...")
	String creatingField();

	@DefaultMessage("Creating Task...")
	String creatingTask();

	@DefaultMessage("<div class=\"severity five\" title=\"Critical\">")
	SafeHtml criticalS5Div();

	@DefaultMessage("Deleting Field...")
	String deletingField();

	@DefaultMessage("There are unsaved changes. Are you sure you want to navigate away? Press OK to navigate away and lose unsaved changes, or Cancel to stay on the current page.")
	String dirtyNavigateWarning();

	@DefaultMessage("Due Date")
	String dueDate();

	@DefaultMessage("Edit Task {0}")
	String editTaskWithId(String taskId);

	@DefaultMessage("Edit {0} {1} - {2} - {3} - {4}")
	String editTaskWindowTitle(String taskType, Integer taskId, String taskShortDescription, String projectName,
			String productName);

	@DefaultMessage("<div class=\"severity one\" title=\"Enhancement\">")
	SafeHtml enhancementS1Div();

	@DefaultMessage("Estimate")
	String estimate();

	@DefaultMessage("Field Created.")
	String fieldCreated();

	@DefaultMessage("Field Deleted.")
	String fieldDeleted();

	@DefaultMessage("Field Updated.")
	String fieldUpdated();

	@DefaultMessage("Found In")
	String foundIn();

	@DefaultMessage("<div class=\"priority four\" title=\"High\">")
	SafeHtml highP4Div();

	@DefaultMessage("<div class=\"priority five\" title=\"Highest\">")
	SafeHtml highestP5Div();

	@DefaultMessage("History")
	String history();

	@DefaultMessage("History of {0} {1} - {2} - {3}")
	String historyWindowTitle(String taskType, Integer taskId, String projectName, String productName);

	@DefaultMessage("Illegal query type: {0}")
	String illegalQueryType(String queryType);

	@DefaultMessage("Iteration")
	String iteration();

	@DefaultMessage("Iteration Saved.")
	String iterationSaved();

	@DefaultMessage("<div class=\"priority two\" title=\"Low\">")
	SafeHtml lowP2Div();

	@DefaultMessage("<div class=\"priority one\" title=\"Lowest\">")
	SafeHtml lowestP1Div();

	@DefaultMessage("<div class=\"severity four\" title=\"Major\">")
	SafeHtml majorS4Div();

	@DefaultMessage("<div class=\"priority three\" title=\"Normal\">")
	SafeHtml normalP3Div();

	@DefaultMessage("<div class=\"severity three\" title=\"Normal\">")
	SafeHtml normalS3Div();

	@DefaultMessage("Place data for {0} isn''t available.")
	String placeDataNotAvailable(String className);

	@DefaultMessage("Post a comment...")
	String postAComment();

	@DefaultMessage("Posting Comment...")
	String postingComment();

	@DefaultMessage("Are you sure you want to delete this product? This operation cannot be undone.")
	String productDeleteConfirmation();

	@DefaultMessage("Product Deleted.")
	String productDeleted();

	@DefaultMessage("Product Saved.")
	String productSaved();

	@DefaultMessage("New Parent Task of {0}")
	String newParentTaskOf(Integer subtaskId);

	@DefaultMessage("New Product")
	String newProduct();

	@DefaultMessage("New Subtask of {0}")
	String newSubtaskOf(Integer parentTaskId);

	@DefaultMessage("New Task")
	String newTask();

	@DefaultMessage("New Task - {0}")
	String newTaskWindowTitle(String projectName);

	@DefaultMessage("No tasks found")
	String noTasksFound();

	@DefaultMessage("No task summaries found")
	String noTaskSummariesFound();

	@DefaultMessage("Product")
	String product();

	@DefaultMessage("Product {0}, by Component")
	String productByComponent(String productName);

	@DefaultMessage("Product {0}, by Release")
	String productByRelease(String productName);

	@DefaultMessage("Product: {0}, Component: {1}")
	String productComponentHeader(String productName, String componentName);

	@DefaultMessage("Product: {0}, Component: {1}, Release: {2}")
	String productComponentReleaseHeader(String productName, String componentName, String milestone);

	@DefaultMessage("Product: {0}, Release: {1}")
	String productReleaseHeader(String productName, String milestone);

	@DefaultMessage("No query named: {0}")
	String queryNotFound(String queryString);

	@DefaultMessage("Query Saved")
	String querySaved();

	@DefaultMessage("Release")
	String release();

	@DefaultMessage("Are you sure you want to delete this release? This operation cannot be undone.")
	String releaseDeleteConfirmation();

	@DefaultMessage("Release Deleted.")
	String releaseDeleted();

	@DefaultMessage("Save Query")
	String saveQuery();

	@DefaultMessage("Saving Task...")
	String savingTask();

	@DefaultMessage("Search for")
	String searchFor();

	@DefaultMessage("Are you sure you want to delete this tag? This operation cannot be undone.")
	String tagDeleteConfirmation();

	@DefaultMessage("Tag Deleted.")
	String tagDeleted();

	@DefaultMessage("Tag Saved.")
	String tagSaved();

	@DefaultMessage("Task Created.")
	String taskCreated();

	@DefaultMessage("Task Saved.")
	String taskSaved();

	@DefaultMessage("Task Summary")
	String taskSummary();

	@DefaultMessage("Task has been updated. Review changes and try again.")
	String taskUpdatedMessage();

	@DefaultMessage("{0} {1} - {2} - {3} - {4}")
	String taskWindowTitle(String taskType, Integer taskId, String taskShortDescription, String projectName,
			String productName);

	@DefaultMessage("<div class=\"severity two\" title=\"Trivial\">")
	SafeHtml trivialS2Div();

	@DefaultMessage("Error: Unexpected server response")
	String unexpectedServerResponse();

	@DefaultMessage("Unknown sort")
	String unknownSort();

	@DefaultMessage("Updating Field...")
	String updatingField();
}
