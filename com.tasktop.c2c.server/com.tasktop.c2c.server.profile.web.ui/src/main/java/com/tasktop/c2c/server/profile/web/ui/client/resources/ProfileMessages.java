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
package com.tasktop.c2c.server.profile.web.ui.client.resources;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@GenerateKeys("com.google.gwt.i18n.server.keygen.MethodNameKeyGenerator")
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface ProfileMessages extends Messages {

	@DefaultMessage("Administer Users")
	String administerUsers();

	@DefaultMessage("All")
	String all();

	@DefaultMessage("Author")
	String author();

	@DefaultMessage("Client side error")
	String clientSideError();

	@DefaultMessage("A client-side error has occurred")
	String clientSideErrorOccurred();

	@DefaultMessage("Closed Tasks")
	String closedTasks();

	@DefaultMessage("Commits")
	String commits();

	@DefaultMessage("Component deleted.")
	String componentDeleted();

	@DefaultMessage("Create Project")
	String createProject();

	@DefaultMessage("Creating invitations...")
	String creatingInvitations();

	@DefaultMessage("Creating product...")
	String creatingProduct();

	@DefaultMessage("Day")
	String day();

	@DefaultMessage("Deleted.")
	String deleted();

	@DefaultMessage("Deleting component...")
	String deletingComponent();

	@DefaultMessage("Deleting product...")
	String deletingProduct();

	@DefaultMessage("Deleting release...")
	String deletingRelease();

	@DefaultMessage("Edit")
	String edit();

	@DefaultMessage("Edit Organization")
	String editOrganization();

	@DefaultMessage("Email")
	String email();

	@DefaultMessage("Your email address has been verified.")
	String emailVerified();

	@DefaultMessage("The referenced entity cannot be found or no longer exists.")
	String entityNotFound();

	@DefaultMessage("Full Email List")
	String fullEmailList();

	@DefaultMessage("Help")
	String help();

	@DefaultMessage("Hide")
	String hide();

	@DefaultMessage("Disabled")
	String hideDisabled();

	@DefaultMessage("Opted Out of News Emails")
	String hideNewsEmailOptOut();

	@DefaultMessage("Opted Out of Service Emails")
	String hideServiceEmailOptOut();

	@DefaultMessage("Unverified Email Addresses")
	String hideUnverified();

	@DefaultMessage("Invalid username or password. Please try again.")
	String invalidUsername();

	@DefaultMessage("Invitation Creator")
	String invitationCreator();

	@DefaultMessage("Invitations created.")
	String invitationsCreated();

	@DefaultMessage("Invitation sent to")
	String invitationSentTo();

	@DefaultMessage("Member")
	String member();

	@DefaultMessage("Name")
	String name();

	@DefaultMessage("Open Tasks")
	String openTasks();

	@DefaultMessage("Organization Shared")
	String organizationShared();

	@DefaultMessage("Owner")
	String owner();

	@DefaultMessage("Product created.")
	String productCreated();

	@DefaultMessage("Product deleted.")
	String productDeleted();

	@DefaultMessage("Product saved.")
	String productSaved();

	@DefaultMessage("projects")
	String projectBaseUrl();

	@DefaultMessage("Project saved.")
	String projectSaved();

	@DefaultMessage("Properties")
	String properties();

	@DefaultMessage("Public")
	String public_();

	@DefaultMessage("Release deleted.")
	String releaseDeleted();

	@DefaultMessage("Restarted.")
	String restarted();

	@DefaultMessage("Saved.")
	String saved();

	@DefaultMessage("Saving...")
	String saving();

	@DefaultMessage("Saving product...")
	String savingProduct();

	@DefaultMessage("Saving project...")
	String savingProject();

	@DefaultMessage("Sending invitation.")
	String sendingInvitation();

	@DefaultMessage("Settings")
	String settings();

	@DefaultMessage("Severity")
	String severity();

	@DefaultMessage("Signed out")
	String signedOut();

	@DefaultMessage("Started.")
	String started();

	@DefaultMessage("Status")
	String status();

	@DefaultMessage("Stopped.")
	String stopped();

	@DefaultMessage("Team member updated.")
	String teamMemberUpdated();

	@DefaultMessage("Total")
	String total();

	@DefaultMessage("Unexpected error")
	String unexpectedError();

	@DefaultMessage("Unknown activity type")
	String unknownActivityType();

	@DefaultMessage("Updating team member...")
	String updatingTeamMember();

	@DefaultMessage("Username")
	String username();

	@DefaultMessage("Watcher")
	String watcher();

	@DefaultMessage("Welcome To")
	String welcomeTo();
}
