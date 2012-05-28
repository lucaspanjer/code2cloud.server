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

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.github.api.GitHub;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.AuthenticationException;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.domain.SortInfo;
import com.tasktop.c2c.server.profile.domain.internal.Agreement;
import com.tasktop.c2c.server.profile.domain.internal.AgreementProfile;
import com.tasktop.c2c.server.profile.domain.internal.OrganizationProfile;
import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.ProjectProfile;

/**
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public class BaseProfileIdentityManagmentService implements IdentityManagmentService {

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected UsersConnectionRepository usersConnRepo;

	@Override
	public Profile getProfileByUsername(String username) throws EntityNotFoundException {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Profile> query = criteriaBuilder.createQuery(Profile.class);
		Root<Profile> root = query.from(Profile.class);
		query.select(root).where(criteriaBuilder.equal(root.get("username"), username));

		try {
			return entityManager.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException("user [" + username + "] not found");
		}
	}

	@Override
	public boolean usernameExists(String username) {
		try {
			getProfileByUsername(username);
			return true;
		} catch (EntityNotFoundException e) {
			return false;
		}
	}

	@Override
	public Profile getProfileByEmail(String email) throws EntityNotFoundException {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Profile> query = criteriaBuilder.createQuery(Profile.class);
		Root<Profile> root = query.from(Profile.class);
		query.select(root).where(criteriaBuilder.equal(root.get("email"), email));

		try {
			return entityManager.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException("user with email [" + email + "] not found");
		}
	}

	@Override
	public Profile validateCredentials(String username, String password) throws AuthenticationException {
		if (username == null || password == null) {
			// Bail out now.
			throw new AuthenticationException("Credentials not provided");
		}

		// First, try the password matcher.
		Profile retProfile = checkIfCredentialsMatchOnPassword(username, password);

		if (retProfile == null) {
			// No match? Try the GitHub key
			retProfile = checkIfCredentialsMatchOnGithubKey(username, password);
		}

		if (retProfile != null && retProfile.getDisabled() != null && retProfile.getDisabled()) {
			throw new AuthenticationException("Account disabled");
		} else if (retProfile == null) {
			throw new AuthenticationException("Username and password do no match");
		}
		if (entityManager.contains(retProfile)) {
			entityManager.refresh(retProfile);
		}

		return retProfile;
	}

	private Profile checkIfCredentialsMatchOnPassword(String username, String password) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Profile> query = criteriaBuilder.createQuery(Profile.class);
			Root<Profile> root = query.from(Profile.class);
			query.select(root)
					.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("username"), username),
							criteriaBuilder.equal(root.get("password"), passwordEncoder.encodePassword(password, null))));

			Profile profile = entityManager.createQuery(query).getSingleResult();
			return profile;
		} catch (NoResultException e) {
			// ignore, expected
			return null;
		}
	}

	private Profile checkIfCredentialsMatchOnGithubKey(String username, String githubKey) {
		// Get a connection for this user, if one exists.
		ConnectionRepository connRepo = this.usersConnRepo.createConnectionRepository(username);
		Connection<GitHub> apiConn = connRepo.findPrimaryConnection(GitHub.class);

		if (apiConn == null) {
			// No connection exists right now.
			return null;
		}

		// If there's a connection, check if the keys match.
		if (githubKey.equals(apiConn.createData().getAccessToken())) {
			// Keys match - return our profile now.
			try {
				return getProfileByUsername(username);
			} catch (EntityNotFoundException nre) {
				// The username didn't exist in the system - can't authenticate.
				return null;
			}
		}

		return null;
	}

	@Override
	public List<String> computeAuthorities(Profile profile) {
		List<String> roles = new ArrayList<String>();

		if (profile.getDisabled()) {
			return roles; // Empty
		}

		if (hasPendingAgreements(profile)) {
			roles.add(Role.UserWithPendingAgreements);
			return roles; // Single role
		}

		roles.add(Role.User);

		if (profile.getAdmin() == true) {
			roles.add(Role.Admin);
		}
		addOrganizationRoles(profile, roles);
		addProjectRoles(profile, roles);
		return roles;

	}

	protected void addOrganizationRoles(Profile profile, List<String> roles) {
		for (OrganizationProfile organizationProfile : profile.getOrganizationProfiles()) {
			String identifier = organizationProfile.getOrganization().getIdentifier();

			if (organizationProfile.getOwner()) {
				roles.add(AuthUtils.toCompoundOrganizationRole(Role.Admin, identifier));
			}

			if (organizationProfile.getUser()) {
				roles.add(AuthUtils.toCompoundOrganizationRole(Role.User, identifier));
			}

		}
	}

	/**
	 * @param profile
	 * @param roles
	 */
	protected void addProjectRoles(Profile profile, List<String> roles) {
		for (ProjectProfile projectProfile : profile.getProjectProfiles()) {
			String projectIdentifier = projectProfile.getProject().getIdentifier();

			// Add our appropriate roles now.
			if (projectProfile.getOwner()) {
				roles.add(AuthUtils.toCompoundProjectRole(Role.Admin, projectIdentifier));
			}

			if (projectProfile.getUser()) {
				roles.add(AuthUtils.toCompoundProjectRole(Role.User, projectIdentifier));
			}

			if (projectProfile.getCommunity()) {
				roles.add(AuthUtils.toCompoundProjectRole(Role.Community, projectIdentifier));
			}
		}
	}

	protected final boolean hasPendingAgreements(Profile profile) {

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
				return true;
			}
		}

		return false;
	}

	@Override
	public Profile createProfile(Profile profile) {
		profile.setId(null);
		profile.setPassword(passwordEncoder.encodePassword(profile.getPassword(), null));
		profile.setDisabled(false);
		entityManager.persist(profile);
		entityManager.flush();
		return profile;
	}

	@Override
	public Profile updateProfile(Profile profile) throws EntityNotFoundException {
		Profile currentProfile = getProfileByUsername(profile.getUsername());

		currentProfile.setEmail(profile.getEmail());
		currentProfile.setFirstName(profile.getFirstName());
		currentProfile.setLastName(profile.getLastName());
		if (profile.getNotificationSettings() != null) {
			currentProfile.getNotificationSettings().setEmailTaskActivity(
					profile.getNotificationSettings().getEmailTaskActivity());
			currentProfile.getNotificationSettings().setEmailNewsAndEvents(
					profile.getNotificationSettings().getEmailNewsAndEvents());
			currentProfile.getNotificationSettings().setEmailServiceAndMaintenance(
					profile.getNotificationSettings().getEmailServiceAndMaintenance());
		}
		currentProfile.setDisabled(profile.getDisabled());
		if (profile.getPassword() != null && profile.getPassword().trim().length() > 0) {
			resetPassword(currentProfile, profile.getPassword());
		}

		return currentProfile;
	}

	protected void resetPassword(Profile profile, String password) {
		profile.setPassword(passwordEncoder.encodePassword(profile.getPassword(), null));
	}

	@Override
	@SuppressWarnings("unchecked")
	public QueryResult<Profile> findProfiles(String queryText, Region region, SortInfo sortInfo) {
		queryText = queryText == null ? "" : queryText.trim();
		queryText = queryText.toLowerCase();
		queryText += "%";

		String coreQuery = "from " + Profile.class.getSimpleName() + " p where " + "LOWER(p.firstName) like :q OR "
				+ "LOWER(p.lastName) like :q OR " + "LOWER(p.username) like :q";
		Query query = entityManager.createQuery("select p "
				+ coreQuery
				+ " "
				+ ProfileServiceBean.createSortClause("p", Profile.class, sortInfo, new SortInfo("firstName"),
						new SortInfo("lastName"), new SortInfo("username")));
		query.setParameter("q", queryText.trim());

		query.setFirstResult(region.getOffset());
		query.setMaxResults(region.getSize());

		Long totalSize = (Long) entityManager.createQuery("select count(p) " + coreQuery).setParameter("q", queryText)
				.getSingleResult();
		List<Profile> results = query.getResultList();

		return new QueryResult<Profile>(region, results, totalSize.intValue());
	}
}
