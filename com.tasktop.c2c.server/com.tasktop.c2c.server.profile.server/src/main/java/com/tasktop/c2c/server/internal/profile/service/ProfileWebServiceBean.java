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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.tasktop.c2c.server.cloud.domain.PoolStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.profile.domain.internal.RandomToken;
import com.tasktop.c2c.server.profile.domain.project.Agreement;
import com.tasktop.c2c.server.profile.domain.project.AgreementProfile;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.domain.project.PasswordResetToken;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectInvitationToken;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.domain.project.SignUpToken;
import com.tasktop.c2c.server.profile.domain.project.SshPublicKey;
import com.tasktop.c2c.server.profile.domain.project.SshPublicKeySpec;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.profile.service.ProfileWebService;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;

@Qualifier("main")
@Service
@Transactional(rollbackFor = { Exception.class })
public class ProfileWebServiceBean implements ProfileWebService, ProfileWebServiceInternal {

	@Autowired
	private ProfileServiceConfiguration configuration;

	@Autowired
	private ProfileServiceConfiguration profileServiceConfiguration;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProjectServiceService projectServiceService;

	private UsersConnectionRepository usersConnRepo;

	@Autowired
	private WebServiceDomain webServiceDomain;

	@Autowired
	@Override
	public void setUsersConnectionRepository(UsersConnectionRepository newConnRepo) {
		this.usersConnRepo = newConnRepo;
	}

	@Override
	public Profile getCurrentProfile() {

		com.tasktop.c2c.server.profile.domain.internal.Profile profile = profileService.getCurrentUserProfile();
		Profile p = null;
		if (profile != null) {
			p = webServiceDomain.copy(profile);

			// Add in whether we have a GitHub link now
			Connection<GitHub> apiConn = getAndTestGithubConnection();

			// If we have a connection, this is true.
			p.setGithubUsername(apiConn == null ? null : apiConn.getDisplayName());
		}
		return p;
	}

	@Override
	public void updateProfile(Profile p) throws ValidationException, EntityNotFoundException {
		com.tasktop.c2c.server.profile.domain.internal.Profile profile = webServiceDomain.copy(p);

		profileService.updateProfile(profile);
	}

	private ConnectionRepository getConnectionRepository() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return this.usersConnRepo.createConnectionRepository(username);
	}

	private Connection<GitHub> getAndTestGithubConnection() {
		ConnectionRepository connRepo = getConnectionRepository();
		Connection<GitHub> apiConn = connRepo.findPrimaryConnection(GitHub.class);

		if (apiConn == null) {
			// No connection exists right now.
			return null;
		}

		if (apiConn.test()) {
			return apiConn;
		} else {

			// Check to see if this is permanent
			if (apiConn.hasExpired()) {

				// Try refreshing the connection to see if that fixes things.
				apiConn.refresh();

				if (apiConn.hasExpired()) {
					// If it has still expired, then remove it - it's not really there any more anyways.
					connRepo.removeConnection(apiConn.getKey());
				} else {
					// Back in business! Test and then return the connection.
					if (apiConn.test()) {
						return apiConn;
					} else {
						// Something screwy going on - bail out.
						return null;
					}
				}
			} else {
				// This connection is permanently borked, remove it.
				connRepo.removeConnection(apiConn.getKey());
			}

			return null;
		}
	}

	@Override
	public Project getProjectForInvitationToken(String token) throws EntityNotFoundException {
		return webServiceDomain.copy(profileService.getProjectForInvitationToken(token), configuration);
	}

	@Override
	public Project getProjectByIdentifier(String projectIdentifier) throws EntityNotFoundException {
		return webServiceDomain.copy(profileService.getActiveProjectByIdentifier(projectIdentifier), configuration);
	}

	@Override
	public Project updateProject(Project project) throws EntityNotFoundException, ValidationException {
		return webServiceDomain.copy(profileService.updateProject(webServiceDomain.copy(project)));
	}

	@Override
	public Project createProject(Project project) throws EntityNotFoundException, ValidationException {
		com.tasktop.c2c.server.profile.domain.internal.Project a = webServiceDomain.copy(project);
		com.tasktop.c2c.server.profile.domain.internal.Profile p = profileService.getCurrentUserProfile();
		if (p == null) {
			throw new EntityNotFoundException();
		}
		return webServiceDomain.copy(profileService.createProject(p.getId(), a));
	}

	public void setConfiguration(ProfileServiceConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public List<Agreement> getPendingAgreements() throws EntityNotFoundException {
		List<com.tasktop.c2c.server.profile.domain.internal.Agreement> internalAgreements = profileService
				.getPendingAgreements();

		List<Agreement> agreements = webServiceDomain.copyAgreements(internalAgreements);
		return agreements;
	}

	@Override
	public void approveAgreement(Long agreementId) throws EntityNotFoundException {
		profileService.approveAgreement(agreementId);
	}

	@Override
	public List<AgreementProfile> getApprovedAgreementProfiles() throws EntityNotFoundException {
		List<com.tasktop.c2c.server.profile.domain.internal.AgreementProfile> internalAgreementProfiles = profileService
				.getApprovedAgreementProfiles();

		List<AgreementProfile> agreementProfiles = webServiceDomain.copyAgreementProfiles(internalAgreementProfiles);
		return agreementProfiles;
	}

	@Override
	public QueryResult<Project> findProjects(ProjectsQuery query) {
		QueryResult<com.tasktop.c2c.server.profile.domain.internal.Project> result = profileService.findProjects(query);
		return new QueryResult<Project>(result.getOffset(), result.getPageSize(), webServiceDomain.copyProjects(
				result.getResultPage(), configuration), result.getTotalResultSize());
	}

	@Override
	public void watchProject(String projectIdentifier) throws EntityNotFoundException {
		profileService.watchProject(projectIdentifier);
	}

	@Override
	public void unwatchProject(String projectIdentifier) throws EntityNotFoundException {
		profileService.unwatchProject(projectIdentifier);
	}

	@Override
	public Boolean isWatchingProject(String projectIdentifier) throws EntityNotFoundException {
		return profileService.isWatchingProject(projectIdentifier);
	}

	@Override
	public SignUpToken createSignUpToken(SignUpToken token) throws ValidationException {
		return webServiceDomain.copy(
				profileService.createSignUpToken(token.getFirstname(), token.getLastname(), token.getEmail()),
				profileServiceConfiguration);
	}

	@Override
	public List<SignUpToken> getUnusedSignUpTokens() {
		return webServiceDomain.copyTokens(profileService.getUnusedSignUpTokens(), profileServiceConfiguration);
	}

	@Override
	public void sendSignUpInvitation(String email) throws EntityNotFoundException {
		profileService.sendSignUpInvitation(email);
	}

	@Override
	public SignUpToken getSignUpToken(String token) throws EntityNotFoundException {
		return webServiceDomain.copy(profileService.getSignUpToken(token), profileServiceConfiguration);
	}

	@Override
	public ProjectInvitationToken getProjectInvitationToken(String token) throws EntityNotFoundException {
		return webServiceDomain.copy(profileService.getProjectInvitationToken(token));
	}

	@Override
	public Long createProfileWithSignUpToken(Profile profile, String tokenStr) throws EntityNotFoundException,
			ValidationException {
		com.tasktop.c2c.server.profile.domain.internal.Profile internalProfile = webServiceDomain.copy(profile);

		RandomToken dbtoken = null;
		if (configuration.getInvitationOnlySignUp() || tokenStr != null) {
			try {
				// will !NOT! rollback transaction
				dbtoken = profileService.getSignUpOrProjectInvitationToken(tokenStr);
			} catch (EntityNotFoundException e) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				throw e;
			}
		}

		// will throw ValidationException if profile is invalid.
		Long profileId = profileService.createProfile(internalProfile);

		// If a Github connection is floating in our session, save it now.
		registerConnectionForUsername(internalProfile.getUsername());

		if (configuration.getInvitationOnlySignUp() || tokenStr != null) {
			if (dbtoken instanceof com.tasktop.c2c.server.profile.domain.internal.SignUpToken) {
				// will throw EntityNotFoundException if token is unavailable.
				profileService.markSignUpTokenUsed(tokenStr);
			}
		}
		return profileId;
	}

	private void registerConnectionForUsername(String username) {
		RequestAttributes attribs = RequestContextHolder.getRequestAttributes();

		if (attribs == null) {
			// Nothing to see here, move along.
			return;
		}

		Connection<?> githubConn = ProviderSignInUtils.getConnection(attribs);

		// We have a connection, so save it now.
		if (githubConn != null) {
			ConnectionRepository connRepo = usersConnRepo.createConnectionRepository(username);
			connRepo.addConnection(githubConn);
		}
	}

	@Override
	public String inviteUserForProject(String email, String appIdentifier) throws EntityNotFoundException {
		return profileService.inviteUserForProject(email, appIdentifier);
	}

	@Override
	public void acceptInvitation(String invitationToken) throws EntityNotFoundException {
		profileService.acceptInvitation(invitationToken);
	}

	@Override
	public SshPublicKey createSshPublicKey(SshPublicKeySpec publicKey) throws ValidationException {
		com.tasktop.c2c.server.profile.domain.internal.SshPublicKey newKey = profileService
				.createSshPublicKey(publicKey);
		SshPublicKey copy = webServiceDomain.copy(newKey);
		return copy;
	}

	@Override
	public List<SshPublicKey> listSshPublicKeys() {
		List<com.tasktop.c2c.server.profile.domain.internal.SshPublicKey> keys = profileService.listSshPublicKeys();
		List<SshPublicKey> values = new ArrayList<SshPublicKey>(keys.size());
		for (com.tasktop.c2c.server.profile.domain.internal.SshPublicKey key : keys) {
			values.add(webServiceDomain.copy(key));
		}
		return values;
	}

	@Override
	public void removeSshPublicKey(Long publicKeyId) throws EntityNotFoundException {
		profileService.removeSshPublicKey(publicKeyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.service.ProfileWebService#isProjectCreateAvailble()
	 */
	@Override
	public Boolean isProjectCreateAvailable() {
		return profileService.isProjectCreateAvailable();
	}

	@Override
	public List<ProjectServiceStatus> computeProjectServicesStatus(String projectId) throws EntityNotFoundException {
		com.tasktop.c2c.server.profile.domain.internal.Project project = profileService
				.getProjectByIdentifier(projectId);
		return projectServiceService.computeProjectServicesStatus(project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.service.ProfileWebService#getPasswordResetToken(java.lang.String)
	 */
	@Override
	public PasswordResetToken getPasswordResetToken(String token) throws EntityNotFoundException {
		com.tasktop.c2c.server.profile.domain.internal.PasswordResetToken passwordResetToken = profileService
				.getPasswordResetToken(token);
		PasswordResetToken copy = webServiceDomain.copy(passwordResetToken);
		return copy;
	}

	@Override
	public Organization createOrganization(Organization org) throws ValidationException {
		return webServiceDomain.copy(profileService.createOrganization(webServiceDomain.copy(org)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.profile.service.ProfileWebService#getOrganizationByIdentfier(java.lang.String)
	 */
	@Override
	public Organization getOrganizationByIdentfier(String orgIdentifier) throws EntityNotFoundException {
		return webServiceDomain.copy(profileService.getOrganizationByIdentfier(orgIdentifier));
	}

	@Override
	public Organization updateOrganization(Organization organization) throws EntityNotFoundException,
			ValidationException {
		return webServiceDomain.copy(profileService.updateOrganization(webServiceDomain.copy(organization)));
	}

	@Override
	public List<PoolStatus> computeNodePoolStatus() {
		return projectServiceService.computePoolStatus();
	}
}
