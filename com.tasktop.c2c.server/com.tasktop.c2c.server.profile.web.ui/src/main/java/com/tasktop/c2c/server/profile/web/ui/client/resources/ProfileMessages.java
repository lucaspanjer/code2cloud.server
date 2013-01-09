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
import com.google.gwt.safehtml.shared.SafeHtml;

@GenerateKeys("com.google.gwt.i18n.server.keygen.MethodNameKeyGenerator")
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface ProfileMessages extends Messages {

	@DefaultMessage("About Project")
	String aboutProject();

	@DefaultMessage("Accept Invitation")
	String acceptInvitation();

	@DefaultMessage("Account")
	String account();

	@DefaultMessage("Account Information")
	String accountInformation();

	@DefaultMessage("Active")
	String active();

	@DefaultMessage("Activity (60 Days)")
	String activityRecent();

	@DefaultMessage("Add Key")
	String addKey();

	@DefaultMessage("Add SSH Key")
	String addSshKey();

	@DefaultMessage("Administer Users")
	String administerUsers();

	@DefaultMessage("Administration")
	String administration();

	@DefaultMessage("Agree")
	String agree();

	@DefaultMessage("All")
	String all();

	@DefaultMessage("Authentication")
	String authentication();

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

	@DefaultMessage("Build Status")
	String buildStatus();

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

	@DefaultMessage("Commited")
	String commited();

	@DefaultMessage("Commits")
	String commits();

	@DefaultMessage("Commits By Author (60 Days)")
	String commitsByAuthorRecent();

	@DefaultMessage("To complete Sign Up, enter account information in the form to the left to create your {0} account.")
	String completeSignUp(String code2Cloud);

	@DefaultMessage("Component deleted.")
	String componentDeleted();

	@DefaultMessage("Confirm Leave This Project")
	String confirmLeaveProject();

	@DefaultMessage("Please confirm you would like to leave this project as a member. Once you leave the project, you will not be able to participate in the project any longer and will require being invited to join the project.")
	String confirmLeaveProjectMsg();

	@DefaultMessage("Create")
	String create();

	@DefaultMessage("Create Account")
	String createAccount();

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

	@DefaultMessage("Delete SSH Key")
	String deleteSshKey();

	@DefaultMessage("Are you sure you want to remove this SSH key?")
	String deleteSshKeyConfirm();

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

	@DefaultMessage("Details")
	String details();

	@DefaultMessage("Disable")
	String disable();

	@DefaultMessage("Disabled")
	String disabled();

	@DefaultMessage("Edit")
	String edit();

	@DefaultMessage("Editing")
	String editing();

	@DefaultMessage("Edit Organization")
	String editOrganization();

	@DefaultMessage("Edit SSH Key")
	String editSshKey();

	@DefaultMessage("Email")
	String email();

	@DefaultMessage("Email Address")
	String emailAddress();

	@DefaultMessage("Email notifications will be sent to <span style=\"font-weight:bold\">{0}</span>.")
	SafeHtml emailNotificationsSentTo(String email);

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

	@DefaultMessage("First Name")
	String firstName();

	@DefaultMessage("First, Last, email@example.com")
	String firstLastEmailExample();

	@DefaultMessage("Enter your email address and your login information will be sent to you.")
	String forgotPasswordEnterEmail();

	@DefaultMessage("Forgot your username or password?")
	String forgotUsernameOrPassword();

	@DefaultMessage("Format (one per line)")
	String formatOnePerLine();

	@DefaultMessage("Full Email List")
	String fullEmailList();

	@DefaultMessage("GitHub")
	String gitHub();

	@DefaultMessage("GitHub Account \"{0}\" Successfully Linked")
	String gitHubAccountLinkedSuccessfully(String username);

	@DefaultMessage("Associate your <a href=\"http://www.github.com\" target=\"_blank\" class=\"underline\">GitHub</a> account with your {0} account.")
	SafeHtml gitHubAssociateAccount(String code2Cloud);

	@DefaultMessage("GitHub Credentials")
	String gitHubCredentials();

	@DefaultMessage("You can link your <a href=\"http://www.github.com\" class=\"underline\">GitHub</a> account to your {0} account. "
			+ "After linking your accounts, you can sign in to {0} using either your {0} credentials or your GitHub credentials.")
	SafeHtml gitHubDetailsContent(String code2Cloud);

	@DefaultMessage("You can link your <a href=\"http://www.github.com/\" target=\"_blank\">GitHub</a> account and sign in to {0} using your GitHub credentials.")
	SafeHtml gitHubSignInDescription(String code2Cloud);

	@DefaultMessage("Go To Wiki Page")
	String goToWikiPage();

	@DefaultMessage("Help")
	String help();

	@DefaultMessage("Hide")
	String hide();

	@DefaultMessage("Home")
	String home();

	@DefaultMessage("Opted Out of News Emails")
	String hideNewsEmailOptOut();

	@DefaultMessage("Opted Out of Service Emails")
	String hideServiceEmailOptOut();

	@DefaultMessage("Unverified Email Addresses")
	String hideUnverified();

	@DefaultMessage("Image from <a href=\"http://www.gravatar.com\" target=\"_blank\" class=\"underline\">gravatar.com</a>")
	SafeHtml imageFromGravatar();

	@DefaultMessage("Invalid username or password. Please try again.")
	String invalidUsername();

	@DefaultMessage("Invitation accepted")
	String invitationAccepted();

	@DefaultMessage("Invitation Creator")
	String invitationCreator();

	@DefaultMessage("{0} is currently invitation only.")
	String invitationOnly(String code2Cloud);

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

	@DefaultMessage("Key")
	String key();

	@DefaultMessage("Last Name")
	String lastName();

	@DefaultMessage("Latest Activity")
	String latestActivity();

	@DefaultMessage("Leave this Project")
	String leaveProject();

	@DefaultMessage("Left project {0}")
	String leftProject(String projectName);

	@DefaultMessage("Link Your GitHub Account")
	String linkYourGitHubAccount();

	@DefaultMessage("Manage Members")
	String manageMembers();

	@DefaultMessage("Maven")
	String maven();

	@DefaultMessage("To deploy Maven artifacts to this repository, use:")
	String mavenDeployLocation();

	@DefaultMessage("The Maven repository for this project is located at:")
	String mavenRepositoryLocation();

	@DefaultMessage("Member")
	String member();

	@DefaultMessage("{0} minutes")
	String minutes(Long duration);

	@DefaultMessage("Name")
	String name();

	@DefaultMessage("Notifications")
	String notifications();

	@DefaultMessage("New features, tips, and events")
	String notificationsNewFeaturesTipsEvents();

	@DefaultMessage("Service and system maintenance updates")
	String notificationsServiceMaintenanceUpdates();

	@DefaultMessage("Task updates, attachments and comments")
	String notificationsTaskUpdatesAttachmentsComments();

	@DefaultMessage("Now Create Your {0} Account")
	String nowCreateYourAccount(String code2Cloud);

	@DefaultMessage("OK")
	String ok();

	@DefaultMessage("Open Tasks")
	String openTasks();

	@DefaultMessage("Options")
	String options();

	@DefaultMessage("Organization Shared")
	String organizationShared();

	@DefaultMessage("Organization Updated")
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

	@DefaultMessage("Change your Password")
	String passwordChange();

	@DefaultMessage("Confirm Password")
	String passwordConfirm();

	@DefaultMessage("The current password you gave is incorrect.")
	String passwordIncorrect();

	@DefaultMessage("New Password")
	String passwordNew();

	@DefaultMessage("Old Password")
	String passwordOld();

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

	@DefaultMessage("New Password and Confirm Password must be the same.")
	String passwordNewAndConfirmationMustMatch();

	@DefaultMessage("Please provide a password.")
	String passwordProvide();

	@DefaultMessage("Please choose a new password.")
	String passwordResetNewPassword();

	@DefaultMessage("Password reset instructions have been sent to your email.")
	String passwordResetInstructionsSent();

	@DefaultMessage("Password updated.")
	String passwordUpdated();

	@DefaultMessage("Your password has been updated. You are now signed in.")
	String passwordUpdatedSignedIn();

	@DefaultMessage("Picture")
	String picture();

	@DefaultMessage("Product created.")
	String productCreated();

	@DefaultMessage("Product deleted.")
	String productDeleted();

	@DefaultMessage("Product saved.")
	String productSaved();

	@DefaultMessage("Profile")
	String profile();

	@DefaultMessage("Profile Disabled")
	String profileDisabled();

	@DefaultMessage("Profile Enabled")
	String profileEnabled();

	@DefaultMessage("Your profile could not be found. Please try again.")
	String profileNotFound();

	@DefaultMessage("Profile updated.")
	String profileUpdated();

	@DefaultMessage("To access this project, sign up for an account or {0} with your existing account")
	SafeHtml projectAccessSignUpOrIn(SafeHtml signInAnchor);

	@DefaultMessage("projects")
	String projectBaseUrl();

	@DefaultMessage("Project created! Provisioning project services. This will take a minute.")
	String projectCreatedAndProvisioning();

	@DefaultMessage("Project deleted.")
	String projectDeleted();

	@DefaultMessage("You have been invited to the ''{0}'' project.")
	String projectInvited(String projectName);

	@DefaultMessage("Project saved.")
	String projectSaved();

	@DefaultMessage("Properties")
	String properties();

	@DefaultMessage("Public")
	String public_();

	@DefaultMessage("Public Key saved: {0} ({1})")
	String publicKeySaved(String keyName, String keyFingerprint);

	@DefaultMessage("Public Key updated: {0} ({1})")
	String publicKeyUpdated(String keyName, String keyFingerprint);

	@DefaultMessage("Read More")
	String readMore();

	@DefaultMessage("Recent Activity")
	String recentActivity();

	@DefaultMessage("Release deleted.")
	String releaseDeleted();

	@DefaultMessage("Remember Me")
	String rememberMe();

	@DefaultMessage("Remove")
	String remove();

	@DefaultMessage("Remove GitHub link for {0}")
	String removeGitHubLinkFor(String linkOwner);

	@DefaultMessage("Restarting...")
	String restarting();

	@DefaultMessage("Restarted.")
	String restarted();

	@DefaultMessage("Reviews")
	String reviews();

	@DefaultMessage("Save")
	String save();

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

	@DefaultMessage("Send Verification")
	String sendVerification();

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

	@DefaultMessage("sign in")
	String signInLc();

	@DefaultMessage("Sign in with GitHub")
	String signInWithGitHub();

	@DefaultMessage("Sign Out")
	String signOut();

	@DefaultMessage("Sign Up")
	String signUp();

	@DefaultMessage("Source")
	String source();

	@DefaultMessage("Git source code repositories are located at:")
	String sourceRepositoryLocation();

	@DefaultMessage("Sources")
	String sources();

	@DefaultMessage("SSH Keys")
	String sshKeys();

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

	@DefaultMessage("Time: {0}.")
	String timeLabel(String timeValue);

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

	@DefaultMessage("Verification email sent. Please check your email to confirm your address.")
	String verificationEmailSent();

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

	@DefaultMessage("What is Email Verification?")
	String whatIsEmailVerification();

	@DefaultMessage("<p>When you create your account or modify your account email address, we''ll send a verification link to the email address you used.</p>"
			+ "<p>Click the link in that email to verify that you own the email address.</p>"
			+ "<p>Email verification is required to access certain {0} features, such a system alerts.</p>")
	SafeHtml whatIsEmailVerificationDetails(String code2Cloud);

	@DefaultMessage("(What is Verification?)")
	String whatIsVerificationParens();

	@DefaultMessage("Wiki")
	String wiki();

	@DefaultMessage("You can add content here by creating a wiki page called \"Home\".")
	String wikiHomePage();
}
