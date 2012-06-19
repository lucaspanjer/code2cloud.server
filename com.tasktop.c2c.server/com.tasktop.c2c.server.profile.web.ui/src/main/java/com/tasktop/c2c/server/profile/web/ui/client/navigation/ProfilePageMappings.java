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
package com.tasktop.c2c.server.profile.web.ui.client.navigation;

import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMappings;
import com.tasktop.c2c.server.common.profile.web.client.place.AgreementsPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.OrganizationProjectsPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectHomePlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectsPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.SignInPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.AdminProfilePlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.AppSectionPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.EmailVerificationPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.HelpPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.InvitationCreatorPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.NewProjectPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.OrganizationAdminPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectAdminSettingsPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectDashboardPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectDeploymentPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectInvitationPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectTeamPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.RequestPasswordResetPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.ResetPasswordPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.SignOutPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.SignUpPlace;
import com.tasktop.c2c.server.profile.web.ui.client.place.UserAccountPlace;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.place.ProjectAdminSourcePlace;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.place.ProjectAdminTeamPlace;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmCommitPlace;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmPlace;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmRepoPlace;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class ProfilePageMappings extends PageMappings {

	public ProfilePageMappings() {
		super(ProjectsPlace.Discover, ProjectsPlace.Discover, OrganizationProjectsPlace.Discover,
				ProjectHomePlace.ProjectHome, SignOutPlace.SignOut, SignUpPlace.SignUp, SignInPlace.SignIn,
				ProjectDashboardPlace.ProjectDashboard, ProjectDeploymentPlace.ProjectDeployment,
				NewProjectPlace.NewProject, HelpPlace.Help, RequestPasswordResetPlace.RequestPasswordReset,
				UserAccountPlace.Account, ProjectTeamPlace.ProjectTeam, AgreementsPlace.Agreements,
				ProjectAdminSourcePlace.ProjectAdminSCM, ProjectAdminTeamPlace.ProjectAdminTeam,
				EmailVerificationPlace.VerifyEmail, ResetPasswordPlace.ResetPassword,
				ProjectInvitationPlace.ProjectInvitation, AppSectionPlace.AppSectionMapping,
				InvitationCreatorPlace.InvitationCreator, AdminProfilePlace.AdminProfiles,
				ProjectAdminSettingsPlace.ProjectAdminSettings, OrganizationAdminPlace.OrgAdminPlace,
				ScmCommitPlace.SCM_COMMIT, ScmRepoPlace.SCM_LOG, ScmPlace.SCM_PLACE);
	}

}
