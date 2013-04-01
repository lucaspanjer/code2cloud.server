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
package com.tasktop.c2c.server.internal.deployment.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Errors;

import com.tasktop.c2c.server.auth.service.AuthenticationServiceUser;
import com.tasktop.c2c.server.common.service.AbstractJpaServiceBean;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.deployment.domain.CloudService;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceType;
import com.tasktop.c2c.server.deployment.domain.DeploymentStatus;
import com.tasktop.c2c.server.deployment.domain.DeploymentType;
import com.tasktop.c2c.server.deployment.service.DeploymentConfigurationService;
import com.tasktop.c2c.server.deployment.service.ServiceException;
import com.tasktop.c2c.server.internal.profile.service.SecurityPolicy;
import com.tasktop.c2c.server.profile.domain.build.BuildArtifact;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails.BuildResult;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectArtifact;
import com.tasktop.c2c.server.profile.domain.project.ProjectArtifacts;
import com.tasktop.c2c.server.profile.service.ProfileService;
import com.tasktop.c2c.server.profile.service.ProjectArtifactService;

/**
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * 
 */
@Transactional(rollbackFor = { Exception.class })
public class DeploymentConfigurationServiceImpl extends AbstractJpaServiceBean implements
		DeploymentConfigurationService, DeploymentConfigurationServiceInternal {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentConfigurationServiceImpl.class.getName());

	private static DeploymentConfigurationServiceImpl lastInstance;

	// For testing
	public static DeploymentConfigurationServiceImpl getLastInstance() {
		return lastInstance;
	}

	public DeploymentConfigurationServiceImpl() {
		lastInstance = this;
	}

	@Autowired
	private ProfileService profileService;

	@Qualifier("main")
	@Autowired
	private ProjectArtifactService projectArtifactService;

	@Autowired
	private SecurityPolicy securityPolicy;

	@Autowired
	private DeploymentDomain deploymentDomain;

	private Map<DeploymentServiceType, DeploymentServiceFactory> deploymentServiceFactoriesByType = new HashMap<DeploymentServiceType, DeploymentServiceFactory>();

	private String getProjectIdentifier() {
		return TenancyUtil.getCurrentTenantProjectIdentifer();
	}

	private Project getProject() {
		try {
			return profileService.getProjectByIdentifier(getProjectIdentifier());
		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DeploymentConfiguration> listDeployments(Region region) {
		return listDeploymentsWithoutServiceQuery(region, true);
	}

	private List<DeploymentConfiguration> listDeploymentsWithoutServiceQuery(Region region, boolean populate) {
		// TODO paging params
		List<com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration> internalResultList = entityManager
				.createQuery(
						"SELECT dc FROM "
								+ com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration.class
										.getSimpleName() + " dc where dc.project.identifier = :projectIdentifer")
				.setParameter("projectIdentifer", getProjectIdentifier()).getResultList();

		List<DeploymentConfiguration> result = new ArrayList<DeploymentConfiguration>(internalResultList.size());

		boolean tryPopulate = true;
		for (com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration internalDeploymentConfiguration : internalResultList) {
			securityPolicy.retrieve(internalDeploymentConfiguration);
			DeploymentConfiguration config = deploymentDomain.convertToPublic(internalDeploymentConfiguration);
			if (populate && tryPopulate) {
				try {
					populateDeploymentConfiguration(config);
				} catch (ServiceException e) {
					config.setErrorString(e.getMessage());
				} catch (Exception e) {
					LOGGER.error("Exception while poulating deployment configuration.", e);
					tryPopulate = false;
				}
			}
			result.add(config);
		}

		return result;
	}

	@Override
	public List<DeploymentConfiguration> listDeploymentsWithoutServiceQuery(Region region) {
		return listDeploymentsWithoutServiceQuery(region, false);
	}

	private void populateDeploymentConfiguration(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {

		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		deploymentService.populate(deploymentConfiguration);
	}

	@Override
	public DeploymentConfiguration createDeployment(DeploymentConfiguration deploymentConfiguration)
			throws ValidationException {
		deploymentDomain.prepareForCreate(deploymentConfiguration);

		try {
			DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
			updateTokenIfNeeded(deploymentConfiguration, deploymentService);

			com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration internalDeploymentConfiguration = deploymentDomain
					.convertToInternal(deploymentConfiguration);
			internalDeploymentConfiguration.setProject(getProject());

			securityPolicy.create(internalDeploymentConfiguration);

			validateBeforeCreate(deploymentConfiguration, internalDeploymentConfiguration);

			entityManager.persist(internalDeploymentConfiguration);
			entityManager.flush(); // Need the Id;
			deploymentConfiguration.setId(internalDeploymentConfiguration.getId());

			deploymentService.create(deploymentConfiguration);

			boolean shouldDeploy = shouldDeployOnCreate(deploymentConfiguration);
			if (shouldDeploy) {
				String projectId = internalDeploymentConfiguration.getProject().getIdentifier();
				deployLatestArtifact(deploymentConfiguration, deploymentService, projectId);
			}

		} catch (ServiceException e) {
			setStatusErrorMessage(deploymentConfiguration, e.getMessage());
		} catch (IOException e) {
			setStatusErrorMessage(deploymentConfiguration, e.getMessage());
		}

		return deploymentConfiguration;
	}

	private boolean shouldDeployOnCreate(DeploymentConfiguration deploymentConfiguration) {
		if (deploymentConfiguration.getDeploymentType() == null
				|| !deploymentConfiguration.getDeploymentType().equals(DeploymentType.MANUAL)) {
			return false;
		} else if (deploymentConfiguration.getBuildJobName() == null
				|| deploymentConfiguration.getBuildJobNumber() == null
				|| deploymentConfiguration.getBuildArtifactPath() == null) {
			return false;
		}
		return true;
	}

	protected void validateBeforeCreate(DeploymentConfiguration publicDeploymentConfiguration,
			com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration internalDeploymentConfiguration)
			throws ValidationException {
		super.validate(publicDeploymentConfiguration);
		if (findDeploymetByName(internalDeploymentConfiguration.getName()) != null) {
			Errors errors = createErrors(publicDeploymentConfiguration);
			errors.rejectValue("name", "deployment.name.notUnique");
			throw new ValidationException(errors, AuthenticationServiceUser.getCurrentUserLocale());
		}
	}

	private com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration findDeploymetByName(String name) {
		try {
			return (com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration) entityManager
					.createQuery(
							"SELECT dc FROM "
									+ com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration.class
											.getSimpleName()
									+ " dc where dc.project.identifier = :projectIdentifer AND dc.name = :name")
					.setParameter("projectIdentifer", getProjectIdentifier()).setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	private void updateTokenIfNeeded(DeploymentConfiguration config, DeploymentService deploymentService)
			throws ServiceException {
		if (config.getPassword() != null && !config.getPassword().isEmpty()) {
			String token = deploymentService.login();
			config.setApiToken(token);
		}
	}

	/**
	 * @param deploymentConfiguration
	 * @param warFile
	 * @throws ServiceException
	 */
	private void deployWar(DeploymentConfiguration deploymentConfiguration, DeploymentService deploymentSerivce,
			File warFile) throws ServiceException {
		try {
			deploymentSerivce.uploadApplication(deploymentConfiguration, warFile);

		} catch (MalformedURLException e) {
			throw new ServiceException("Failed to deploy war file " + warFile.getName(), e);
		} catch (IOException e) {
			throw new ServiceException("Failed to deploy war file " + warFile.getName(), e);
		}
	}

	@Override
	public DeploymentConfiguration updateDeployment(DeploymentConfiguration deploymentConfiguration)
			throws EntityNotFoundException, ValidationException {
		validate(deploymentConfiguration);
		com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration internalDeploymentConfiguration = entityManager
				.find(com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration.class,
						deploymentConfiguration.getId());
		if (internalDeploymentConfiguration == null) {
			throw new EntityNotFoundException("No DC with id: " + deploymentConfiguration.getId());
		}
		securityPolicy.modify(internalDeploymentConfiguration);

		DeploymentService deploymentService = null;
		try {

			deploymentService = createDeploymentService(deploymentConfiguration);

			updateTokenIfNeeded(deploymentConfiguration, deploymentService);

			deploymentService.update(deploymentConfiguration);

			boolean shouldDeploy = shouldDeployOnUpdate(deploymentConfiguration, internalDeploymentConfiguration);
			if (shouldDeploy) {
				String projectId = internalDeploymentConfiguration.getProject().getIdentifier();
				deployLatestArtifact(deploymentConfiguration, deploymentService, projectId);
			}

		} catch (ServiceException e) {
			setStatusErrorMessage(deploymentConfiguration, e.getMessage());
		} catch (IOException e) {
			setStatusErrorMessage(deploymentConfiguration, e.getMessage());
		}

		// Update the fields in the managed object
		deploymentDomain.updateInternal(deploymentConfiguration, internalDeploymentConfiguration);

		return deploymentConfiguration;
	}

	@Override
	public void deployLatestArtifact(DeploymentConfiguration deploymentConfiguration,
			DeploymentService deploymentService, String projectId) throws IOException, ServiceException {
		ProjectArtifacts artifacts = projectArtifactService.findBuildArtifacts(projectId,
				deploymentConfiguration.getBuildJobName(), deploymentConfiguration.getBuildJobNumber());
		ProjectArtifact artifact = null;
		if (artifacts == null) {
			throw new ServiceException("Could not find build to deploy");
		} else {
			for (ProjectArtifact a : artifacts.getArtifacts()) {
				if (a.getPath().equals(deploymentConfiguration.getBuildArtifactPath())) {
					artifact = a;
					break;
				}
			}
			if (artifact == null) {
				throw new ServiceException("Could not find artifact to deploy");
			} else {
				File tempWarFile = File.createTempFile("deploy", getExtension(artifact));
				projectArtifactService.downloadProjectArtifact(projectId, tempWarFile, artifact);
				deployWar(deploymentConfiguration, deploymentService, tempWarFile);
				deploymentConfiguration.setLastDeploymentDate(new Date());
			}
		}
		deploymentService.populate(deploymentConfiguration);
	}

	private String getExtension(ProjectArtifact artifact) {
		String extension;

		int lastDot = artifact.getPath().lastIndexOf(".");
		if (lastDot == -1) {
			return ".war";
		}
		return artifact.getPath().substring(lastDot);
	}

	/**
	 * @param deploymentConfiguration
	 * @param existingDeploymentConfiguration
	 * @return
	 */
	private boolean shouldDeployOnUpdate(DeploymentConfiguration deploymentConfiguration,
			com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration existingDeploymentConfiguration) {
		if (deploymentConfiguration.getDeploymentType() == null
				|| !deploymentConfiguration.getDeploymentType().equals(DeploymentType.MANUAL)) {
			return false;
		} else if (deploymentConfiguration.getBuildJobName() == null
				|| deploymentConfiguration.getBuildJobNumber() == null
				|| deploymentConfiguration.getBuildArtifactPath() == null) {
			return false;
		}
		// Ok so check for changes;
		return !(deploymentConfiguration.getBuildJobName().equals(existingDeploymentConfiguration.getBuildJobName())
				&& deploymentConfiguration.getBuildJobNumber().equals(
						existingDeploymentConfiguration.getBuildJobNumber()) && deploymentConfiguration
				.getBuildArtifactPath().equals(existingDeploymentConfiguration.getBuildArtifactPath()));
	}

	private void setStatusErrorMessage(DeploymentConfiguration deploymentConfiguration, String message) {
		deploymentConfiguration.setErrorString(message);
	}

	@Override
	public void deleteDeployment(DeploymentConfiguration deploymentConfiguration, boolean alsoDeleteFromCF)
			throws EntityNotFoundException, ServiceException {
		com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration internalDeploymentConfiguration = entityManager
				.find(com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration.class,
						deploymentConfiguration.getId());
		if (internalDeploymentConfiguration == null) {
			throw new EntityNotFoundException("No DC with id: " + deploymentConfiguration.getId());
		}
		securityPolicy.delete(internalDeploymentConfiguration);
		entityManager.remove(internalDeploymentConfiguration);

		if (alsoDeleteFromCF) {
			createDeploymentService(deploymentConfiguration).deleteApplication(deploymentConfiguration);
		}
	}

	@Override
	public DeploymentStatus startDeployment(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		checkUpdatePermissions(deploymentConfiguration);
		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		deploymentService.startApplication(deploymentConfiguration);
		deploymentService.populate(deploymentConfiguration);

		return deploymentConfiguration.getStatus();
	}

	/**
	 * @param deploymentConfiguration
	 */
	private void checkUpdatePermissions(DeploymentConfiguration deploymentConfiguration) {
		com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration internalDeploymentConfiguration = entityManager
				.find(com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration.class,
						deploymentConfiguration.getId());
		securityPolicy.modify(internalDeploymentConfiguration);
	}

	@Override
	public DeploymentStatus stopDeployment(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		checkUpdatePermissions(deploymentConfiguration);

		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		deploymentService.stopApplication(deploymentConfiguration);
		deploymentService.populate(deploymentConfiguration);

		return deploymentConfiguration.getStatus();
	}

	@Override
	public DeploymentStatus restartDeployment(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		checkUpdatePermissions(deploymentConfiguration);

		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		deploymentService.restartApplication(deploymentConfiguration);
		deploymentService.populate(deploymentConfiguration);

		return deploymentConfiguration.getStatus();
	}

	@Override
	public List<com.tasktop.c2c.server.deployment.domain.CloudService> getAvailableServices(
			DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		return deploymentService.getServices();
	}

	@Override
	public CloudService createService(CloudService service, DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		checkUpdatePermissions(deploymentConfiguration);
		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		return deploymentService.createService(service);
	}

	@Override
	public List<Integer> getAvailableMemoryConfigurations(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		List<Integer> options = new ArrayList<Integer>();

		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		int[] choices = deploymentService.getApplicationMemoryChoices();
		if (choices != null) {
			for (int choice : choices) {
				options.add(choice);
			}
		}

		return options;
	}

	@Override
	public List<DeploymentServiceConfiguration> getAvailableServiceConfigurations(
			DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		DeploymentService deploymentService = createDeploymentService(deploymentConfiguration);
		return deploymentService.getAvailableServiceConfigurations(deploymentConfiguration);
	}

	private DeploymentService createDeploymentService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {

		DeploymentServiceFactory factory = null;
		if (deploymentConfiguration.getServiceType() == null) {
			if (deploymentServiceFactoriesByType.size() == 1) {
				deploymentConfiguration.setServiceType(deploymentServiceFactoriesByType.keySet().iterator().next());
				factory = deploymentServiceFactoriesByType.values().iterator().next();
			} else {
				throw new ServiceException("No deployment type specified, and there are multiple types", null);
			}
		} else {
			factory = deploymentServiceFactoriesByType.get(deploymentConfiguration.getServiceType());
		}
		if (factory == null) {
			throw new ServiceException(String.format("No such deployment type [%s]",
					deploymentConfiguration.getServiceType()), null);
		}
		return factory.constructService(deploymentConfiguration);
	}

	// TODO, push down to service.
	@Override
	public boolean validateCredentials(String url, String username, String password) {
		DeploymentConfiguration deploymentConfiguration = new DeploymentConfiguration();
		deploymentConfiguration.setApiBaseUrl(url);
		deploymentConfiguration.setUsername(username);
		deploymentConfiguration.setPassword(password);

		DeploymentService deploymentSerivce;
		try {
			deploymentSerivce = createDeploymentService(deploymentConfiguration);
			return deploymentSerivce.validateCredentials(deploymentConfiguration);
		} catch (ServiceException e) {
			return false;
		}
	}

	@Override
	// TODO explicit security check.
	public void onBuildCompleted(String jobName, BuildDetails buildDetails) {
		List<com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration> internalResultList = entityManager
				.createQuery(
						"SELECT dc FROM "
								+ com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration.class
										.getSimpleName()
								+ " dc where dc.project.identifier = :projectIdentifer AND dc.deploymentType = :depType AND dc.buildJobName = :jobName")
				.setParameter("projectIdentifer", getProjectIdentifier())
				.setParameter("depType", DeploymentType.AUTOMATED).setParameter("jobName", jobName).getResultList();

		for (com.tasktop.c2c.server.internal.deployment.domain.DeploymentConfiguration dc : internalResultList) {
			if (!(BuildResult.SUCCESS.equals(buildDetails.getResult()) || (BuildResult.UNSTABLE.equals(buildDetails
					.getResult()) && dc.isDeployUnstableBuilds()))) {
				return;
			}
			String artifactPath = dc.getBuildArtifactPath();
			BuildArtifact toDeploy = null;
			for (BuildArtifact artifact : buildDetails.getArtifacts()) {
				if (matches(artifactPath, artifact)) {
					// TODO what if multiple matches.
					toDeploy = artifact;
				}
			}

			if (toDeploy == null) {
				// TODO message somewhere?
				continue;
			}

			try {
				File tempWarFile = File.createTempFile("deploy", "war");
				ProjectArtifact artifact = new ProjectArtifact();
				artifact.setUrl(toDeploy.getUrl()); // All thats needed now
				projectArtifactService.downloadProjectArtifact(getProjectIdentifier(), tempWarFile, artifact);
				DeploymentConfiguration deploymentConfig = deploymentDomain.convertToPublic(dc);
				deployWar(deploymentConfig, createDeploymentService(deploymentConfig), tempWarFile);
				dc.setLastDeploymentDate(new Date());
				dc.setBuildJobNumber(buildDetails.getNumber() + "");
			} catch (IOException e) {
				LOGGER.warn("exception durring auto deployment", e);
			} catch (ServiceException e) {
				LOGGER.warn("exception durring auto deployment", e);
			}

		}

	}

	private PathMatcher pathMatcher = new AntPathMatcher();

	private boolean matches(String artifactPath, BuildArtifact artifact) {
		return pathMatcher.match(artifactPath, artifact.getRelativePath());
	}

	@Override
	public List<DeploymentServiceType> getSupportedDeploymentServiceTypes() {
		return new ArrayList<DeploymentServiceType>(deploymentServiceFactoriesByType.keySet());
	}

	/**
	 * @param projectArtifactService
	 *            the projectArtifactService to set
	 */
	public void setProjectArtifactService(ProjectArtifactService projectArtifactService) {
		this.projectArtifactService = projectArtifactService;
	}

	public void setDeploymentServiceFactoriesByType(
			Map<DeploymentServiceType, DeploymentServiceFactory> deploymentServiceFactoriesByType) {
		this.deploymentServiceFactoriesByType = deploymentServiceFactoriesByType;
	}

}
