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
package com.tasktop.c2c.server.internal.profile.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.internal.profile.crypto.OpenSSHPublicKeyReader;
import com.tasktop.c2c.server.profile.domain.internal.OrganizationProfile;
import com.tasktop.c2c.server.profile.domain.internal.PasswordResetToken;
import com.tasktop.c2c.server.profile.domain.internal.ProjectProfile;
import com.tasktop.c2c.server.profile.domain.project.Agreement;
import com.tasktop.c2c.server.profile.domain.project.AgreementProfile;
import com.tasktop.c2c.server.profile.domain.project.NotificationSettings;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectInvitationToken;
import com.tasktop.c2c.server.profile.domain.project.ProjectPreferences;
import com.tasktop.c2c.server.profile.domain.project.ProjectRole;
import com.tasktop.c2c.server.profile.domain.project.ProjectService;
import com.tasktop.c2c.server.profile.domain.project.ProjectTeamMember;
import com.tasktop.c2c.server.profile.domain.project.ProjectTeamSummary;
import com.tasktop.c2c.server.profile.domain.project.SignUpToken;
import com.tasktop.c2c.server.profile.domain.project.SshPublicKey;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.tasks.domain.TaskUserProfile;

@Component
public class WebServiceDomain {

	@Autowired
	private ProfileServiceConfiguration serviceConfiguration;

	public Project copy(com.tasktop.c2c.server.profile.domain.internal.Project project,
			ProfileServiceConfiguration configuration) {
		// First, copy the rest of our fields.
		Project p = copy(project);

		// Then, copy our project service profiles in if appropriate.
		if (project.getProjectServiceProfile() != null) {
			p.setProjectServices(copyProjectServices(project.getProjectServiceProfile().getProjectServices(),
					configuration));
		} else { // Prevent potential NPE
			project.setProjectProfiles(Collections.EMPTY_LIST);
		}
		return p;
	}

	public Project copy(com.tasktop.c2c.server.profile.domain.internal.Project project) {
		return copy(project, false);
	}

	public Project copy(com.tasktop.c2c.server.profile.domain.internal.Project project, boolean shallow) {
		Project p = new Project();
		p.setId(project.getId());
		p.setIdentifier(project.getIdentifier());
		p.setName(project.getName());
		p.setDescription(project.getDescription());
		p.setAccessibility(project.getAccessibility());
		p.setNumWatchers(project.getNumWatchers());
		p.setNumCommiters(project.getNumCommitters());
		if (!shallow) {
			p.setOrganization(copy(project.getOrganization()));
			p.setProjectPreferences(copy(project.getProjectPreferences()));
		}

		// Don't copy our ProjectServiceProfile as part of this - that requires a ProfileWebServiceConfiguration
		// be passed in.

		return p;
	}

	public ProjectPreferences copy(com.tasktop.c2c.server.profile.domain.internal.ProjectPreferences projectPreferences) {
		ProjectPreferences p = new ProjectPreferences();
		p.setId(projectPreferences.getId());
		p.setWikiLanguage(projectPreferences.getWikiLanguage());

		return p;
	}

	private List<ProjectService> copyProjectServices(
			List<com.tasktop.c2c.server.profile.domain.internal.ProjectService> projectServices,
			ProfileServiceConfiguration configuration) {
		List<ProjectService> result = new ArrayList<ProjectService>();

		for (com.tasktop.c2c.server.profile.domain.internal.ProjectService internalService : projectServices) {
			result.add(copy(internalService, configuration));
		}

		return result;
	}

	private ProjectService copy(com.tasktop.c2c.server.profile.domain.internal.ProjectService internalService,
			ProfileServiceConfiguration configuration) {
		ProjectService result = new ProjectService();

		result.setServiceType(internalService.getType());
		result.setId(internalService.getId());
		if (internalService.getServiceHost() != null) {
			result.setAvailable(internalService.getServiceHost().isAvailable());
		} else if (internalService.getExternalUrl() != null) {
			result.setAvailable(true);
		} else {
			result.setAvailable(false);
		}

		if (internalService.getExternalUrl() != null) {
			result.setUrl(internalService.getExternalUrl());
		} else {
			String restOfUrl = "";
			switch (internalService.getType()) {
			case BUILD:
				restOfUrl = "hudson/";
				break;
			case MAVEN:
				restOfUrl = "maven/";
				break;
			case SCM:
				restOfUrl = "scm/";
				break;
			case TASKS:
				restOfUrl = "tasks/";
				break;
			case WIKI:
				restOfUrl = "wiki/";
				break;
			}

			result.setUrl(configuration.getServiceUrlPrefix(internalService.getProjectServiceProfile().getProject()
					.getIdentifier())
					+ restOfUrl);
		}

		return result;
	}

	public com.tasktop.c2c.server.profile.domain.internal.Project copy(Project p) {
		com.tasktop.c2c.server.profile.domain.internal.Project project = new com.tasktop.c2c.server.profile.domain.internal.Project();
		project.setId(p.getId());
		project.setIdentifier(p.getIdentifier());
		project.setName(p.getName());
		project.setDescription(p.getDescription());
		project.setAccessibility(p.getAccessibility());
		project.setOrganization(copy(p.getOrganization()));
		project.setProjectPreferences(copy(p.getProjectPreferences()));

		return project;
	}

	public com.tasktop.c2c.server.profile.domain.internal.ProjectPreferences copy(ProjectPreferences p) {
		com.tasktop.c2c.server.profile.domain.internal.ProjectPreferences projectPreferences = new com.tasktop.c2c.server.profile.domain.internal.ProjectPreferences();
		projectPreferences.setId(p.getId());
		projectPreferences.setWikiLanguage(p.getWikiLanguage());

		return projectPreferences;
	}

	public com.tasktop.c2c.server.profile.domain.internal.Profile copy(Profile p) {
		com.tasktop.c2c.server.profile.domain.internal.Profile profile = new com.tasktop.c2c.server.profile.domain.internal.Profile();
		profile.setUsername(p.getUsername());
		profile.setEmail(p.getEmail());
		profile.setFirstName(p.getFirstName());
		profile.setLastName(p.getLastName());
		profile.setUsername(p.getUsername());
		profile.setPassword(p.getPassword());
		profile.setId(p.getId());
		profile.setNotificationSettings(copy(p.getNotificationSettings()));
		profile.setDisabled(p.getAccountDisabled());
		return profile;
	}

	public Profile copy(com.tasktop.c2c.server.profile.domain.internal.Profile profile) {
		Profile p = new Profile();
		p.setId(profile.getId());
		p.setUsername(profile.getUsername());
		p.setFirstName(profile.getFirstName());
		p.setLastName(profile.getLastName());
		p.setEmail(profile.getEmail());
		p.setEmailVerfied(profile.getEmailVerified());
		p.setGravatarHash(profile.getGravatarHash());
		// NOTE: we never copy password here
		p.setNotificationSettings(copy(profile.getNotificationSettings()));
		p.setAccountDisabled(profile.getDisabled());
		return p;
	}

	public List<Profile> copyProfiles(List<com.tasktop.c2c.server.profile.domain.internal.Profile> profiles) {
		List<Profile> result = new ArrayList<Profile>(profiles.size());
		for (com.tasktop.c2c.server.profile.domain.internal.Profile source : profiles) {
			result.add(copy(source));
		}
		return result;
	}

	/**
	 * @param notificationSettings
	 * @return
	 */
	public NotificationSettings copy(
			com.tasktop.c2c.server.profile.domain.internal.NotificationSettings notificationSettings) {
		if (notificationSettings == null) {
			return null;
		}
		NotificationSettings ns = new NotificationSettings();
		ns.setId(notificationSettings.getId());
		ns.setEmailTaskActivity(notificationSettings.getEmailTaskActivity());
		ns.setEmailNewsAndEvents(notificationSettings.getEmailNewsAndEvents());
		ns.setEmailServiceAndMaintenance(notificationSettings.getEmailServiceAndMaintenance());
		return ns;
	}

	/**
	 * @param notificationSettings
	 * @return
	 */
	public com.tasktop.c2c.server.profile.domain.internal.NotificationSettings copy(
			NotificationSettings notificationSettings) {
		if (notificationSettings == null) {
			return null;
		}
		com.tasktop.c2c.server.profile.domain.internal.NotificationSettings ns = new com.tasktop.c2c.server.profile.domain.internal.NotificationSettings();
		ns.setId(notificationSettings.getId());
		ns.setEmailTaskActivity(notificationSettings.getEmailTaskActivity());
		ns.setEmailNewsAndEvents(notificationSettings.getEmailNewsAndEvents());
		ns.setEmailServiceAndMaintenance(notificationSettings.getEmailServiceAndMaintenance());
		return ns;
	}

	public Agreement copy(com.tasktop.c2c.server.profile.domain.internal.Agreement agreement, Boolean withProfiles) {
		Agreement a = new Agreement();
		a.setId(agreement.getId());
		a.setTitle(agreement.getTitle());
		a.setText(agreement.getText());
		a.setCreationDate(agreement.getDateCreated());
		a.setRank(agreement.getRank());
		if (withProfiles) {
			a.setAgreementProfiles(copyAgreementProfiles(agreement.getAgreementProfiles()));
		}
		return a;
	}

	public List<AgreementProfile> copyAgreementProfiles(
			List<com.tasktop.c2c.server.profile.domain.internal.AgreementProfile> agreementProfiles) {
		List<AgreementProfile> aps = new ArrayList<AgreementProfile>();

		if (agreementProfiles == null) {
			return null;
		}

		for (com.tasktop.c2c.server.profile.domain.internal.AgreementProfile agreementProfile : agreementProfiles) {
			aps.add(copy(agreementProfile));
		}
		return aps;
	}

	public AgreementProfile copy(com.tasktop.c2c.server.profile.domain.internal.AgreementProfile agreementProfile) {
		AgreementProfile ap = new AgreementProfile();
		ap.setAgreementDate(agreementProfile.getDateAgreed());
		ap.setAgreement(copy(agreementProfile.getAgreement(), false));
		return ap;
	}

	public List<Agreement> copyAgreements(List<com.tasktop.c2c.server.profile.domain.internal.Agreement> agreements) {
		List<Agreement> copies = new ArrayList<Agreement>(agreements.size());
		for (com.tasktop.c2c.server.profile.domain.internal.Agreement agreement : agreements) {
			copies.add(copy(agreement, true));
		}
		return copies;
	}

	public List<Project> copyProjects(List<com.tasktop.c2c.server.profile.domain.internal.Project> resultPage,
			ProfileServiceConfiguration configuration) {
		List<Project> projects = new ArrayList<Project>(resultPage.size());
		for (com.tasktop.c2c.server.profile.domain.internal.Project internalProject : resultPage) {
			projects.add(copy(internalProject, configuration));
		}
		return projects;
	}

	public SignUpToken copy(com.tasktop.c2c.server.profile.domain.internal.SignUpToken internalToken,
			ProfileServiceConfiguration configuration) {
		SignUpToken token = new SignUpToken();
		token.setToken(internalToken.getToken());
		token.setFirstname(internalToken.getFirstname());
		token.setLastname(internalToken.getLastname());
		token.setEmail(internalToken.getEmail());
		token.setUrl(configuration.getSignUpInvitationURL(internalToken.getToken()));
		return token;
	}

	public List<SignUpToken> copyTokens(List<com.tasktop.c2c.server.profile.domain.internal.SignUpToken> tokens,
			ProfileServiceConfiguration configuration) {
		List<SignUpToken> convertedTokens = new ArrayList<SignUpToken>();
		for (com.tasktop.c2c.server.profile.domain.internal.SignUpToken token : tokens) {
			convertedTokens.add(copy(token, configuration));
		}
		return convertedTokens;
	}

	public TaskUserProfile copy(ProjectProfile projectProfile) {
		com.tasktop.c2c.server.profile.domain.internal.Profile profile = projectProfile.getProfile();
		TaskUserProfile taskUserProfile = new TaskUserProfile();
		taskUserProfile.setId(profile.getId().intValue());
		taskUserProfile.setLoginName(profile.getUsername());
		taskUserProfile.setRealname(profile.getFullName());
		taskUserProfile.setGravatarHash(profile.getGravatarHash());
		return taskUserProfile;
	}

	public ProjectInvitationToken copy(com.tasktop.c2c.server.profile.domain.internal.InvitationToken internalToken) {
		ProjectInvitationToken token = new ProjectInvitationToken();
		token.setToken(internalToken.getToken());
		token.setEmail(internalToken.getEmail());
		token.setIssuingUser(copy(internalToken.getIssuingProfile()));
		return token;
	}

	public com.tasktop.c2c.server.profile.domain.project.PasswordResetToken copy(PasswordResetToken p) {
		com.tasktop.c2c.server.profile.domain.project.PasswordResetToken passwordResetToken = new com.tasktop.c2c.server.profile.domain.project.PasswordResetToken();
		passwordResetToken.setToken(p.getToken());
		passwordResetToken.setProfile(copy(p.getProfile()));
		return passwordResetToken;
	}

	private OpenSSHPublicKeyReader rsaReader = new OpenSSHPublicKeyReader();

	public SshPublicKey copy(com.tasktop.c2c.server.profile.domain.internal.SshPublicKey key) {
		SshPublicKey copy = new SshPublicKey();
		copy.setAlgorithm(key.getAlgorithm());
		copy.setFingerprint(key.getFingerprint());
		copy.setId(key.getId());
		copy.setName(key.getName());
		if (key.getAlgorithm().equals("RSA")) {
			try {
				copy.setKeyText(rsaReader.computeEncodedKeyText(key));
			} catch (Exception e) {
				// ignore;
			}
		}
		return copy;
	}

	public com.tasktop.c2c.server.profile.domain.internal.SshPublicKey copy(SshPublicKey key) {
		com.tasktop.c2c.server.profile.domain.internal.SshPublicKey copy = new com.tasktop.c2c.server.profile.domain.internal.SshPublicKey();
		copy.setAlgorithm(key.getAlgorithm());
		copy.setFingerprint(key.getFingerprint());
		copy.setId(key.getId());
		copy.setName(key.getName());
		return copy;
	}

	/**
	 * @param org
	 * @return
	 */
	public com.tasktop.c2c.server.profile.domain.internal.Organization copy(Organization org) {
		if (org == null) {
			return null;
		}

		com.tasktop.c2c.server.profile.domain.internal.Organization target = new com.tasktop.c2c.server.profile.domain.internal.Organization();
		target.setDescription(org.getDescription());
		target.setId(org.getId());
		target.setIdentifier(org.getIdentifier());
		target.setName(org.getName());
		target.setProjectPreferences(copy(org.getProjectPreferences()));
		return target;
	}

	/**
	 * @param createOrganization
	 * @return
	 */
	public Organization copy(com.tasktop.c2c.server.profile.domain.internal.Organization org) {
		if (org == null) {
			return null;
		}
		Organization target = new Organization();
		target.setDescription(org.getDescription());
		target.setId(org.getId());
		target.setIdentifier(org.getIdentifier());
		target.setName(org.getName());
		if (org.getProjects() != null) {
			target.setProjects(new ArrayList<Project>(org.getProjects().size()));
			for (com.tasktop.c2c.server.profile.domain.internal.Project p : org.getProjects()) {
				target.getProjects().add(copy(p, true));
			}
		}
		if (org.getOrganizationProfiles() != null) {
			target.setMembers(new ArrayList<Profile>());
			for (OrganizationProfile op : org.getOrganizationProfiles()) {
				target.getMembers().add(copy(op.getProfile()));
			}
		}
		target.setProjectPreferences(copy(org.getProjectPreferences()));

		return target;
	}

	public ProjectTeamSummary copyTeamSummary(com.tasktop.c2c.server.profile.domain.internal.Project project) {
		ProjectTeamSummary summary = new ProjectTeamSummary();

		List<ProjectTeamMember> teamMemberList = new ArrayList<ProjectTeamMember>();
		for (ProjectProfile projectProfile : project.getProjectProfiles()) {
			// if the profile only has the Community role then they are just watching and don't appear in the team
			// list.
			Set<ProjectRole> profileRoles = new HashSet<ProjectRole>();

			if (projectProfile.getUser()) {
				profileRoles.add(ProjectRole.MEMBER);
			}

			if (projectProfile.getOwner()) {
				profileRoles.add(ProjectRole.OWNER);
			}

			if (profileRoles.size() == 0) {
				// We had a user with only the community role - skip them, as they aren't really a part of the
				// project
				// team (they are just watching the project).
				continue;
			}

			Profile domainProfile = copy(projectProfile.getProfile());

			ProjectTeamMember projectTeamMember = new ProjectTeamMember();
			projectTeamMember.setProfile(domainProfile);
			projectTeamMember.setRoles(profileRoles);

			teamMemberList.add(projectTeamMember);
		}

		Collections.sort(teamMemberList);
		summary.setMembers(teamMemberList);
		return summary;

	}

}
