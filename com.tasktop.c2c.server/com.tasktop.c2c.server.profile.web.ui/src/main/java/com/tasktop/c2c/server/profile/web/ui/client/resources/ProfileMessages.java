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

	@DefaultMessage("Active")
	String active();

	@DefaultMessage("Administer Users")
	String administerUsers();

	@DefaultMessage("Agree")
	String agree();

	@DefaultMessage("All")
	String all();

	@DefaultMessage("Author")
	String author();

	@DefaultMessage("Build")
	String build();

	@DefaultMessage("Build {0}")
	String buildNumber(Integer number);

	@DefaultMessage("{0} Build took {1}.")
	String buildDuration(String description, String duration);

	@DefaultMessage("resulted in {0}.")
	String buildResult(String result);

	@DefaultMessage("is pending.")
	String buildResultPending();

	@DefaultMessage("Cancel")
	String cancel();

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

	@DefaultMessage("Confirm Leave This Project")
	String confirmLeaveProject();

	@DefaultMessage("Please confirm you would like to leave this project as a member. Once you leave the project, you will not be able to participate in the project any longer and will require being invited to join the project.")
	String confirmLeaveProjectMsg();

	@DefaultMessage("Create")
	String create();

	@DefaultMessage("Create Invitations")
	String createInvitations();

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

	@DefaultMessage("Disable")
	String disable();

	@DefaultMessage("Disabled")
	String disabled();

	@DefaultMessage("Edit")
	String edit();

	@DefaultMessage("Edit Organization")
	String editOrganization();

	@DefaultMessage("Email")
	String email();

	@DefaultMessage("Email Address")
	String emailAddress();

	@DefaultMessage("Your email address has been verified.")
	String emailVerified();

	@DefaultMessage("Enable")
	String enable();

	@DefaultMessage("Enabled")
	String enabled();

	@DefaultMessage("The referenced entity cannot be found or no longer exists.")
	String entityNotFound();

	@DefaultMessage("Please enter an email")
	String enterEmail();

	@DefaultMessage("Please enter a valid email")
	String enterValidEmail();

	@DefaultMessage("Error while saving")
	String errorWhileSaving();

	@DefaultMessage("First, Last, email@example.com")
	String firstLastEmailExample();

	@DefaultMessage("Enter your email address and your login information will be sent to you.")
	String forgotPasswordEnterEmail();

	@DefaultMessage("Format (one per line)")
	String formatOnePerLine();

	@DefaultMessage("Full Email List")
	String fullEmailList();

	@DefaultMessage("GitHub Account \"{0}\" Successfully Linked")
	String githubAccountLinkedSuccessfully(String username);

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

	@DefaultMessage("Invitation recipients (CSV)")
	String invitationRecipientsCsv();

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

	@DefaultMessage("Leave this Project")
	String leaveProject();

	@DefaultMessage("Left project {0}")
	String leftProject(String projectName);

	@DefaultMessage("Member")
	String member();

	@DefaultMessage("{0} minutes")
	String minutes(Long duration);

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

	@DefaultMessage("owner")
	String ownerLowercase();

	@DefaultMessage("{0} of {1}")
	String pagerOf(String current, String total);

	@DefaultMessage("{0} of over {1}")
	String pagerOfOver(String current, String totalEstimate);

	@DefaultMessage("Password")
	String password();

	@DefaultMessage("Confirm Password")
	String passwordConfirm();

	@DefaultMessage("Your password must:")
	String passwordRulesLabel();

	@DefaultMessage("have at least one digit character")
	String passwordRulesIncludeDigit();

	@DefaultMessage("have at least one lowercase character")
	String passwordRulesIncludeLc();

	@DefaultMessage("be at least 8 characters in length")
	String passwordRulesMinLength();

	@DefaultMessage("not include your username")
	String passwordRulesNotIncludeUsername();

	@DefaultMessage("Password Reset")
	String passwordReset();

	@DefaultMessage("Password and Confirm Password must be the same.")
	String passwordAndConfirmationMustMatch();

	@DefaultMessage("Please choose a new password.")
	String passwordResetNewPassword();

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

	@DefaultMessage("Recent Activity")
	String recentActivity();

	@DefaultMessage("Release deleted.")
	String releaseDeleted();

	@DefaultMessage("Restarting...")
	String restarting();

	@DefaultMessage("Restarted.")
	String restarted();

	@DefaultMessage("Saved.")
	String saved();

	@DefaultMessage("Save New Password")
	String saveNewPassword();

	@DefaultMessage("Saving...")
	String saving();

	@DefaultMessage("Saving product...")
	String savingProduct();

	@DefaultMessage("Saving project...")
	String savingProject();

	@DefaultMessage("Search")
	String search();

	@DefaultMessage("{0} seconds")
	String seconds(Long duration);

	@DefaultMessage("Sending invitation.")
	String sendingInvitation();

	@DefaultMessage("Send Invitation Emails")
	String sendInvitationEmails();

	@DefaultMessage("Settings")
	String settings();

	@DefaultMessage("Severity")
	String severity();

	@DefaultMessage("Show")
	String show();

	@DefaultMessage("Signed out")
	String signedOut();

	@DefaultMessage("Sign In")
	String signIn();

	@DefaultMessage("Sign Up")
	String signUp();

	@DefaultMessage("Source")
	String source();

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

	@DefaultMessage("Submit Request")
	String submitRequest();

	@DefaultMessage("System Invitations")
	String systemInvitations();

	@DefaultMessage("Tasks")
	String tasks();

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

	@DefaultMessage("Unwatch")
	String unwatch();

	@DefaultMessage("Unwatched project {0}")
	String unwatchedProject(String projectName);

	@DefaultMessage("Updating team member...")
	String updatingTeamMember();

	@DefaultMessage("View Project Dashboard")
	String viewProjectDashboard();

	@DefaultMessage("has invited you to collaborate on a project")
	String userHasInvitedToCollaborate();

	@DefaultMessage("Username")
	String username();

	@DefaultMessage("Watch")
	String watch();

	@DefaultMessage("Watcher")
	String watcher();

	@DefaultMessage("Watching project {0}")
	String watchingProject(String projectName);

	@DefaultMessage("Welcome To")
	String welcomeTo();

	@DefaultMessage("Wiki")
	String wiki();

	@DefaultMessage("You can add content here by creating a wiki page called \"Home\".")
	String wikiHomePage();
}
