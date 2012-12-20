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

	@DefaultMessage("You cannot disable your own account.")
	String cannotDisableOwnAccount();

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

	@DefaultMessage("Creating project...")
	String creatingProject();

	@DefaultMessage("Dashboard")
	String dashboard();

	@DefaultMessage("Day")
	String day();

	@DefaultMessage("Deleted.")
	String deleted();

	@DefaultMessage("Deleting...")
	String deleting();

	@DefaultMessage("Deleting component...")
	String deletingComponent();

	@DefaultMessage("Deleting product...")
	String deletingProduct();

	@DefaultMessage("Deleting project...")
	String deletingProject();

	@DefaultMessage("Deleting release...")
	String deletingRelease();

	@DefaultMessage("Deployments")
	String deployments();

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

	@DefaultMessage("Please enter an email")
	String enterEmail();

	@DefaultMessage("Please enter a valid email")
	String enterValidEmail();

	@DefaultMessage("Error while saving")
	String errorWhileSaving();

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

	@DefaultMessage("Invitation accepted")
	String invitationAccepted();

	@DefaultMessage("Invitation Creator")
	String invitationCreator();

	@DefaultMessage("Invitations created.")
	String invitationsCreated();

	@DefaultMessage("Invitations created. See below for details:")
	String invitationsCreatedSeeBelow();

	@DefaultMessage("Invitation sent to")
	String invitationSentTo();

	@DefaultMessage("Invitation to join {0}")
	String invitationToJoin(String projectName);

	@DefaultMessage("Invitation token is not valid.")
	String invitationTokenNotValid();

	@DefaultMessage("First Name, Last Name, Email Address, Token, URL")
	String invitationTokens();

	@DefaultMessage("Member")
	String member();

	@DefaultMessage("Name")
	String name();

	@DefaultMessage("Open Tasks")
	String openTasks();

	@DefaultMessage("Organization Shared")
	String organizationShared();

	@DefaultMessage("Organization updated")
	String organizationUpdated();

	@DefaultMessage("Owner")
	String owner();

	@DefaultMessage("Password Reset")
	String passwordReset();

	@DefaultMessage("Password and Confirm Password must be the same.")
	String passwordAndConfirmationMustMatch();

	@DefaultMessage("Password reset instructions have been sent to your email.")
	String passwordResetInstructionsSent();

	@DefaultMessage("Your password has been updated. You are now signed in.")
	String passwordUpdatedSignedIn();

	@DefaultMessage("Product created.")
	String productCreated();

	@DefaultMessage("Product deleted.")
	String productDeleted();

	@DefaultMessage("Product saved.")
	String productSaved();

	@DefaultMessage("Profile Disabled")
	String profileDisabled();

	@DefaultMessage("Profile Enabled")
	String profileEnabled();

	@DefaultMessage("Your profile could not be found. Please try again.")
	String profileNotFound();

	@DefaultMessage("projects")
	String projectBaseUrl();

	@DefaultMessage("Project created! Provisioning project services. This will take a minute.")
	String projectCreatedAndProvisioning();

	@DefaultMessage("Project deleted.")
	String projectDeleted();

	@DefaultMessage("Project saved.")
	String projectSaved();

	@DefaultMessage("Properties")
	String properties();

	@DefaultMessage("Public")
	String public_();

	@DefaultMessage("Release deleted.")
	String releaseDeleted();

	@DefaultMessage("Restarting...")
	String restarting();

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

	@DefaultMessage("Sign Up")
	String signUp();

	@DefaultMessage("Started.")
	String started();

	@DefaultMessage("Starting...")
	String starting();

	@DefaultMessage("Status")
	String status();

	@DefaultMessage("Stopped.")
	String stopped();

	@DefaultMessage("Stopping...")
	String stopping();

	@DefaultMessage("Team")
	String team();

	@DefaultMessage("Team member updated.")
	String teamMemberUpdated();

	@DefaultMessage("Token required for sign up.")
	String tokenRequiredForSignUp();

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
