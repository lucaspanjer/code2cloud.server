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

	@DefaultMessage("Active")
	String active();

	@DefaultMessage("Add Iteration")
	String addIteration();

	@DefaultMessage("Tasks Admin - {0} - {1}")
	String adminWindowTitle(String projectName, String productName);

	@DefaultMessage("Advanced Search")
	String advancedSearch();

	@DefaultMessage("All tasks")
	String allTasks();

	@DefaultMessage("Assigned to me")
	String assignedToMe();

	@DefaultMessage("Associations")
	String associations();

	@DefaultMessage("Attached to")
	String attachedTo();

	@DefaultMessage("Attachments")
	String attachments();

	@DefaultMessage("Attachments ({0})")
	String attachmentsHeader(int attachmentCount);

	@DefaultMessage("Available For New Tasks")
	String availableForNewTasks();

	@DefaultMessage("Back to List")
	String backToList();

	@DefaultMessage("Back to Task")
	String backToTask();

	@DefaultMessage("by")
	String byLc();

	@DefaultMessage("By Product and Release")
	String byProductAndRelease();

	@DefaultMessage("CC")
	String carbonCopy();

	@DefaultMessage("Changed")
	String changed();

	@DefaultMessage("Commented on")
	String commentedOn();

	@DefaultMessage("Commenter")
	String commenter();

	@DefaultMessage("Comment #{0}")
	String commentNumber(Integer commentNumber);

	@DefaultMessage("Comment Saved")
	String commentSaved();

	@DefaultMessage("comments")
	String commentsLc();

	@DefaultMessage("Comments <span> ({0})</span>")
	SafeHtml commentsWithTotal(Integer commentsTotal);

	@DefaultMessage("Comment text is required.")
	String commentTextRequired();

	@DefaultMessage("Commits")
	String commits();

	@DefaultMessage("Comp")
	String comp();

	@DefaultMessage("Are you sure you want to delete this component? This operation cannot be undone.")
	String componentDeleteConfirmation();

	@DefaultMessage("Component")
	String component();

	@DefaultMessage("Component Deleted.")
	String componentDeleted();

	@DefaultMessage("Components")
	String components();

	@DefaultMessage("Confirm Delete")
	String confirmDelete();

	@DefaultMessage("Created")
	String created();

	@DefaultMessage("Created attachment {0}")
	String createdAttachmentId(Integer attachmentId);

	@DefaultMessage("Created by")
	String createdBy();

	@DefaultMessage("Created by: {0}")
	String createdByReporter(String reporter);

	@DefaultMessage("Created iteration {0}.")
	String createdIteration(String iterationName);

	@DefaultMessage("Create Task")
	String createTask();

	@DefaultMessage("Creating Field...")
	String creatingField();

	@DefaultMessage("Creating Task...")
	String creatingTask();

	@DefaultMessage("Creator")
	String creator();

	@DefaultMessage("<div class=\"severity five\" title=\"Critical\">")
	SafeHtml criticalS5Div();

	@DefaultMessage("This will permanently remove the custom field and its values for all tasks. This operation cannot be undone.")
	String customFieldDeleteWarning();

	@DefaultMessage("Custom Fields")
	String customFields();

	@DefaultMessage("Date Type")
	String dateType();

	@DefaultMessage("Default Release")
	String defaultRelease();

	@DefaultMessage("Are you sure you want to delete the query: {0}? This operation cannot be undone.")
	String deleteQueryConfirmationMessage(String queryName);

	@DefaultMessage("Deleting Field...")
	String deletingField();

	@DefaultMessage("Description")
	String description();

	@DefaultMessage("Description + Comments")
	String descriptionAndComments();

	@DefaultMessage("Description Checkbox")
	String descriptionCheckbox();

	@DefaultMessage("Details")
	String details();

	@DefaultMessage("There are unsaved changes. Are you sure you want to navigate away? Press OK to navigate away and lose unsaved changes, or Cancel to stay on the current page.")
	String dirtyNavigateWarning();

	@DefaultMessage("Due Date")
	String dueDate();

	@DefaultMessage("Duplicates")
	String duplicates();

	@DefaultMessage("End Date")
	String endDate();

	@DefaultMessage("Edit Saved Search")
	String editSavedSearch();

	@DefaultMessage("Edit Task {0}")
	String editTaskWithId(String taskId);

	@DefaultMessage("Edit {0} {1} - {2} - {3} - {4}")
	String editTaskWindowTitle(String taskType, Integer taskId, String taskShortDescription, String projectName,
			String productName);

	@DefaultMessage("<div class=\"severity one\" title=\"Enhancement\">")
	SafeHtml enhancementS1Div();

	@DefaultMessage("Estimate")
	String estimate();

	@DefaultMessage("Existing")
	String existing();

	@DefaultMessage("Export")
	String export();

	@DefaultMessage("External")
	String external();

	@DefaultMessage("Field Created.")
	String fieldCreated();

	@DefaultMessage("Field Deleted.")
	String fieldDeleted();

	@DefaultMessage("Field Details")
	String fieldDetails();

	@DefaultMessage("Fields")
	String fields();

	@DefaultMessage("Checkbox")
	String fieldTypeCheckbox();

	@DefaultMessage("Long text input")
	String fieldTypeLongText();

	@DefaultMessage("Multiple selection")
	String fieldTypeMultiSelect();

	@DefaultMessage("Single selection")
	String fieldTypeSingleSelect();

	@DefaultMessage("Task reference")
	String fieldTypeTaskReference();

	@DefaultMessage("Single line text input")
	String fieldTypeText();

	@DefaultMessage("Time and date")
	String fieldTypeTimestamp();

	@DefaultMessage("Field Values")
	String fieldValues();

	@DefaultMessage("Field Updated.")
	String fieldUpdated();

	@DefaultMessage("File")
	String file();

	@DefaultMessage("Files")
	String files();

	@DefaultMessage("Found In")
	String foundIn();

	@DefaultMessage("General")
	String general();

	@DefaultMessage("Hide Inactive")
	String hideInactive();

	@DefaultMessage("<div class=\"priority four\" title=\"High\">")
	SafeHtml highP4Div();

	@DefaultMessage("<div class=\"priority five\" title=\"Highest\">")
	SafeHtml highestP5Div();

	@DefaultMessage("History")
	String history();

	@DefaultMessage("History for {0}")
	String historyFor(String task);

	@DefaultMessage("History of {0} {1} - {2} - {3}")
	String historyWindowTitle(String taskType, Integer taskId, String projectName, String productName);

	@DefaultMessage("Hours")
	String hours();

	@DefaultMessage("Illegal query type: {0}")
	String illegalQueryType(String queryType);

	@DefaultMessage("(In reply to comment #{0})")
	String inReplyToComment(Integer commentNumber);

	@DefaultMessage("Iteration")
	String iteration();

	@DefaultMessage("Iterations")
	String iterations();

	@DefaultMessage("Iteration Saved.")
	String iterationSaved();

	@DefaultMessage("Label")
	String label();

	@DefaultMessage("Logged time on")
	String loggedTimeOn();

	@DefaultMessage("<div class=\"priority two\" title=\"Low\">")
	SafeHtml lowP2Div();

	@DefaultMessage("<div class=\"priority one\" title=\"Lowest\">")
	SafeHtml lowestP1Div();

	@DefaultMessage("<div class=\"severity four\" title=\"Major\">")
	SafeHtml majorS4Div();

	@DefaultMessage("more")
	String moreLc();

	@DefaultMessage("My Searches")
	String mySearches();

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

	@DefaultMessage("New Custom Field")
	String newCustomField();

	@DefaultMessage("New Parent Task of {0}")
	String newParentTaskOf(Integer subtaskId);

	@DefaultMessage("New Product")
	String newProduct();

	@DefaultMessage("New Subtask")
	String newSubtask();

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

	@DefaultMessage("Not set")
	String notSet();

	@DefaultMessage("{0} comments")
	String numberOfComments(Integer commentsSize);

	@DefaultMessage("Obsolete (hidden)")
	String obsoleteHidden();

	@DefaultMessage("of")
	String ofLc();

	@DefaultMessage("Open tasks")
	String openTasks();

	@DefaultMessage("Owner")
	String owner();

	@DefaultMessage("Parent")
	String parent();

	@DefaultMessage("Parent Task")
	String parentTask();

	@DefaultMessage("People")
	String people();

	@DefaultMessage("Person")
	String person();

	@DefaultMessage("Post Comment")
	String postComment();

	@DefaultMessage("Pri")
	String pri();

	@DefaultMessage("Priority")
	String priority();

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

	@DefaultMessage("Product Details")
	String productDetails();

	@DefaultMessage("Product: {0}, Release: {1}")
	String productReleaseHeader(String productName, String milestone);

	@DefaultMessage("Products")
	String products();

	@DefaultMessage("Product: {0}")
	String productWithName(String productName);

	@DefaultMessage("Properties")
	String properties();

	@DefaultMessage("No query named: {0}")
	String queryNotFound(String queryString);

	@DefaultMessage("Query Saved")
	String querySaved();

	@DefaultMessage("Recently changed")
	String recentlyChanged();

	@DefaultMessage("Related to me")
	String relatedToMe();

	@DefaultMessage("Release")
	String release();

	@DefaultMessage("Releases")
	String releases();

	@DefaultMessage("Release Version")
	String releaseVersion();

	@DefaultMessage("Are you sure you want to delete this release? This operation cannot be undone.")
	String releaseDeleteConfirmation();

	@DefaultMessage("Release Deleted.")
	String releaseDeleted();

	@DefaultMessage("Reply")
	String reply();

	@DefaultMessage("Resolution")
	String resolution();

	@DefaultMessage("Save Changes")
	String saveChanges();

	@DefaultMessage("Save Query")
	String saveQuery();

	@DefaultMessage("Save or cancel current edit first.")
	String saveOrCancelCurrentEdit();

	@DefaultMessage("Saving Task...")
	String savingTask();

	@DefaultMessage("Search for")
	String searchFor();

	@DefaultMessage("Search Tasks")
	String searchTasks();

	@DefaultMessage("Sev")
	String sev();

	@DefaultMessage("Severity")
	String severity();

	@DefaultMessage("Standard Searches")
	String standardSearches();

	@DefaultMessage("Start Date")
	String startDate();

	@DefaultMessage("Subtask")
	String subtask();

	@DefaultMessage("Subtasks")
	String subtasks();

	@DefaultMessage("Subtasks: {0}")
	String subtasksTime(String subtasksTime);

	@DefaultMessage("Summary")
	String summary();

	@DefaultMessage("Summary Checkbox")
	String summaryCheckbox();

	@DefaultMessage("Tag")
	String tag();

	@DefaultMessage("Are you sure you want to delete this tag? This operation cannot be undone.")
	String tagDeleteConfirmation();

	@DefaultMessage("Tag Deleted.")
	String tagDeleted();

	@DefaultMessage("Tag Details")
	String tagDetails();

	@DefaultMessage("Tags")
	String tags();

	@DefaultMessage("Tag Saved.")
	String tagSaved();

	@DefaultMessage("Task Created.")
	String taskCreated();

	@DefaultMessage("Task with id \"{0}\" not found.")
	String taskNotFound(Integer taskId);

	@DefaultMessage("Task Saved.")
	String taskSaved();

	@DefaultMessage("Task Summary")
	String taskSummary();

	@DefaultMessage("{0} ({1} tasks in total: {2} open, {3} closed)")
	String taskSummaryHeader(String linkText, Integer taskTotal, Integer numOpen, Integer numClosed);

	@DefaultMessage("Task has been updated. Review changes and try again.")
	String taskUpdatedMessage();

	@DefaultMessage("Task {0}")
	String taskWithId(Integer taskId);

	@DefaultMessage("Task {0}: {1}")
	String taskWithIdAndShortDescription(Integer taskId, String taskShortDescription);

	@DefaultMessage("{0} {1} - {2} - {3} - {4}")
	String taskWindowTitle(String taskType, Integer taskId, String taskShortDescription, String projectName,
			String productName);

	@DefaultMessage("Time")
	String time();

	@DefaultMessage("Total: {0} <br> This: {1} <br> Subtasks: {2}")
	SafeHtml timeBreakdown(String totalTime, String thisTaskTime, String subTaskTime);

	@DefaultMessage("Time: {0}.")
	String timeLabel(String timeValue);

	@DefaultMessage("Time Spent")
	String timeSpent();

	@DefaultMessage("Total")
	String total();

	@DefaultMessage("<div class=\"severity two\" title=\"Trivial\">")
	SafeHtml trivialS2Div();

	@DefaultMessage("View History")
	String viewHistory();

	@DefaultMessage("Error: Unexpected server response")
	String unexpectedServerResponse();

	@DefaultMessage("Unknown sort")
	String unknownSort();

	@DefaultMessage("Updated")
	String updated();

	@DefaultMessage("Updated on {0}")
	String updatedOnDate(String updateDate);

	@DefaultMessage("Updating Field...")
	String updatingField();

	@DefaultMessage("Upload")
	String upload();

	@DefaultMessage("Workflow")
	String workflow();
}
