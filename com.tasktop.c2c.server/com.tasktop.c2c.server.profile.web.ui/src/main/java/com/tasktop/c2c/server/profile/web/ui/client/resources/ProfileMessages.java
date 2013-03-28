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

	@DefaultMessage("Added user {0}.")
	String addedUser(String user);

	@DefaultMessage("Add External Repository")
	String addExternalRepository();

	@DefaultMessage("Add Hosted Repository")
	String addHostedRepository();

	@DefaultMessage("Adding repository...")
	String addingRepository();

	@DefaultMessage("Add Key")
	String addKey();

	@DefaultMessage("Add SSH Key")
	String addSshKey();

	@DefaultMessage("Administer Users")
	String administerUsers();

	@DefaultMessage("Agree")
	String agree();

	@DefaultMessage("All")
	String all();

	@DefaultMessage("Artifact")
	String artifact();

	@DefaultMessage("There is no artifact built yet.")
	String artifactNotBuilt();

	@DefaultMessage("Artifact Path")
	String artifactPath();

	@DefaultMessage("Authentication")
	String authentication();

	@DefaultMessage("Author")
	String author();

	@DefaultMessage("Automatic")
	String automatic();

	@DefaultMessage("{0} Build took {1}.")
	String buildDuration(String description, String duration);

	@DefaultMessage("Build {0}")
	String buildNumber(Integer number);

	@DefaultMessage("resulted in {0}.")
	String buildResult(String result);

	@DefaultMessage("is pending.")
	String buildResultPending();

	@DefaultMessage("Build Status")
	String buildStatus();

	@DefaultMessage("You cannot disable your own account.")
	String cannotDisableOwnAccount();

	@DefaultMessage("Cannot read CSV: {0}")
	String cannotReadCsv(String errorMessage);

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

	@DefaultMessage("Confirm Delete")
	String confirmDelete();

	@DefaultMessage("Confirm Leave This Project")
	String confirmLeaveProject();

	@DefaultMessage("Please confirm you would like to leave this project as a member. Once you leave the project, you will not be able to participate in the project any longer and will require being invited to join the project.")
	String confirmLeaveProjectMsg();

	@DefaultMessage("Confirm Remove")
	String confirmRemove();

	@DefaultMessage("Create Account")
	String createAccount();

	@DefaultMessage("Create Deployment")
	String createDeployment();

	@DefaultMessage("Create from template")
	String createFromTemplate();

	@DefaultMessage("Create Invitations")
	String createInvitations();

	@DefaultMessage("Create New Service...")
	String createNewService();

	@DefaultMessage("Create Project")
	String createProject();

	@DefaultMessage("Creating invitations...")
	String creatingInvitations();

	@DefaultMessage("Creating product...")
	String creatingProduct();

	@DefaultMessage("Creating project...")
	String creatingProject();

	@DefaultMessage("Credentials")
	String credentials();

	@DefaultMessage("Dashboard")
	String dashboard();

	@DefaultMessage("Day")
	String day();

	@DefaultMessage("Also delete this application in the service.")
	String deleteDeployedApplicationServiceAlso();

	@DefaultMessage("This will permanently remove the deployed application from this project. This operation cannot be undone.")
	String deleteDeployedApplicationWarning();

	@DefaultMessage("Delete Project")
	String deleteProject();

	@DefaultMessage("I understand that my project will be permanently deleted.")
	String deleteProjectIUnderstand();

	@DefaultMessage("This will permanently delete the project including all associated services and their data. This operation cannot be undone.")
	String deleteProjectWarning();

	@DefaultMessage("Delete SSH Key")
	String deleteSshKey();

	@DefaultMessage("Are you sure you want to remove this SSH key?")
	String deleteSshKeyConfirm();

	@DefaultMessage("Deleting component...")
	String deletingComponent();

	@DefaultMessage("Deleting product...")
	String deletingProduct();

	@DefaultMessage("Deleting project...")
	String deletingProject();

	@DefaultMessage("Deleting release...")
	String deletingRelease();

	@DefaultMessage("Deleting repository...")
	String deletingRepository();

	@DefaultMessage("Deployment will happen after the next build")
	String deploymentAfterNextBuild();

	@DefaultMessage("Deployment will happen after saving")
	String deploymentAfterSaving();

	@DefaultMessage("Deploy unstable builds")
	String deployUnstableBuilds();

	@DefaultMessage("Description")
	String description();

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

	@DefaultMessage("Please enter an email.")
	String enterEmail();

	@DefaultMessage("Enter Email Address")
	String enterEmailAddress();

	@DefaultMessage("Please enter a valid email.")
	String enterValidEmail();

	@DefaultMessage("The referenced entity cannot be found or no longer exists.")
	String entityNotFound();

	@DefaultMessage("Error while saving")
	String errorWhileSaving();

	@DefaultMessage("External Source Repositories")
	String externalSourceRepositories();

	@DefaultMessage("First, Last, email@example.com")
	String firstLastEmailExample();

	@DefaultMessage("First Name")
	String firstName();

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

	@DefaultMessage("Opted Out of News Emails")
	String hideNewsEmailOptOut();

	@DefaultMessage("Opted Out of Service Emails")
	String hideServiceEmailOptOut();

	@DefaultMessage("Unverified Email Addresses")
	String hideUnverified();

	@DefaultMessage("Hosted Source Repositories")
	String hostedSourceRepositories();

	@DefaultMessage("Image from <a href=\"http://www.gravatar.com\" target=\"_blank\" class=\"underline\">gravatar.com</a>")
	SafeHtml imageFromGravatar();

	@DefaultMessage("Instances")
	String instances();

	@DefaultMessage("Invalid username or password. Please try again.")
	String invalidUsername();

	@DefaultMessage("Invitation accepted")
	String invitationAccepted();

	@DefaultMessage("Invitation Creator")
	String invitationCreator();

	@DefaultMessage("Invitation email sent to {0}")
	String invitationEmailSentTo(String recipient);

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

	@DefaultMessage("Invite")
	String invite();

	@DefaultMessage("Note: To grant access to a user, use the Invite link. You can edit or remove existing team members using the table on this page.")
	String inviteMemberNote();

	@DefaultMessage("Job")
	String job();

	@DefaultMessage("Key")
	String key();

	@DefaultMessage("Language")
	String language();

	@DefaultMessage("Last Deployment")
	String lastDeployment();

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

	@DefaultMessage("Manual")
	String manual();

	@DefaultMessage("Mapped URL")
	String mappedUrl();

	@DefaultMessage("Mapped URLs")
	String mappedUrls();

	@DefaultMessage("Maven")
	String maven();

	@DefaultMessage("To deploy Maven artifacts to this repository, use:")
	String mavenDeployLocation();

	@DefaultMessage("The Maven repository for this project is located at:")
	String mavenRepositoryLocation();

	@DefaultMessage("Member")
	String member();

	@DefaultMessage("Member removed.")
	String memberRemoved();

	@DefaultMessage("Member updated.")
	String memberUpdated();

	@DefaultMessage("Memory")
	String memory();

	@DefaultMessage("{0} minutes")
	String minutes(Long duration);

	@DefaultMessage("new")
	String newLc();

	@DefaultMessage("You don''t have any deployments yet. Create a new deployment to get started.")
	String noDeploymentsMessage();

	@DefaultMessage("<none>")
	String noneBracketed();

	@DefaultMessage("No services defined")
	String noServicesDefined();

	@DefaultMessage("Notifications")
	String notifications();

	@DefaultMessage("New features, tips, and events")
	String notificationsNewFeaturesTipsEvents();

	@DefaultMessage("Service and system maintenance updates")
	String notificationsServiceMaintenanceUpdates();

	@DefaultMessage("Task updates, attachments and comments")
	String notificationsTaskUpdatesAttachmentsComments();

	@DefaultMessage("Not yet")
	String notYet();

	@DefaultMessage("Now Create Your {0} Account")
	String nowCreateYourAccount(String code2Cloud);

	@DefaultMessage("Open Tasks")
	String openTasks();

	@DefaultMessage("Organization Private")
	String organizationPrivate();

	@DefaultMessage("Organization Shared")
	String organizationShared();

	@DefaultMessage("Organization Updated")
	String organizationUpdated();

	@DefaultMessage("Owner")
	String owner();

	@DefaultMessage("Owner + Member")
	String ownerAndMember();

	@DefaultMessage("owner")
	String ownerLowercase();

	@DefaultMessage("{0} of {1}")
	String pagerOf(String current, String total);

	@DefaultMessage("{0} of over {1}")
	String pagerOfOver(String current, String totalEstimate);

	@DefaultMessage("Password")
	String password();

	@DefaultMessage("Password and Confirm Password must be the same.")
	String passwordAndConfirmationMustMatch();

	@DefaultMessage("Change your Password")
	String passwordChange();

	@DefaultMessage("Confirm Password")
	String passwordConfirm();

	@DefaultMessage("The current password you gave is incorrect.")
	String passwordIncorrect();

	@DefaultMessage("New Password")
	String passwordNew();

	@DefaultMessage("New Password and Confirm Password must be the same.")
	String passwordNewAndConfirmationMustMatch();

	@DefaultMessage("Old Password")
	String passwordOld();

	@DefaultMessage("Please provide a password.")
	String passwordProvide();

	@DefaultMessage("Password Reset")
	String passwordReset();

	@DefaultMessage("Password reset instructions have been sent to your email.")
	String passwordResetInstructionsSent();

	@DefaultMessage("Please choose a new password.")
	String passwordResetNewPassword();

	@DefaultMessage("have at least one digit character")
	String passwordRulesIncludeDigit();

	@DefaultMessage("have at least one lowercase character")
	String passwordRulesIncludeLc();

	@DefaultMessage("Your password must:")
	String passwordRulesLabel();

	@DefaultMessage("be at least 8 characters in length")
	String passwordRulesMinLength();

	@DefaultMessage("not include your username")
	String passwordRulesNotIncludeUsername();

	@DefaultMessage("Password updated.")
	String passwordUpdated();

	@DefaultMessage("Your password has been updated. You are now signed in.")
	String passwordUpdatedSignedIn();

	@DefaultMessage("Picture")
	String picture();

	@DefaultMessage("Privacy")
	String privacy();

	@DefaultMessage("Private")
	String private_();

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

	@DefaultMessage("Project Details")
	String projectDetails();

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

	@DefaultMessage("Remove GitHub link for {0}")
	String removeGitHubLinkFor(String linkOwner);

	@DefaultMessage("Do you want to remove this person from the project?")
	String removeMemberConfirmation();

	@DefaultMessage("Are you sure you want to remove <span style=\"font-weight: bolder;\">{0}</span>?")
	SafeHtml removeRepositoryConfirmation(String repositoryName);

	@DefaultMessage("This will permanently remove the repository from this project. This operation cannot be undone.")
	String removeRepositoryDetails();

	@DefaultMessage("Repository added.")
	String repositoryAdded();

	@DefaultMessage("Repository deleted.")
	String repositoryDeleted();

	@DefaultMessage("Reviews")
	String reviews();

	@DefaultMessage("Save and Close")
	String saveAndClose();

	@DefaultMessage("Save and Deploy")
	String saveAndDeploy();

	@DefaultMessage("Save New Password")
	String saveNewPassword();

	@DefaultMessage("Saving product...")
	String savingProduct();

	@DefaultMessage("Saving project...")
	String savingProject();

	@DefaultMessage("{0} seconds")
	String seconds(Long duration);

	@DefaultMessage("Select a Build...")
	String selectABuild();

	@DefaultMessage("Select a Job...")
	String selectAJob();

	@DefaultMessage("Select an Artifact...")
	String selectAnArtifact();

	@DefaultMessage("Select a Service...")
	String selectAService();

	@DefaultMessage("Select a template")
	String selectATemplate();

	@DefaultMessage("Please select a user first.")
	String selectUserFirst();

	@DefaultMessage("Sending invitation.")
	String sendingInvitation();

	@DefaultMessage("Send Invitation Emails")
	String sendInvitationEmails();

	@DefaultMessage("Send Verification")
	String sendVerification();

	@DefaultMessage("Server URL")
	String serverUrl();

	@DefaultMessage("Services")
	String services();

	@DefaultMessage("Severity")
	String severity();

	@DefaultMessage("Signed out")
	String signedOut();

	@DefaultMessage("sign in")
	String signInLc();

	@DefaultMessage("Sign in with GitHub")
	String signInWithGitHub();

	@DefaultMessage("Sign Out")
	String signOut();

	@DefaultMessage("Sign Up")
	String signUp();

	@DefaultMessage("Invalid format at line {0}: expecting First Name, Last Name, Email")
	String signUpTokenFromCsvInvalidFormat(int lineNumber);

	@DefaultMessage("Source Code")
	String sourceCode();

	@DefaultMessage("Git source code repositories are located at:")
	String sourceRepositoryLocation();

	@DefaultMessage("Sources")
	String sources();

	@DefaultMessage("SSH Keys")
	String sshKeys();

	@DefaultMessage("SSH Public Key (for external source repositories)")
	String sshPublicKeyForExternalRepos();

	@DefaultMessage("Submit Request")
	String submitRequest();

	@DefaultMessage("System Invitations")
	String systemInvitations();

	@DefaultMessage("Team Management")
	String teamManagement();

	@DefaultMessage("Team member updated.")
	String teamMemberUpdated();

	@DefaultMessage("Use as template")
	String templateProject();

	@DefaultMessage("Time: {0}.")
	String timeLabel(String timeValue);

	@DefaultMessage("Token required for sign up.")
	String tokenRequiredForSignUp();

	@DefaultMessage("Total")
	String total();

	@DefaultMessage("Unable to create project at this time. Please try again later.")
	String unableToCreateProject();

	@DefaultMessage("Unexpected error")
	String unexpectedError();

	@DefaultMessage("Unknown")
	String unknown();

	@DefaultMessage("Unknown activity type")
	String unknownActivityType();

	@DefaultMessage("Unwatch")
	String unwatch();

	@DefaultMessage("Unwatched project {0}")
	String unwatchedProject(String projectName);

	@DefaultMessage("Updating team member...")
	String updatingTeamMember();

	@DefaultMessage("has invited you to collaborate on a project")
	String userHasInvitedToCollaborate();

	@DefaultMessage("User Name")
	String userName();

	@DefaultMessage("Verification email sent. Please check your email to confirm your address.")
	String verificationEmailSent();

	@DefaultMessage("View Project Dashboard")
	String viewProjectDashboard();

	@DefaultMessage("Watch")
	String watch();

	@DefaultMessage("Watcher")
	String watcher();

	@DefaultMessage("Watching project {0}")
	String watchingProject(String projectName);

	@DefaultMessage("Welcome To {0}!")
	String welcomeTo(String code2Cloud);

	@DefaultMessage("What is Email Verification?")
	String whatIsEmailVerification();

	@DefaultMessage("<p>When you create your account or modify your account email address, we''ll send a verification link to the email address you used.</p>"
			+ "<p>Click the link in that email to verify that you own the email address.</p>"
			+ "<p>Email verification is required to access certain {0} features, such a system alerts.</p>")
	SafeHtml whatIsEmailVerificationDetails(String code2Cloud);

	@DefaultMessage("(What is Verification?)")
	String whatIsVerificationParens();

	@DefaultMessage("You can add content here by creating a wiki page called \"Home\".")
	String wikiHomePage();

	@DefaultMessage("Wiki Markup")
	String wikiMarkup();

}
