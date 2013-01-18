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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.auth.service.AuthenticationService;
import com.tasktop.c2c.server.auth.service.AuthenticationServiceUser;
import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.common.service.AbstractJpaServiceBean;
import com.tasktop.c2c.server.common.service.AuthenticationException;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.InsufficientPermissionsException;
import com.tasktop.c2c.server.common.service.ReplicationScope;
import com.tasktop.c2c.server.common.service.Security;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.domain.QueryRequest;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.common.service.domain.Quota;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.domain.SortInfo;
import com.tasktop.c2c.server.common.service.job.JobService;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.internal.profile.crypto.PublicKeyReader;
import com.tasktop.c2c.server.profile.domain.Email;
import com.tasktop.c2c.server.profile.domain.internal.Agreement;
import com.tasktop.c2c.server.profile.domain.internal.AgreementProfile;
import com.tasktop.c2c.server.profile.domain.internal.BaseEntity;
import com.tasktop.c2c.server.profile.domain.internal.ConfigurationProperty;
import com.tasktop.c2c.server.profile.domain.internal.EmailVerificationToken;
import com.tasktop.c2c.server.profile.domain.internal.InvitationToken;
import com.tasktop.c2c.server.profile.domain.internal.Organization;
import com.tasktop.c2c.server.profile.domain.internal.OrganizationProfile;
import com.tasktop.c2c.server.profile.domain.internal.PasswordResetToken;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectPreferences;
import com.tasktop.c2c.server.profile.domain.internal.ProjectProfile;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.RandomToken;
import com.tasktop.c2c.server.profile.domain.internal.SignUpToken;
import com.tasktop.c2c.server.profile.domain.internal.SshPublicKey;
import com.tasktop.c2c.server.profile.domain.project.ProjectAccessibility;
import com.tasktop.c2c.server.profile.domain.project.ProjectRelationship;
import com.tasktop.c2c.server.profile.domain.project.ProjectsQuery;
import com.tasktop.c2c.server.profile.domain.project.SignUpTokens;
import com.tasktop.c2c.server.profile.domain.project.SshPublicKeySpec;
import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;
import com.tasktop.c2c.server.profile.domain.validation.ProfilePasswordValidator;
import com.tasktop.c2c.server.profile.service.EmailService;
import com.tasktop.c2c.server.profile.service.NotificationService;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;
import com.tasktop.c2c.server.profile.service.QuotaService;
import com.tasktop.c2c.server.profile.service.provider.TaskServiceProvider;
import com.tasktop.c2c.server.profile.service.provider.WikiServiceProvider;
import com.tasktop.c2c.server.tasks.domain.TaskUserProfile;
import com.tasktop.c2c.server.tasks.service.TaskService;
import com.tasktop.c2c.server.util.VelocityUtils;
import com.tasktop.c2c.server.wiki.domain.Person;
import com.tasktop.c2c.server.wiki.service.WikiService;

/**
 * Main implementation of the {@link ProfileService} using JPA.
 * 
 * @author David Green <david.green@tasktop.com> (Tasktop Technologies Inc.)
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * @author Lucas Panjer <lucas.panjer@tasktop.com> (Tasktop Technologies Inc.)
 * @author Ryan Slobojon <ryan.slobojan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
@Service("profileService")
@Qualifier("main")
@Transactional(rollbackFor = { Exception.class })
public class ProfileServiceBean extends AbstractJpaServiceBean implements ProfileService, InternalProfileService {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileServiceBean.class);

	private static final int MAX_SIZE = 1000;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private SecurityPolicy securityPolicy;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private ProjectServiceService projectServiceService;

	@Autowired
	private ProfileServiceConfiguration configuration;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private JobService jobService;

	@Autowired
	private TaskServiceProvider taskServiceProvider;

	@Autowired
	private WikiServiceProvider wikiServiceProvider;

	@Autowired(required = true)
	@Qualifier("main")
	private PublicKeyReader publicKeyReader;

	@Autowired
	private WebServiceDomain webServiceDomain;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private IdentityManagmentService identityManagmentService;

	@Autowired
	private QuotaService quotaService;

	private static final String profileCreatedTemplate = "email_templates/profileCreated.vm";
	private static final String passwordResetTemplate = "email_templates/passwordResetRequest.vm";
	private static final String projectInvitationTemplate = "email_templates/projectInvitation.vm";
	private static final String signUpInvitationTemplate = "email_templates/signUpInvitation.vm";
	private static final String emailVerificationTemplate = "email_templates/emailVerification.vm";

	public void setTaskServiceProvider(TaskServiceProvider taskServiceProvider) {
		this.taskServiceProvider = taskServiceProvider;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	private final class ProfileConstraintsCreateValidator implements Validator {
		// FIXME: we could move this into a registered validator if we could
		// figure out how to inject the entity manager (request scope)
		@SuppressWarnings("unchecked")
		@Override
		public void validate(Object target, Errors errors) {
			Profile profile = (Profile) target;
			if (profile.getUsername() != null && profile.getUsername().trim().length() > 0) {

				if (identityManagmentService.usernameExists(profile.getUsername())) {
					errors.reject("profile.usernameUnique", new Object[] { profile.getUsername() }, null);
				}

			}
			if (profile.getEmail() != null && profile.getEmail().trim().length() > 0) {

				try {
					Profile userWithEmail = identityManagmentService.getProfileByEmail(profile.getEmail());
					if (!userWithEmail.equals(profile)) {
						errors.reject("profile.emailUnique", new Object[] { profile.getEmail() }, null);
					}
				} catch (EntityNotFoundException e) {
					// expected
				}

			}
		}

		@Override
		public boolean supports(Class<?> clazz) {
			return Profile.class.isAssignableFrom(clazz);
		}
	}

	private final class ProfileConstraintsUpdateValidator implements Validator {
		// FIXME: we could move this into a registered validator if we could
		// figure out how to inject the entity manager (request scope)
		@SuppressWarnings("unchecked")
		@Override
		public void validate(Object target, Errors errors) {
			Profile profile = (Profile) target;

			if (profile.getEmail() != null && profile.getEmail().trim().length() > 0) {

				try {
					Profile userWithEmail = identityManagmentService.getProfileByEmail(profile.getEmail());
					if (!userWithEmail.equals(profile)) {
						errors.reject("profile.emailUnique", new Object[] { profile.getEmail() }, null);
					}
				} catch (EntityNotFoundException e) {
					// expected
				}

			}
		}

		@Override
		public boolean supports(Class<?> clazz) {
			return Profile.class.isAssignableFrom(clazz);
		}
	}

	private final class ProjectConstraintsValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			return Project.class.isAssignableFrom(clazz);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void validate(Object target, Errors errors) {
			Project project = (Project) target;
			if (project.getName() != null && project.getName().trim().length() > 0) {

				String queryString = "select e from " + Project.class.getSimpleName()
						+ " e where (e.name = :name or e.identifier = :identifier) and ";
				if (project.getOrganization() != null) {
					queryString += "e.organization = :organization";
				} else {
					queryString += "e.organization is null";
				}
				Query query = entityManager.createQuery(queryString);
				query.setParameter("name", project.getName()).setParameter("identifier", project.getIdentifier());

				if (project.getOrganization() != null) {
					query.setParameter("organization", project.getOrganization());
				}
				List<Project> projectsWithName = query.getResultList();
				if (!projectsWithName.isEmpty()) {
					if (projectsWithName.size() != 1 || !projectsWithName.get(0).equals(project)) {
						errors.reject("project.nameUnique", new Object[] { project.getName() }, null);
					}
				}
			}
		}
	}

	@Override
	public Long createProfile(Profile profile) throws ValidationException {
		if (entityManager.contains(profile)) {
			throw new IllegalArgumentException();
		}
		fixupCase(profile);

		securityPolicy.create(profile);

		validate(profile, validator, new ProfileConstraintsCreateValidator(), new ProfilePasswordValidator());
		profile.setNotificationSettings(notificationService.constructDefaultSettings());

		Profile created = identityManagmentService.createProfile(profile);

		sendVerificationEmail(created);

		return created.getId();
	}

	private void sendWelcomEmail(Profile profile) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("profile", profile);
		model.put("AppName", configuration.getAppName());
		model.put("AppBaseUrl", configuration.getProfileBaseUrl());

		String bodyText = VelocityUtils.getLocalizedTemplateText(velocityEngine, profileCreatedTemplate, model,
				AuthenticationServiceUser.getCurrentUserLanguage());
		Email email = new Email(profile.getEmail(), "Welcome to " + configuration.getAppName(), bodyText, "text/plain");
		emailService.schedule(email);
	}

	@Secured({ Role.User, Role.Admin })
	@Override
	public void updateProfile(Profile profile) throws ValidationException, EntityNotFoundException {
		fixupCase(profile);

		securityPolicy.modify(profile);

		Profile currentProfile = entityManager.find(Profile.class, profile.getId());
		if (currentProfile == null) {
			throw new EntityNotFoundException();
		}

		boolean passwordReset = false;
		boolean nameChanged = true;
		boolean emailChanged = true;

		nameChanged = !currentProfile.getFirstName().equals(profile.getFirstName())
				|| !currentProfile.getLastName().equals(profile.getLastName());
		emailChanged = !currentProfile.getEmail().equals(profile.getEmail());

		if (profile.getPassword() != null && profile.getPassword().trim().length() > 0) {
			passwordReset = true;
		}

		// Validation
		List<Validator> validators = new ArrayList<Validator>();
		validators.add(validator);
		validators.add(new ProfileConstraintsUpdateValidator());
		if (passwordReset) {
			validators.add(new ProfilePasswordValidator());
		}
		validate(profile, validators);

		currentProfile = identityManagmentService.updateProfile(profile);

		if (nameChanged || emailChanged) {
			// use a Set to avoid duplicates - we don't want to update a project for this profile more than once
			Set<String> projectsToUpdate = new HashSet<String>();

			// update Projects the Profile is directly associated with
			for (ProjectProfile projectProfile : currentProfile.getProjectProfiles()) {
				projectsToUpdate.add(projectProfile.getProject().getIdentifier());
				jobService.schedule(new ReplicateProjectProfileJob(projectProfile.getProject(), projectProfile
						.getProfile().getId(), ReplicationScope.UPDATE_IF_EXISTS));
			}

			// update Projects the Profile has access to (for example, public Projects)
			for (ProjectProfile projectProfile : findAccessibleProjectsForProfile(currentProfile)) {
				if (!projectsToUpdate.contains(projectProfile.getProject().getIdentifier())) {
					jobService.schedule(new ReplicateProjectProfileJob(projectProfile.getProject(), projectProfile
							.getProfile().getId(), ReplicationScope.UPDATE_IF_EXISTS));
				}
			}
		}
		if (emailChanged) {
			currentProfile.setEmailVerified(false);
			sendVerificationEmail(currentProfile);
		}
	}

	/**
	 * Looks up all Projects for which this Profile is indirectly associated, then returns a List of ProjectProfiles of
	 * the same length, with each found Project is paired with the passed-in Profile. This includes public Projects as
	 * well as org-private Projects for Organizations to which the Profile belongs.
	 * 
	 * @param profile
	 * @return
	 */
	public List<ProjectProfile> findAccessibleProjectsForProfile(Profile profile) {
		List<ProjectProfile> ppList = new ArrayList<ProjectProfile>();
		// Query to find all Projects with Public access
		Query publicQuery = entityManager.createQuery(
				"SELECT project FROM " + Project.class.getSimpleName()
						+ " project WHERE project.accessibility = :public").setParameter("public",
				ProjectAccessibility.PUBLIC);
		List<Project> publicResults = publicQuery.getResultList();
		for (Project project : publicResults) {
			ProjectProfile pp = new ProjectProfile();
			pp.setProfile(profile);
			pp.setProject(project);
			ppList.add(pp);
		}
		List<String> orgIdsForCurrentUser = AuthUtils.findOrganizationIdsForCurrentUser();
		// Query to find all Projects with Org-Private access, and where the Profile's Organization is related to the
		// Profile
		if (orgIdsForCurrentUser.size() > 0) {
			StringBuilder queryBldr = new StringBuilder().append("SELECT project FROM ")
					.append(Project.class.getSimpleName()).append(" ")
					.append("project JOIN project.organization AS org ")
					.append("WHERE project.accessibility = :orgPrivate ").append("AND org.identifier IN :orgIds");
			Query orgPrivateQuery = entityManager.createQuery(queryBldr.toString());
			orgPrivateQuery.setParameter("orgPrivate", ProjectAccessibility.ORGANIZATION_PRIVATE);
			orgPrivateQuery.setParameter("orgIds", orgIdsForCurrentUser);
			List<Project> orgPrivateResults = orgPrivateQuery.getResultList();
			for (Project project : orgPrivateResults) {
				ProjectProfile pp = new ProjectProfile();
				pp.setProfile(profile);
				pp.setProject(project);
				ppList.add(pp);
			}
		}
		return ppList;
	}

	@Secured(Role.User)
	@Override
	public void updateProjectProfile(ProjectProfile projectProfile) throws EntityNotFoundException, ValidationException {
		securityPolicy.modify(projectProfile);
		if (!entityManager.contains(projectProfile)) {
			ProjectProfile managedApplicationProfile = entityManager.find(ProjectProfile.class, projectProfile.getId());
			if (managedApplicationProfile == null) {
				throw new EntityNotFoundException();
			}

			managedApplicationProfile.setOwner(projectProfile.getOwner());
			managedApplicationProfile.setUser(projectProfile.getUser());
			managedApplicationProfile.setCommunity(projectProfile.getCommunity());

			// test for user removing self as owner, disallow
			if (!managedApplicationProfile.getOwner()
					&& getCurrentUserProfile().equals(managedApplicationProfile.getProfile())) {
				Errors errors = createErrors(projectProfile);
				errors.reject("project.ownerCannotRemoveSelf");
				throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
			}
			projectProfile = managedApplicationProfile;
		}
		validate(projectProfile, validator);
	}

	@Secured(Role.User)
	@Override
	public void createProjectProfile(String projectId, String username) throws EntityNotFoundException {
		Profile profile = identityManagmentService.getProfileByUsername(username);
		Project project = getProjectByIdentifier(projectId);
		ProjectProfile pp = addProjectProfileInternal(project, profile);
		securityPolicy.create(pp);

	}

	@Secured(Role.User)
	@Override
	public ProjectProfile getProjectProfile(Long projectId, Long profileId) throws EntityNotFoundException {
		ProjectProfile projectProfile;
		try {
			projectProfile = (ProjectProfile) entityManager
					.createQuery(
							"select e from " + ProjectProfile.class.getSimpleName()
									+ " e where e.profile.id = :p and e.project.id = :a").setParameter("p", profileId)
					.setParameter("a", projectId).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		}
		securityPolicy.retrieve(projectProfile);
		return projectProfile;
	}

	/**
	 * username and email should always be lower-case to avoid potential for duplicates
	 */
	private void fixupCase(Profile profile) {
		if (profile.getEmail() != null) {
			profile.setEmail(profile.getEmail().toLowerCase());
		}
		if (profile.getUsername() != null) {
			profile.setUsername(profile.getUsername().toLowerCase());
		}
	}

	@Override
	public Long authenticate(String username, String password) throws AuthenticationException {
		SecurityContextHolder.getContext().setAuthentication(null);

		AuthenticationToken token = authenticationService.authenticate(username, password);
		AuthenticationServiceUser user = AuthenticationServiceUser.fromAuthenticationToken(token, password);

		AuthUtils.insertNewAuthToken(user, password, token.getAuthorities(), token);

		try {
			return getProfileByUsername(username).getId();
		} catch (EntityNotFoundException e) {
			// should never happen
			throw new IllegalStateException(e);
		}
	}

	@Secured(Role.User)
	@Override
	public Profile getProfile(Long id) throws EntityNotFoundException {
		Profile profile = getProfileInternal(id);
		securityPolicy.retrieve(profile);
		return profile;
	}

	private Profile getProfileInternal(Long id) throws EntityNotFoundException {
		if (id != null) {
			Profile profile = entityManager.find(Profile.class, id);
			if (profile != null) {
				return profile;
			}
		}
		throw new EntityNotFoundException();
	}

	@Secured(Role.User)
	@Override
	public Profile getProfileByEmail(String emailAddress) {

		if (emailAddress != null) {
			String queryAddress = emailAddress.trim();
			try {
				Profile result = identityManagmentService.getProfileByEmail(queryAddress);
				securityPolicy.retrieve(result);
				return result;
			} catch (EntityNotFoundException e) {
				// ignore, we'll fall through and return null
				// FIXME for consistency, shouldn't we throw an EntityNotFoundException?
			}
		}
		return null;
	}

	private Profile privateGetProfileByEmail(String emailAddress) {
		if (emailAddress != null) {
			emailAddress = emailAddress.trim();
			if (emailAddress.length() > 0) {
				CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
				CriteriaQuery<Profile> query = criteriaBuilder.createQuery(Profile.class);
				Root<Profile> root = query.from(Profile.class);
				query.select(root).where(criteriaBuilder.equal(root.get("email"), emailAddress));
				try {
					Profile profile = entityManager.createQuery(query).getSingleResult();
					return profile;
				} catch (NoResultException e) {
					// ignore
				}
			}
		}
		return null;
	}

	@Secured(Role.User)
	@Override
	public Profile getProfileByUsername(String username) throws EntityNotFoundException {
		Profile profile = identityManagmentService.getProfileByUsername(username);
		securityPolicy.retrieve(profile);
		return profile;
	}

	@Secured(Role.User)
	@Override
	public Project createProject(Long profileId, Project project) throws ValidationException, EntityNotFoundException {

		// Before we start this createProject call, check to see if we have room for more projects in the system right
		// now
		if (!spaceAvailableForNewProject()) {
			// Construct and throw a new ValidationException
			Errors errors = createErrors(project);
			errors.reject("project.maxNumReached");
			throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
		}

		Organization associatedOrg = null;
		if (project.getOrganization() != null) {
			associatedOrg = entityManager.find(Organization.class, project.getOrganization().getId());
			if (associatedOrg == null) {
				throw new EntityNotFoundException();
			}
		} else if (TenancyUtil.getCurrentTenantOrganizationIdentifer() != null) {
			associatedOrg = getOrganizationByIdentfier(TenancyUtil.getCurrentTenantOrganizationIdentifer());
		}

		project.setOrganization(associatedOrg);
		project.computeIdentifier();

		securityPolicy.create(project);
		setDefaultValuesBeforeCreate(project);
		validate(project, validator, new ProjectConstraintsValidator());
		quotaService.enforceQuota(Quota.MAX_PROJECTS_QUOTA_NAME, project);

		Profile profile = getProfileInternal(profileId);
		securityPolicy.modify(profile);

		project.setId(null);
		entityManager.persist(project);
		entityManager.flush();

		if (associatedOrg != null) {
			associatedOrg.getProjects().add(project);
		}

		ProjectProfile projectProfile = project.addProfile(profile);
		// Mark project create as both project user and owner
		projectProfile.setUser(true);
		projectProfile.setOwner(true);

		entityManager.persist(projectProfile);
		entityManager.flush();

		try {
			projectServiceService.provisionDefaultServices(project.getId());
		} catch (ProvisioningException e) {
			throw new RuntimeException(e); // FIXME
		}

		return project;
	}

	@Override
	public Boolean isProjectCreateAvailable() {
		return spaceAvailableForNewProject();
	}

	private boolean spaceAvailableForNewProject() {
		try {
			ConfigurationProperty property = getConfigurationProperty(ConfigurationProperty.MAXNUM_PROJECTS_NAME);
			int maxNum = Integer.parseInt(property.getValue());
			int currentProjectCount = getEntityCount(Project.class);
			return (currentProjectCount < maxNum);
		} catch (EntityNotFoundException e) {
			// No configuration property available, there is no limit
			return true;
		}
	}

	private <T> int getEntityCount(Class<T> entityClass) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<T> root = query.from(entityClass);
		query.select(criteriaBuilder.count(root));
		return entityManager.createQuery(query).getSingleResult().intValue();
	}

	private ConfigurationProperty getConfigurationProperty(String name) throws EntityNotFoundException {
		return getEntityByField("name", name, ConfigurationProperty.class);
	}

	private <T> T getEntityByField(String fieldName, String fieldValue, Class<T> entityClass)
			throws EntityNotFoundException {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClass);
		Root<T> root = query.from(entityClass);
		query.select(root).where(criteriaBuilder.equal(root.get(fieldName), fieldValue));
		try {
			T retObj = entityManager.createQuery(query).getSingleResult();
			securityPolicy.retrieve(retObj);
			return retObj;
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		}
	}

	private void setDefaultValuesBeforeCreate(Project project) {
		if (project.getAccessibility() == null) {
			project.setAccessibility(ProjectAccessibility.PRIVATE);
		}
		if (project.getProjectPreferences() == null) {
			project.setProjectPreferences(createDefaultProjectPrefernces());
		}
	}

	private ProjectPreferences createDefaultProjectPrefernces() {
		ProjectPreferences result = new ProjectPreferences();
		result.setWikiLanguage(WikiMarkupLanguage.TEXTILE);
		return result;
	}

	@Secured(Role.User)
	@Override
	public Project updateProject(Project project) throws EntityNotFoundException, ValidationException {

		securityPolicy.modify(project);

		Project managedProject = entityManager.find(Project.class, project.getId());
		verifyProjectDeletion(managedProject);
		if (managedProject == null) {
			throw new EntityNotFoundException();
		}
		entityManager.refresh(managedProject);
		entityManager.refresh(managedProject.getProjectPreferences());

		validate(project, validator, new ProjectConstraintsValidator());

		if (project.getProjectPreferences().getWikiLanguage() != managedProject.getProjectPreferences()
				.getWikiLanguage()) {
			jobService.schedule(new UpdateProjectWikiPreferencesJob(project, WikiService.MARKUP_LANGUAGE_DB_KEY));
			jobService.schedule(new UpdateProjectTaskPreferencesJob(project, TaskService.MARKUP_LANGUAGE_DB_KEY));
		}

		if (!entityManager.contains(project)) {
			// we disallow change of identifier
			managedProject.setName(project.getName());
			managedProject.setDescription(project.getDescription());
			managedProject.setAccessibility(project.getAccessibility());
			managedProject.getProjectPreferences().setWikiLanguage(project.getProjectPreferences().getWikiLanguage());
		}

		return managedProject;
	}

	@Secured(Role.User)
	@Override
	public Project getProject(Long id) throws EntityNotFoundException {
		if (id != null) {
			Project project = entityManager.find(Project.class, id);
			verifyProjectDeletion(project);
			if (project != null) {

				securityPolicy.retrieve(project);

				return project;
			}
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Project getProjectByIdentifier(String identity) throws EntityNotFoundException {
		Project p = getProjectByIdentifierInternal(identity);
		verifyProjectDeletion(p);
		return p;
	}

	@Override
	public Project getProjectByIdentifierEvenIfDeleted(String identity) throws EntityNotFoundException {
		return getProjectByIdentifierInternal(identity);
	}

	private Project getProjectByIdentifierInternal(String identity) throws EntityNotFoundException {
		if (identity == null) {
			throw new IllegalArgumentException();
		}

		try {
			Project project = (Project) entityManager
					.createQuery("select a from " + Project.class.getSimpleName() + " a where a.identifier = :i")
					.setParameter("i", identity).getSingleResult();

			securityPolicy.retrieve(project);

			return project;
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		}
	}

	@Secured(Role.User)
	@Override
	public List<Project> getProfileProjects(Long profileId) throws EntityNotFoundException {
		Profile profile = getProfileInternal(profileId);
		if (profile != null) {
			securityPolicy.modify(profile);

			List<Project> projects = (List<Project>) entityManager
					.createQuery(
							"SELECT DISTINCT project FROM "
									+ Project.class.getSimpleName()
									+ " project, IN(project.projectProfiles) pp WHERE pp.profile.id = :id and project.deleted = false "
									+ createSortClause("project", Project.class, new SortInfo("name")))
					.setParameter("id", profile.getId()).getResultList();

			for (Project project : projects) {
				verifyProjectDeletion(project);
				securityPolicy.retrieve(project);
			}
			return projects;
		}
		return null;
	}

	@Override
	public void requestPasswordReset(String emailAddress) throws EntityNotFoundException {
		Profile profile = privateGetProfileByEmail(emailAddress);

		if (profile != null) {
			PasswordResetToken passwordResetToken = addPasswordResetToken(profile);
			String passwordResetURL = configuration.getProfilePasswordResetURL(passwordResetToken.getToken());
			emailPasswordResetMessage(profile, passwordResetURL);
		} else {
			throw new EntityNotFoundException();
		}
	}

	private void emailPasswordResetMessage(Profile profile, String passwordResetURL) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("passwordResetURL", passwordResetURL);
		model.put("AppName", configuration.getAppName());
		model.put("AppBaseUrl", configuration.getProfileBaseUrl());
		model.put("username", profile.getUsername());

		String bodyText = VelocityUtils.getLocalizedTemplateText(velocityEngine, passwordResetTemplate, model,
				AuthenticationServiceUser.getCurrentUserLanguage());
		Email email = new Email(profile.getEmail(), "Password Reset", bodyText, "text/plain");
		emailService.schedule(email);
	}

	private PasswordResetToken addPasswordResetToken(Profile profile) {
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setDateCreated(new Date());
		passwordResetToken.setProfile(profile);
		passwordResetToken.setToken(UUID.randomUUID().toString());
		passwordResetToken.setDateUsed(null);
		profile.getPasswordResetTokens().add(passwordResetToken);
		entityManager.persist(profile);
		return passwordResetToken;
	}

	// private PasswordResetToken getPassResetToken(String token) {
	// return getToken(token, PasswordResetToken.class);
	// }

	private InvitationToken getInvitationToken(String token) {
		return getToken(token, InvitationToken.class);
	}

	private EmailVerificationToken getEmailVerificationToken(String token) {
		return getToken(token, EmailVerificationToken.class);
	}

	@Override
	public String resetPassword(String token, String newPassword) throws EntityNotFoundException, ValidationException {
		SecurityContextHolder.getContext().setAuthentication(null);
		// load the token
		PasswordResetToken passwordResetToken = this.getPasswordResetToken(token);

		Profile profile = passwordResetToken.getProfile();
		if (profile == null) {
			// shouldn't ever have a detached token.
			throw new EntityNotFoundException();
		}

		// set the profile password, validate, and persist
		profile.setPassword(newPassword);
		validate(profile, validator, new ProfilePasswordValidator());
		profile.setPassword(passwordEncoder.encodePassword(newPassword, null));
		entityManager.persist(profile);

		// mark token as used
		passwordResetToken.setDateUsed(new Date());
		entityManager.persist(passwordResetToken);
		return profile.getUsername();
	}

	@Secured(Role.User)
	@Override
	public String inviteUserForProject(String email, String appIdentifier) throws EntityNotFoundException {

		// Pull in our domain objects now.
		Project project = getProjectByIdentifier(appIdentifier);
		Profile profile = getCurrentUserProfile();

		// Pre-populate a new Invitation token.
		InvitationToken token = new InvitationToken();
		token.setDateCreated(new Date());
		token.setProject(project);
		token.setIssuingProfile(profile);
		token.setToken(UUID.randomUUID().toString());
		token.setEmail(email);

		securityPolicy.create(token);

		// Do our persist before our invite, so that an issue in sending the email doesn't prevent the invitation itself
		// from existing in the system.
		entityManager.persist(token);

		// Send an email to this user now.
		emailProjectInvitationMessage(token);
		return token.getToken();
	}

	private void emailProjectInvitationMessage(InvitationToken token) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("profile", token.getIssuingProfile());
		model.put("project", token.getProject());
		model.put("invitePageUrl", configuration.getInvitationURL(token.getToken()));
		model.put("AppName", configuration.getAppName());

		String bodyText = VelocityUtils.getLocalizedTemplateText(velocityEngine, projectInvitationTemplate, model,
				AuthenticationServiceUser.getCurrentUserLanguage());
		Email email = new Email(token.getEmail(), token.getIssuingProfile().getFirstName()
				+ " has invited you to a project on " + configuration.getAppName(), bodyText, "text/plain");
		emailService.schedule(email);
	}

	@SuppressWarnings("unchecked")
	private <T extends RandomToken> T getToken(String token, Class<T> tokenType) {

		if (token == null || token.trim().length() == 0) {
			return null;
		}

		String queryStr = String.format("select t from %s t where t.token = :tokenStr", tokenType.getSimpleName());
		try {
			return (T) entityManager.createQuery(queryStr).setParameter("tokenStr", token.trim()).getSingleResult();
		} catch (NoResultException nre) {
			// Catch this and return null.
			return null;
		}
	}

	@Override
	public Project getProjectForInvitationToken(String invitationToken) throws EntityNotFoundException {

		// Get the associated token now.
		InvitationToken token = getInvitationToken(invitationToken);

		// If this token is either missing or already consumed, throw an exception indicating that it doesn't exist.
		if (token == null || token.getDateUsed() != null) {
			throw new EntityNotFoundException();
		}

		verifyProjectDeletion(token.getProject());
		return token.getProject();
	}

	@Secured(Role.User)
	@Override
	public void acceptInvitation(String invitationToken) throws EntityNotFoundException {

		// Get the associated token now.
		InvitationToken token = getInvitationToken(invitationToken);

		// If this token is either missing or already consumed, throw an exception indicating that it doesn't exist.
		if (token == null || token.getDateUsed() != null) {
			throw new EntityNotFoundException();
		}

		// Pull in the project, and make our link now.
		Profile profile = getCurrentUserProfile();
		Project project = token.getProject();
		addProjectProfileInternal(project, profile);

		// Mark the date and save out our changes now.
		token.setDateUsed(new Date());
		entityManager.persist(token);
	}

	@Secured(Role.User)
	@Override
	public void sendVerificationEmail() {
		Profile profile = getCurrentUserProfile();
		sendVerificationEmail(profile);
	}

	/**
	 * @param profile
	 */
	private void sendVerificationEmail(Profile profile) {
		if (profile.getEmailVerified()) {
			return;
		}

		EmailVerificationToken token = new EmailVerificationToken();
		token.setProfile(profile);
		token.setEmail(profile.getEmail());
		token.setToken(UUID.randomUUID().toString());
		token.setDateCreated(new Date());
		token.setDateUsed(null);

		entityManager.persist(token);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("token", token);
		model.put("profile", profile);
		model.put("AppName", configuration.getAppName());
		model.put("AppBaseUrl", configuration.getProfileBaseUrl());
		model.put("URL", configuration.getEmailVerificationURL(token.getToken()));
		String bodyText = VelocityUtils.getLocalizedTemplateText(velocityEngine, emailVerificationTemplate, model,
				AuthenticationServiceUser.getCurrentUserLanguage());
		Email email = new Email(token.getEmail(), "Verify your " + configuration.getAppName() + " email", bodyText,
				"text/plain");
		emailService.schedule(email);
	}

	@Secured(Role.User)
	@Override
	public void verifyEmail(String emailToken) throws EntityNotFoundException, ValidationException {

		// Get the associated token now.
		EmailVerificationToken token = getEmailVerificationToken(emailToken);

		// If this token is either missing or already consumed, throw an exception indicating that it doesn't exist.
		if (token == null || token.getDateUsed() != null) {
			throw new EntityNotFoundException();
		}

		// Pull in the project, and make our link now.
		Profile profile = getCurrentUserProfile();

		if (!profile.equals(token.getProfile())) {
			Errors errors = createErrors(token);
			errors.reject("email.verify.wrongProfile");
			throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
		}

		if (!token.getProfile().getEmail().equals(token.getEmail())) {
			Errors errors = createErrors(emailToken);
			errors.reject("email.verify.oldEmail");
			throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
		}

		profile.setEmailVerified(true);

		if (!profile.getSentWelcomeEmail()) {
			sendWelcomEmail(profile);
			profile.setSentWelcomeEmail(true);
		}

		// Mark the date and save out our changes now.
		token.setDateUsed(new Date());
		entityManager.persist(token);
	}

	@Secured(Role.User)
	@Override
	public QueryResult<Profile> findProfiles(String queryText, Region region, SortInfo sortInfo) {
		if (region == null) {
			region = new Region(0, 100);
		} else if (region.getSize() > MAX_SIZE) {
			region.setSize(MAX_SIZE);
		}
		QueryResult<Profile> result = identityManagmentService.findProfiles(queryText, region, sortInfo);

		for (Profile profile : result.getResultPage()) {
			securityPolicy.retrieve(profile);
		}
		return result;
	}

	static String createSortClause(String entityAlias, Class<? extends BaseEntity> entity, SortInfo... sortInfos) {
		String sql = "order by";
		if (sortInfos != null) {
			int count = 0;
			for (SortInfo sortInfo : sortInfos) {
				if (sortInfo == null) {
					continue;
				}
				if (hasPersistentField(entity, sortInfo.getSortField())) {
					sql += (count++ > 0 ? ", " : " ") + "LOWER(" + entityAlias + "." + sortInfo.getSortField() + ")";
					if (sortInfo.getSortOrder() == SortInfo.Order.DESCENDING) {
						sql += " desc";
					}
				}
			}
		}
		return sql;
	}

	private static boolean hasPersistentField(Class<? extends BaseEntity> entity, String fieldName) {
		Class<?> clazz = entity;
		while (clazz != Object.class) {
			try {
				clazz.getDeclaredField(fieldName);
				return true;
			} catch (Exception e) {
				// expected
			}
			clazz = clazz.getSuperclass();
		}
		return false;
	}

	private ProjectProfile addProjectProfileInternal(Project project, Profile profile) {
		List<ProjectProfile> projectProfiles = project.getProjectProfiles();
		ProjectProfile projectProfile = null;
		for (ProjectProfile curProjectProfile : projectProfiles) {
			if (curProjectProfile.getProfile().equals(profile)) {
				projectProfile = curProjectProfile;

				// If we're already marked as a user, then bail out now
				if (curProjectProfile.getUser()) {
					return curProjectProfile;
				} else {
					break;
				}
			}
		}

		// No pre-existing project profile? Create one now.
		if (projectProfile == null) {
			projectProfile = new ProjectProfile();
			projectProfile.setProject(project);
			project.getProjectProfiles().add(projectProfile);
			projectProfile.setProfile(profile);
			profile.getProjectProfiles().add(projectProfile);

			jobService.schedule(new ReplicateProjectProfileJob(project, profile.getId(),
					ReplicationScope.CREATE_OR_UPDATE));
		}

		// Mark this profile as a user of the project.
		projectProfile.setUser(true);

		if (projectProfile.getId() == null) {
			// If this is new, save it now.
			entityManager.persist(projectProfile);
		}
		return projectProfile;

	}

	@Secured(Role.User)
	@Override
	public void watchProject(String projectIdentifier) throws EntityNotFoundException {
		Project project = getProjectByIdentifier(projectIdentifier);

		// Only allow watching of a public project - if a private project is given, then pretend it doesn't exist to
		// prevent information leakage.
		if (!ProjectAccessibility.PUBLIC.equals(project.getAccessibility())) {
			throw new EntityNotFoundException();
		}

		Profile currentUser = getCurrentUserProfile();

		// Check to see if we're currently in the set of users for this project.
		for (ProjectProfile projectProfile : project.getProjectProfiles()) {
			if (currentUser.equals(projectProfile.getProfile())) {
				// We have a link already, ensure it is watched.
				if (!projectProfile.getCommunity()) {
					projectProfile.setCommunity(true);
					entityManager.persist(projectProfile);
				}
				return;
			}
		}

		// No pre-existing link? Create one now.
		ProjectProfile newProjectProfile = new ProjectProfile();
		newProjectProfile.setProject(project);
		project.getProjectProfiles().add(newProjectProfile);
		newProjectProfile.setProfile(currentUser);
		currentUser.getProjectProfiles().add(newProjectProfile);

		// Mark this profile as a watcher of the project.
		newProjectProfile.setCommunity(true);
		entityManager.persist(newProjectProfile);
	}

	@Secured(Role.User)
	@Override
	public void unwatchProject(String projectIdentifier) throws EntityNotFoundException {
		Project project = getProjectByIdentifier(projectIdentifier);

		Profile currentUser = getCurrentUserProfile();

		// If there is a projectProfile with the community role set it to false.
		for (ProjectProfile projectProfile : project.getProjectProfiles()) {
			if (currentUser.equals(projectProfile.getProfile())) {
				// remove the community role
				projectProfile.setCommunity(false);

				// if no roles are left remove the projectProfile
				if (!projectProfile.hasAnyRoles()) {
					currentUser.getProjectProfiles().remove(projectProfile);
					entityManager.remove(projectProfile);
				}
			}
		}
	}

	@Secured(Role.User)
	@Override
	public void removeProjectProfile(Long projectId, Long profileId) throws EntityNotFoundException,
			ValidationException {
		Project project = getProject(projectId);
		Profile profile = getProfileInternal(profileId);

		securityPolicy.remove(project, profile, "projectProfiles");

		ProjectProfile profileToRemove = null;
		List<ProjectProfile> projectProfiles = project.getProjectProfiles();
		if (projectProfiles.size() == 1) {
			// every project must have at least one member
			Errors errors = createErrors(project);
			errors.reject("project.mustHaveMembers", null,
					"Cannot remove member: an project must have at least one member");
			throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
		}

		int ownerCount = 0;
		for (ProjectProfile projectProfile : projectProfiles) {
			if (projectProfile.getOwner()) {
				++ownerCount;
			}
			if (projectProfile.getProfile().equals(profile)) {
				profileToRemove = projectProfile;
			}
		}
		if (profileToRemove == null) {
			throw new EntityNotFoundException();
		}
		if (ownerCount == 1 && profileToRemove.getOwner()) {
			// every project must have at least one owner
			Errors errors = createErrors(project);
			errors.reject("project.mustHaveOwner", null, "Cannot remove owner: an project must have at least one owner");
			throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
		}

		project.getProjectProfiles().remove(profileToRemove);
		profile.getProjectProfiles().remove(profileToRemove);
		entityManager.remove(profileToRemove);
	}

	@Override
	public Profile getCurrentUserProfile() {
		String currentUser = Security.getCurrentUser();

		if (currentUser != null) {
			try {
				return identityManagmentService.getProfileByUsername(currentUser);
			} catch (EntityNotFoundException e) {
				// expected
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Secured({ Role.User, Role.UserWithPendingAgreements })
	@Override
	public List<Agreement> getPendingAgreements() throws EntityNotFoundException {

		List<Agreement> pendingAgreements = new ArrayList<Agreement>();
		Profile profile = getCurrentUserProfile();

		Query q = entityManager.createQuery(
				"select a from " + Agreement.class.getSimpleName() + " a where a.active = :active").setParameter(
				"active", true);

		List<Agreement> activeAgreements = q.getResultList();

		Query q2 = entityManager.createQuery(
				"select ap.agreement from " + AgreementProfile.class.getSimpleName()
						+ " ap where ap.profile = :profile").setParameter("profile", profile);

		List<Agreement> agreedAgreements = q2.getResultList();

		for (Agreement agreement : activeAgreements) {
			if (agreedAgreements.indexOf(agreement) == -1) {
				pendingAgreements.add(agreement);
			}
		}

		return pendingAgreements;
	}

	@Secured({ Role.User, Role.UserWithPendingAgreements })
	@Override
	public void approveAgreement(Long agreementId) throws EntityNotFoundException {

		Profile profile = getCurrentUserProfile();

		Agreement agreement = entityManager.find(Agreement.class, agreementId);

		AgreementProfile ap = new AgreementProfile();
		ap.setProfile(profile);
		ap.setAgreement(agreement);
		ap.setDateAgreed(new Date());
		// securityPolicy.create(ap);

		entityManager.persist(ap);
	}

	@Secured(Role.User)
	@Override
	public List<AgreementProfile> getApprovedAgreementProfiles() throws EntityNotFoundException {
		Profile profile = getCurrentUserProfile();

		Query q = entityManager.createQuery(
				"select ap from " + AgreementProfile.class.getSimpleName() + " ap where ap.profile = :profile")
				.setParameter("profile", profile);

		@SuppressWarnings("unchecked")
		List<AgreementProfile> approvedAgreementProfiles = q.getResultList();

		return approvedAgreementProfiles;
	}

	@SuppressWarnings("unchecked")
	private QueryResult<Project> findProjects(String queryText, String orgIdentifierOrNull, QueryRequest queryRequest) {
		SortInfo sortInfo = queryRequest == null ? null : queryRequest.getSortInfo();
		Region region = queryRequest == null ? null : queryRequest.getPageInfo();

		Profile profile;
		try {
			profile = Security.getCurrentUser() == null ? null : getProfileByUsername(Security.getCurrentUser());
		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e); // Should never happen.
		}

		queryText = queryText == null ? "" : queryText.trim();
		queryText = queryText.toLowerCase();
		queryText = queryText.length() > 0 ? "%" + queryText + "%" : "%";

		String coreQuery = "FROM " + Project.class.getSimpleName() + " project ";

		if (profile != null) {
			coreQuery += ", IN(project.projectProfiles) pp ";
		}
		coreQuery += "WHERE (LOWER(project.name) LIKE :q OR LOWER(project.description) LIKE :q OR LOWER(project.identifier) LIKE :q) AND (project.accessibility = :public ";

		if (profile != null) {
			coreQuery += " OR pp.profile.id = :id";
		}

		coreQuery += ")  AND project.deleted = false";

		if (orgIdentifierOrNull != null) {
			coreQuery += " AND project.organization.identifier =  :orgId";
		}

		Query query = entityManager.createQuery("SELECT distinct project " + coreQuery + " "
				+ createSortClause("project", Project.class, sortInfo, new SortInfo("name")));

		query.setParameter("q", queryText.trim());
		query.setParameter("public", ProjectAccessibility.PUBLIC);
		if (profile != null) {
			query.setParameter("id", profile.getId());
		}
		if (orgIdentifierOrNull != null) {
			query.setParameter("orgId", orgIdentifierOrNull);
		}

		if (region == null) {
			region = new Region(0, 100);
		} else if (region.getSize() > MAX_SIZE) {
			region.setSize(MAX_SIZE);
		}
		query.setFirstResult(region.getOffset());
		query.setMaxResults(region.getSize());

		Query countQuery = entityManager.createQuery("select count(distinct project) " + coreQuery)
				.setParameter("q", queryText).setParameter("public", ProjectAccessibility.PUBLIC);
		if (profile != null) {
			countQuery.setParameter("id", profile.getId());
		}
		if (orgIdentifierOrNull != null) {
			countQuery.setParameter("orgId", orgIdentifierOrNull);
		}

		Long totalSize = (Long) countQuery.getSingleResult();

		List<Project> results = query.getResultList();
		for (Project project : results) {
			securityPolicy.retrieve(project);
		}

		return new QueryResult<Project>(region, results, totalSize.intValue());
	}

	@Secured(Role.User)
	@Override
	public Boolean isWatchingProject(String projectIdentifier) throws EntityNotFoundException {
		Profile profile = getCurrentUserProfile();
		Project project = getProjectByIdentifier(projectIdentifier);

		Query q = entityManager.createQuery("select count(pp) from " + ProjectProfile.class.getSimpleName()
				+ " pp where pp.profile = :profile AND pp.project = :project AND pp.community = true");
		q.setParameter("profile", profile);
		q.setParameter("project", project);
		Long count = (Long) q.getSingleResult();
		if (count > 0)
			return true;
		return false;
	}

	@Override
	public SignUpToken getSignUpToken(String signUpToken) throws EntityNotFoundException {
		SignUpToken dbToken = getToken(signUpToken, SignUpToken.class);
		// We want to return true if we have a token with an empty used-date.
		if (dbToken != null && dbToken.getDateUsed() == null) {
			return dbToken;
		}
		throw new EntityNotFoundException();
	}

	@Override
	public PasswordResetToken getPasswordResetToken(String token) throws EntityNotFoundException {
		PasswordResetToken dbToken = getToken(token, PasswordResetToken.class);
		// We want to return true if we have a token with an empty used-date.
		if (dbToken != null && dbToken.getDateUsed() == null) {
			return dbToken;
		}
		throw new EntityNotFoundException();
	}

	@Override
	public void markSignUpTokenUsed(String token) throws EntityNotFoundException {
		SignUpToken dbToken = getToken(token, SignUpToken.class);
		if (dbToken != null && dbToken.getDateUsed() == null) {
			dbToken.setDateUsed(new Date());
			entityManager.persist(dbToken);
			return;
		}
		throw new EntityNotFoundException();
	}

	@Override
	public InvitationToken getProjectInvitationToken(String projectInvitationToken) throws EntityNotFoundException {
		InvitationToken dbToken = getToken(projectInvitationToken, InvitationToken.class);
		// We want to return true if we have a token with an empty used-date.
		if (dbToken != null && dbToken.getDateUsed() == null) {
			return dbToken;
		}
		throw new EntityNotFoundException();
	}

	@Secured(Role.Admin)
	public SignUpToken createSignUpToken(com.tasktop.c2c.server.profile.domain.project.SignUpToken token)
			throws ValidationException {
		return createSignUpToken(token.getFirstname(), token.getLastname(), token.getEmail());
	}

	@Secured(Role.Admin)
	@Override
	public SignUpToken createSignUpToken(String firstName, String lastName, String email) throws ValidationException {
		SignUpToken token = new SignUpToken();
		token.setFirstname(firstName);
		token.setLastname(lastName);
		token.setEmail(email);
		token.setToken(UUID.randomUUID().toString());
		token.setDateCreated(new Date());
		token.setDateUsed(null);

		validate(token, validator);

		entityManager.persist(token);
		entityManager.flush();
		return token;
	}

	@Secured(Role.Admin)
	@Override
	@SuppressWarnings("unchecked")
	public List<SignUpToken> getUnusedSignUpTokens() {
		Query query = entityManager.createQuery("select t from " + SignUpToken.class.getSimpleName()
				+ " t where t.dateUsed is null");
		return (List<SignUpToken>) query.getResultList();
	}

	@Secured(Role.Admin)
	@Override
	public void sendSignUpInvitation(String email) throws EntityNotFoundException {
		SignUpToken token = null;
		try {
			// find the first token that matches this email and is unused
			Query q = entityManager.createQuery("select t from " + SignUpToken.class.getSimpleName()
					+ " t where t.dateUsed is null and t.email = :email");
			q.setParameter("email", email);
			token = (SignUpToken) q.getSingleResult();
		} catch (NoResultException e) {
			// no token found
			throw new EntityNotFoundException();
		}

		// check for existing profiles with that email, we don't want to allow creation of another one.
		Profile profile = privateGetProfileByEmail(email);
		if (profile != null) {
			// if exists throw exception
			throw new EntityNotFoundException();
		}

		// send email
		emailSignUpInvitationMessage(token);
	}

	private void emailSignUpInvitationMessage(SignUpToken token) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("firstName", token.getFirstname());
		model.put("lastName", token.getLastname());
		model.put("URL", configuration.getSignUpInvitationURL(token.getToken()));
		model.put("AppName", configuration.getAppName());
		model.put("AppBaseUrl", configuration.getProfileBaseUrl());
		model.put("UpdateSite", configuration.getUpdateSiteUrl());
		String bodyText = VelocityUtils.getLocalizedTemplateText(velocityEngine, signUpInvitationTemplate, model,
				AuthenticationServiceUser.getCurrentUserLanguage());
		Email email = new Email(token.getEmail(), "You have been invited to join " + configuration.getAppName(),
				bodyText, "text/plain");
		emailService.schedule(email);

		// Task 2376: send an invite to info@code.cloudfoundry.com on system invite, containing invite info
		String notificationEmailAddress = configuration.getSignupNotificationEmail();
		if (notificationEmailAddress != null && !notificationEmailAddress.isEmpty()) {
			String secondBodyText = String.format(
					"first name: %s\nlast name: %s\n email: %s\n token: %s\n\nSent by: %s", token.getFirstname(),
					token.getLastname(), token.getEmail(), token.getToken(), getCurrentUserProfile().getFullName());
			Email secondEmail = new Email(notificationEmailAddress, "System invitation to " + token.getEmail()
					+ " sent for " + configuration.getWebHost(), secondBodyText, "text/plain");
			emailService.schedule(secondEmail);
		}
	}

	@Secured(Role.System)
	@Override
	public void replicateProjectProfile(Long profileId, Long projectId, ReplicationScope scope)
			throws EntityNotFoundException {
		Project project = projectId == null ? null : entityManager.find(Project.class, projectId);
		Profile profile = profileId == null ? null : entityManager.find(Profile.class, profileId);
		if (project == null || profile == null) {
			throw new EntityNotFoundException();
		}
		if (project.getProjectServiceProfile() == null) {
			return;
		}
		for (ProjectService projectService : project.getProjectServiceProfile().getProjectServices()) {
			if (projectService.getServiceHost() != null && projectService.getServiceHost().isAvailable()) {
				switch (projectService.getType()) {
				case TASKS: {
					TaskUserProfile taskUserProfile = new TaskUserProfile();
					taskUserProfile.setLoginName(profile.getUsername());
					taskUserProfile.setRealname(profile.getFullName());
					taskUserProfile.setGravatarHash(profile.getGravatarHash());
					taskServiceProvider.getTaskService(project.getIdentifier())
							.replicateProfile(taskUserProfile, scope);
					break;
				}
				case WIKI: {
					Person person = new Person();
					person.setLoginName(profile.getUsername());
					person.setName(profile.getFullName());
					wikiServiceProvider.getWikiService(project.getIdentifier()).replicateProfile(person, scope);
					break;
				}
				default:
					break; // do nothing for other service types
				}
			}
		}
	}

	@Override
	@Transactional(noRollbackFor = { EntityNotFoundException.class })
	public RandomToken getSignUpOrProjectInvitationToken(String token) throws EntityNotFoundException {

		RandomToken dbToken = null;
		try {
			// will not rollback transaction!!
			dbToken = getSignUpToken(token);
		} catch (EntityNotFoundException e) {
			// will throw EntityNotFoundException if not found.
			dbToken = getProjectInvitationToken(token);
		}
		return dbToken;
	}

	@Secured(Role.Admin)
	@Override
	public SignUpTokens createInvitations(SignUpTokens invitationTokens, boolean sendEmail) throws ValidationException {
		SignUpTokens result = new SignUpTokens();

		List<SignUpToken> newTokens = new ArrayList<SignUpToken>(invitationTokens.getTokens().size());

		for (com.tasktop.c2c.server.profile.domain.project.SignUpToken token : invitationTokens.getTokens()) {
			SignUpToken signUpToken = createSignUpToken(token);
			newTokens.add(signUpToken);
			result.add(webServiceDomain.copy(signUpToken, configuration));
		}
		if (sendEmail) {
			for (SignUpToken token : newTokens) {
				emailSignUpInvitationMessage(token);
			}
		}

		return result;
	}

	@Secured(Role.Admin)
	@Override
	@SuppressWarnings("unchecked")
	public List<Profile> listAllProfiles() {
		return entityManager.createQuery("select p from " + Profile.class.getSimpleName() + " p").getResultList();
	}

	@Secured(Role.User)
	@Override
	public SshPublicKey createSshPublicKey(SshPublicKeySpec keySpec) throws ValidationException {
		validate(keySpec);

		SshPublicKey sshPublicKey = publicKeyReader.readPublicKey(keySpec.getKeyData());
		if (sshPublicKey == null) {
			Errors errors = createErrors(keySpec);
			errors.reject("invalidKeyFormat");
			throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
		}
		sshPublicKey.setName(keySpec.getName());
		return createSshPublicKey(sshPublicKey);
	}

	@Secured(Role.User)
	@Override
	public SshPublicKey createSshPublicKey(SshPublicKey publicKey) throws ValidationException {
		SshPublicKey managedKey = new SshPublicKey();
		managedKey.setName(publicKey.getName());
		managedKey.setAlgorithm(publicKey.getAlgorithm());
		managedKey.setKeyData(publicKey.getKeyData());

		validate(managedKey);

		managedKey.computeFingerprint();

		Profile profile = getCurrentUserProfile();

		profile.getSshPublicKeys().add(managedKey);
		managedKey.setProfile(profile);

		entityManager.persist(managedKey);
		entityManager.flush();

		return managedKey;
	}

	@Secured(Role.User)
	@Override
	public SshPublicKey updateSshPublicKey(SshPublicKey publicKey) throws ValidationException {
		SshPublicKey managedKey = entityManager.find(SshPublicKey.class, publicKey.getId());
		managedKey.setName(publicKey.getName());
		validate(managedKey);

		managedKey.computeFingerprint();

		return managedKey;
	}

	@Secured(Role.User)
	@Override
	public List<SshPublicKey> listSshPublicKeys() {
		Profile profile = getCurrentUserProfile();
		return new ArrayList<SshPublicKey>(profile.getSshPublicKeys());
	}

	@Secured(Role.User)
	@Override
	public void removeSshPublicKey(Long publicKeyId) throws EntityNotFoundException {
		Profile profile = getCurrentUserProfile();

		SshPublicKey sshPublicKey = entityManager.find(SshPublicKey.class, publicKeyId);
		if (sshPublicKey == null || !sshPublicKey.getProfile().equals(profile)) {
			throw new EntityNotFoundException();
		}
		profile.getSshPublicKeys().remove(sshPublicKey);
		entityManager.remove(sshPublicKey);
		entityManager.flush();
	}

	private QueryResult<Project> findProjects(ProjectRelationship projectRelationship, String orgIdentifierOrNull,
			QueryRequest queryRequest) {

		Profile profile;
		try {
			profile = Security.getCurrentUser() == null ? null : getProfileByUsername(Security.getCurrentUser());
		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e); // Should never happen.
		}

		if (profile == null && ProjectRelationship.ALL.equals(projectRelationship)) {
			projectRelationship = ProjectRelationship.PUBLIC; // All mean public for anon-user
		}

		if (profile == null && !ProjectRelationship.PUBLIC.equals(projectRelationship)) {
			throw new InsufficientPermissionsException("Need to be logged in for this query");
		}

		String fromString = "FROM " + Project.class.getSimpleName() + " project, IN(project.projectProfiles) pp ";
		StringBuilder whereStringBldr = new StringBuilder();
		boolean needIdParam = true;
		boolean needPubParam = false;
		boolean needPrivParam = false;
		whereStringBldr.append("WHERE project.deleted = false AND ");
		switch (projectRelationship) {
		case ALL:
			// include projects with org private accessibility and current user is a member of an org associated with
			// the project
			whereStringBldr
					.append("(project.accessibility = :public OR (pp.profile.id = :id AND (pp.user = true OR pp.owner = true OR (pp.community = true AND project.accessibility = :public) ))");
			if (orgIdentifierOrNull != null) {
				whereStringBldr.append(" OR project.accessibility = :organizationPrivate");
				needPrivParam = true;
			}
			whereStringBldr.append(") ");
			needPubParam = true;
			if (orgIdentifierOrNull == null) {
				// include org-private projects with the same organization id as one the current user is associated with

				// use the security context to find this user's organization id(s) in case this is held in something
				// other than a database
				List<String> orgIdsForUser = AuthUtils.findOrganizationIdsForCurrentUser();
				if (orgIdsForUser.size() > 0) {
					whereStringBldr.append("OR (project.id IN (SELECT innerQueryProject.id FROM "
							+ Project.class.getSimpleName()
							+ " innerQueryProject WHERE innerQueryProject.accessibility = :organizationPrivate AND (");
					Iterator<String> it = orgIdsForUser.iterator();
					while (it.hasNext()) {
						whereStringBldr.append("innerQueryProject.organization.identifier = '").append(it.next())
								.append("' ");
						if (it.hasNext()) {
							whereStringBldr.append("OR ");
						}
					}
					whereStringBldr.append("))) ");
					needPrivParam = true;
				}
			}
			break;
		case MEMBER:
			whereStringBldr.append("pp.profile.id = :id AND pp.user = true ");
			break;
		case OWNER:
			whereStringBldr.append("pp.profile.id = :id AND pp.owner = true ");
			break;
		case ORGANIZATION_PRIVATE: // requires an organization
			if (orgIdentifierOrNull == null) {
				throw new IllegalArgumentException();
			}
			whereStringBldr.append("project.accessibility = :organizationPrivate ");
			needPrivParam = true;
			needIdParam = false;
			break;
		case WATCHER:
			whereStringBldr.append("pp.profile.id = :id AND pp.community = true AND project.accessibility = :public ");
			needPubParam = true;
			break;
		case PUBLIC:
			needIdParam = false;
			needPubParam = true;
			whereStringBldr.append("project.accessibility = :public ");
			break;
		default:
			throw new IllegalStateException();
		}

		if (orgIdentifierOrNull != null) {
			whereStringBldr.append(" AND project.organization.identifier = :orgId ");
		}

		Query totalResultQuery = entityManager.createQuery("SELECT count(DISTINCT project) " + fromString
				+ whereStringBldr.toString());
		if (needPubParam) {
			totalResultQuery.setParameter("public", ProjectAccessibility.PUBLIC);
		}
		if (needPrivParam) {
			totalResultQuery.setParameter("organizationPrivate", ProjectAccessibility.ORGANIZATION_PRIVATE);
		}
		if (needIdParam) {
			totalResultQuery.setParameter("id", profile.getId());
		}

		if (orgIdentifierOrNull != null) {
			totalResultQuery.setParameter("orgId", orgIdentifierOrNull);
		}
		int totalResultSize = ((Long) totalResultQuery.getSingleResult()).intValue();

		Query q = entityManager.createQuery("SELECT DISTINCT project " + fromString + whereStringBldr.toString()
				+ createSortClause("project", Project.class, new SortInfo("name")));
		if (needPubParam) {
			q.setParameter("public", ProjectAccessibility.PUBLIC);
		}
		if (needPrivParam) {
			q.setParameter("organizationPrivate", ProjectAccessibility.ORGANIZATION_PRIVATE);
		}
		if (needIdParam) {
			q.setParameter("id", profile.getId());
		}
		if (orgIdentifierOrNull != null) {
			q.setParameter("orgId", orgIdentifierOrNull);
		}

		if (queryRequest != null && queryRequest.getPageInfo() != null) {
			q.setFirstResult(queryRequest.getPageInfo().getOffset());
			q.setMaxResults(queryRequest.getPageInfo().getSize());
		}

		List<Project> projects = q.getResultList();

		for (Project project : projects) {
			securityPolicy.retrieve(project);
		}

		Region region;
		if (queryRequest == null || queryRequest.getPageInfo() == null) {
			region = new Region(0, Integer.MAX_VALUE);
		} else {
			region = queryRequest.getPageInfo();
		}
		return new QueryResult<Project>(region, projects, totalResultSize);

	}

	private final class OrganizationConstraintsValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			return Project.class.isAssignableFrom(clazz);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void validate(Object target, Errors errors) {
			Organization organization = (Organization) target;
			if (organization.getIdentifier() != null && organization.getIdentifier().trim().length() > 0) {
				List<Organization> orgsWithIdentifier = entityManager
						.createQuery(
								"select e from " + Organization.class.getSimpleName()
										+ " e where e.identifier = :identifier")
						.setParameter("identifier", organization.getIdentifier()).getResultList();
				if (!orgsWithIdentifier.isEmpty()) {
					if (orgsWithIdentifier.size() != 1 || !orgsWithIdentifier.get(0).equals(organization)) {
						errors.reject("organization.identifierUnique", new Object[] { organization.getIdentifier() },
								null);
					}
				}
			}
		}

	}

	@Secured(Role.User)
	@Override
	public Organization createOrganization(Organization org) throws ValidationException {

		// Compute identifer
		if (org.getIdentifier() == null) {
			org.computeIdentifier();
		}

		if (org.getProjectPreferences() == null) {
			org.setProjectPreferences(createDefaultProjectPrefernces());
		}

		securityPolicy.create(org);
		validate(org, validator, new OrganizationConstraintsValidator());

		entityManager.persist(org);

		return org;
	}

	@Override
	public Organization getOrganizationByIdentfier(String orgIdentifier) throws EntityNotFoundException {
		if (orgIdentifier == null) {
			throw new IllegalArgumentException();
		}

		try {
			Organization org = (Organization) entityManager
					.createQuery("select a from " + Organization.class.getSimpleName() + " a where a.identifier = :i")
					.setParameter("i", orgIdentifier).getSingleResult();

			securityPolicy.retrieve(org);

			return org;
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		}
	}

	@Override
	public QueryResult<Project> findProjects(ProjectsQuery query) {
		if (query.getQueryString() != null) {
			return findProjects(query.getQueryString(), query.getOrganizationIdentifier(), query.getQueryRequest());
		} else if (query.getProjectRelationship() != null) {
			return findProjects(query.getProjectRelationship(), query.getOrganizationIdentifier(),
					query.getQueryRequest());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void deleteProject(String projectIdentifier) throws EntityNotFoundException {
		Project project = getProjectByIdentifier(projectIdentifier);
		project.setIsDeleted(true);
		securityPolicy.delete(project);

		if (project.getProjectServiceProfile() == null
				|| project.getProjectServiceProfile().getProjectServices().isEmpty()) {
			doDeleteProject(projectIdentifier);
		} else {
			for (ProjectService service : project.getProjectServiceProfile().getProjectServices()) {
				jobService.schedule(new ProjectServiceDeprovisioningJob(project.getIdentifier(), service.getId()));
			}
		}
	}

	@Override
	public void doDeleteProjectIfReady(String projectIdentifier) throws EntityNotFoundException {

		Project project = getProjectByIdentifierInternal(projectIdentifier);
		boolean readyToDelete = true;
		for (ProjectService projectService : project.getProjectServiceProfile().getProjectServices()) {
			if (projectService.getServiceHost() != null) {
				readyToDelete = false;
				break;
			}
		}

		LOG.debug(String.format("We have [%s] service for project [%s], and %s ready to delete", project
				.getProjectServiceProfile().getProjectServices().size(), projectIdentifier, readyToDelete ? "are"
				: "are not"));
		if (readyToDelete) {
			doDeleteProject(projectIdentifier);
		}
	}

	@Override
	public void doDeleteProject(String projectIdentifier) throws EntityNotFoundException {
		Project project = getProjectByIdentifierInternal(projectIdentifier);
		entityManager.refresh(project);
		entityManager.remove(project);
	}

	@Override
	public List<Organization> getOwnedOrganizations() {
		List<Organization> orgs = new LinkedList<Organization>();
		List<OrganizationProfile> orgProfiles = entityManager
				.createQuery(
						"select e from " + OrganizationProfile.class.getSimpleName()
								+ " e where e.profile.id = :profileId")
				.setParameter("profileId", getCurrentUserProfile().getId()).getResultList();

		for (OrganizationProfile orgProfile : orgProfiles) {
			orgs.add(orgProfile.getOrganization());
		}

		return orgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tasktop.c2c.server.profile.service.ProfileService#updateOrganization(com.tasktop.c2c.server.profile.domain
	 * .project.Organization)
	 */
	@Secured(Role.User)
	@Override
	public Organization updateOrganization(Organization organization) throws EntityNotFoundException,
			ValidationException {

		securityPolicy.modify(organization);

		Organization managedOrganization = entityManager.find(Organization.class, organization.getId());
		if (managedOrganization == null) {
			throw new EntityNotFoundException();
		}
		entityManager.refresh(managedOrganization);
		entityManager.refresh(managedOrganization.getProjectPreferences());

		validate(managedOrganization, validator, new OrganizationConstraintsValidator());

		if (!entityManager.contains(organization)) {
			// we disallow change of identifier
			managedOrganization.setName(organization.getName());
			managedOrganization.setDescription(organization.getDescription());
			managedOrganization.getProjectPreferences().setWikiLanguage(
					organization.getProjectPreferences().getWikiLanguage());
		}

		return managedOrganization;
	}

	private void verifyProjectDeletion(Project project) throws EntityNotFoundException {
		if (project != null && project.isDeleted() != null && project.isDeleted()) {
			throw new EntityNotFoundException();
		}
	}

}
