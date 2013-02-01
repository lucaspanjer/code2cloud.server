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
package com.tasktop.c2c.server.common.profile.web.client;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@GenerateKeys("com.google.gwt.i18n.server.keygen.MethodNameKeyGenerator")
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface CommonProfileMessages extends Messages {

	@DefaultMessage("Your account has been disabled")
	String accountDisabled();

	@DefaultMessage("Aborted")
	String aborted();

	@DefaultMessage("Admin")
	String admin();

	@DefaultMessage("Administration")
	String administration();

	@DefaultMessage("Browse Projects")
	String browseProjects();

	@DefaultMessage("Build")
	String build();

	@DefaultMessage("Builds")
	String builds();

	@DefaultMessage("Cancel")
	String cancel();

	@DefaultMessage("Code2Cloud")
	String code2Cloud();

	@DefaultMessage(":")
	String colon();

	@DefaultMessage(",")
	String comma();

	@DefaultMessage("Dashboard")
	String dashboard();

	@DefaultMessage("Date")
	String date();

	@DefaultMessage("Deployments")
	String deployments();

	@DefaultMessage("Details")
	String details();

	@DefaultMessage("Discover Projects")
	String discoverProjects();

	@DefaultMessage("{0}!")
	String exclamationMark(String text);

	@DefaultMessage("({0})")
	String parentheses(String innerText);

	@DefaultMessage("{0}?")
	String questionMark(String text);

	@DefaultMessage("Create")
	String create();

	@DefaultMessage("Delete")
	String delete();

	@DefaultMessage("Deleted.")
	String deleted();

	@DefaultMessage("Deleting...")
	String deleting();

	@DefaultMessage("Disable")
	String disable();

	@DefaultMessage("Disabled")
	String disabled();

	@DefaultMessage("Edit")
	String edit();

	@DefaultMessage("Editing")
	String editing();

	@DefaultMessage("...")
	String ellipsis();

	@DefaultMessage("Enable")
	String enable();

	@DefaultMessage("Enabled")
	String enabled();

	@DefaultMessage("End")
	String end();

	@DefaultMessage("Error")
	String error();

	@DefaultMessage("Failure")
	String failure();

	@DefaultMessage("Help")
	String help();

	@DefaultMessage("Hide")
	String hide();

	@DefaultMessage("Home")
	String home();

	@DefaultMessage("ID")
	String id();

	@DefaultMessage("Legal Agreements")
	String legalAgreements();

	@DefaultMessage("Member")
	String member();

	@DefaultMessage("Name")
	String name();

	@DefaultMessage("New")
	String _new();

	@DefaultMessage("No")
	String no();

	@DefaultMessage("None")
	String none();

	@DefaultMessage("You do not have permissions to visit the page")
	String noPermissionsToVisitPage();

	@DefaultMessage("Not built yet")
	String notBuiltYet();

	@DefaultMessage("OK")
	String ok();

	@DefaultMessage("Options")
	String options();

	@DefaultMessage("Organization \"{0}\" not found.")
	String organizationNotFound(String organizationId);

	@DefaultMessage("Other")
	String other();

	@DefaultMessage("Owner")
	String owner();

	@DefaultMessage("Want to participate in a project you see, or create your own public or private project? "
			+ "Code2Cloud is currently in beta and is accepting requests to join. "
			+ "<em>Just send your first name, last name, and email address to <a href=\"mailto:engineering-c2c@tasktop.com\">engineering-c2c@tasktop.com</a>.</em> ")
	SafeHtml participateInstructions();

	@DefaultMessage("Project Admin")
	String projectAdmin();

	@DefaultMessage("Project \"{0}\" not found.")
	String projectNotFound(String projectId);

	@DefaultMessage("Projects for {0}")
	String projectsForOrganization(String organizationName);

	@DefaultMessage("Remove")
	String remove();

	@DefaultMessage("Restart")
	String restart();

	@DefaultMessage("Restarting...")
	String restarting();

	@DefaultMessage("Restarted.")
	String restarted();

	@DefaultMessage("Save")
	String save();

	@DefaultMessage("Saved.")
	String saved();

	@DefaultMessage("Saving...")
	String saving();

	@DefaultMessage("Search")
	String search();

	@DefaultMessage("A server side error occurred")
	String serverSideErrorOccured();

	@DefaultMessage("Settings")
	String settings();

	@DefaultMessage("Note: Setting a project as \"Public\" makes the project code, wiki docs, tasks and builds publicly available")
	SafeHtml settingsProjectHelp();

	@DefaultMessage("Show")
	String show();

	@DefaultMessage("Sign In")
	String signIn();

	@DefaultMessage("Source")
	String source();

	@DefaultMessage("Start")
	String start();

	@DefaultMessage("Started")
	String started();

	@DefaultMessage("Starting...")
	String starting();

	@DefaultMessage("Status")
	String status();

	@DefaultMessage("Stop")
	String stop();

	@DefaultMessage("Stopped")
	String stopped();

	@DefaultMessage("Stopping...")
	String stopping();

	@DefaultMessage("Success")
	String success();

	@DefaultMessage("Tasks")
	String tasks();

	@DefaultMessage("Team")
	String team();

	@DefaultMessage("Type")
	String type();

	@DefaultMessage("Undo")
	String undo();

	@DefaultMessage("Error: Unexpected server response")
	String unexpectedServerResponse();

	@DefaultMessage("Unstable")
	String unstable();

	@DefaultMessage("Updating")
	String updating();

	@DefaultMessage("URL")
	String url();

	@DefaultMessage("Validate")
	String validate();

	@DefaultMessage("Code2Cloud is an online service for <em>creating, hosting, and deploying software development projects</em>. In just a "
			+ "few clicks, you can create your own project ready to store <em>source code</em> (Git), "
			+ "track <em>tasks and issues</em>, create <em>wiki documentation</em>, "
			+ "set up <em>continuous integration</em> (Hudson), and <em>deploy</em> to a local server "
			+ "or your <em>Cloud Foundry</em> instance. All of these tools come <em>pre-integrated</em> to work together, so you don''t "
			+ "have to build or maintain the tools you''re using to build the world''s next greatest application.")
	SafeHtml welcomeOverviewHtml();

	@DefaultMessage("Wiki")
	String wiki();

	@DefaultMessage("Yes")
	String yes();

}
