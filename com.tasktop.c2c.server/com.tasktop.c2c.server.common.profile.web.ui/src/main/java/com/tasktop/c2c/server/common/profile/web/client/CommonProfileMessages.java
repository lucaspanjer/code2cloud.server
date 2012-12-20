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

	@DefaultMessage("Admin")
	String admin();

	@DefaultMessage("Browse Projects")
	String browseProjects();

	@DefaultMessage("Builds")
	String builds();

	@DefaultMessage("Code2Cloud")
	String code2Cloud();

	@DefaultMessage("Dashboard")
	String dashboard();

	@DefaultMessage("Deployments")
	String deployments();

	@DefaultMessage("Discover Projects")
	String discoverProjects();

	@DefaultMessage("Home")
	String home();

	@DefaultMessage("Legal Agreements")
	String legalAgreements();

	@DefaultMessage("You do not have permissions to visit the page")
	String noPermissionsToVisitPage();

	@DefaultMessage("Organization \"{0}\" not found.")
	String organizationNotFound(String organizationId);

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

	@DefaultMessage("A server side error occurred")
	String serverSideErrorOccured();

	@DefaultMessage("Note: Setting a project as \"Public\" makes the project code, wiki docs, tasks and builds publicly available")
	SafeHtml settingsProjectHelp();

	@DefaultMessage("Sign In")
	String signIn();

	@DefaultMessage("Tasks")
	String tasks();

	@DefaultMessage("Team")
	String team();

	@DefaultMessage("Code2Cloud is an online service for <em>creating, hosting, and deploying software development projects</em>. In just a "
			+ "few clicks, you can create your own project ready to store <em>source code</em> (Git), "
			+ "track <em>tasks and issues</em>, create <em>wiki documentation</em>, "
			+ "set up <em>continuous integration</em> (Hudson), and <em>deploy</em> to a local server "
			+ "or your <em>Cloud Foundry</em> instance. All of these tools come <em>pre-integrated</em> to work together, so you don''t "
			+ "have to build or maintain the tools you''re using to build the world''s next greatest application.")
	SafeHtml welcomeOverviewHtml();

	@DefaultMessage("Wiki")
	String wiki();
}
