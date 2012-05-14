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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus.ServiceState;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.cloud.service.HudsonSlavePoolServiceInternal;
import com.tasktop.c2c.server.cloud.service.NodeProvisioningService;
import com.tasktop.c2c.server.common.service.AbstractJpaServiceBean;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.job.JobService;
import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementService;
import com.tasktop.c2c.server.configuration.service.ProjectServiceMangementServiceClient;
import com.tasktop.c2c.server.configuration.service.ProjectServiceMangementServiceProvider;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.ProjectServiceProfile;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.profile.service.ProfileWebService;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;

@Service("projectServiceService")
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceServiceBean extends AbstractJpaServiceBean implements ProjectServiceService,
		InternalApplicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceServiceBean.class.getSimpleName());

	@Autowired
	private ProfileServiceConfiguration configuration;

	@Autowired
	private JobService jobService;

	@Resource(name = "profileWebServiceBean")
	private ProfileWebService profileWebService;

	@Resource
	private Map<ServiceType, NodeProvisioningService> nodeProvisioningServiceByType;

	@Autowired
	private ProjectServiceMangementServiceProvider projectServiceMangementServiceProvider;

	@Autowired
	private HudsonSlavePoolServiceInternal hudsonSlavePoolServiceInternal;

	private boolean updateServiceTemplateOnStart = true;

	@Override
	public void provisionDefaultServices(Long projectId) throws EntityNotFoundException, ProvisioningException {
		Project project = entityManager.find(Project.class, projectId);
		if (project == null) {
			throw new EntityNotFoundException();
		}
		verifyCanProvision(project);

		ProjectServiceProfile template = getDefaultTemplate();
		if (template == null) {
			throw new EntityNotFoundException();
		}
		ProjectServiceProfile serviceProfile = template.createCopy();
		serviceProfile.setProject(project);
		project.setProjectServiceProfile(serviceProfile);

		updateTemplateServiceConfiguration(project);
		entityManager.persist(serviceProfile);

		// Schedule the jobs
		for (ProjectService service : serviceProfile.getProjectServices()) {
			jobService.schedule(new ProjectServicesProvisioningJob(project, service.getType()));
		}

	}

	/**
	 * get the default template
	 * 
	 * @return the default template, or null if there is none
	 */
	private ProjectServiceProfile getDefaultTemplate() {
		try {
			ProjectServiceProfile template = (ProjectServiceProfile) entityManager.createQuery(
					"select e from " + ProjectServiceProfile.class.getSimpleName() + " e where e.template = true")
					.getSingleResult();
			// we shouldn't need to do this here, but for some reason it fixes an issue when running unit
			// tests from within Eclipse. Pretty harmless.
			entityManager.refresh(template);
			return template;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public void doProvisionServices(Long projectId, ServiceType type) throws EntityNotFoundException,
			ProvisioningException {
		Project project = entityManager.find(Project.class, projectId);
		if (project == null) {
			throw new EntityNotFoundException();
		}

		AuthUtils.assumeSystemIdentity(project.getIdentifier());

		ProjectServiceConfiguration config = createProjectServiceConfiguration(project);

		NodeProvisioningService nodeProvisioningService = nodeProvisioningServiceByType.get(type);
		if (nodeProvisioningService == null) {
			LOGGER.info("Not seting up service " + type + " no node provisionService available");
			return;
		}

		LOGGER.info("provisioning node for " + type);
		com.tasktop.c2c.server.cloud.domain.ServiceHost domainServiceHost = nodeProvisioningService.provisionNode();
		ServiceHost serviceHost = convertToInternal(domainServiceHost);
		LOGGER.info("provisioned to " + serviceHost.getInternalNetworkAddress());

		// Now configure the host for the new project.
		ProjectServiceMangementServiceClient nodeConfigurationService = projectServiceMangementServiceProvider
				.getNewService(domainServiceHost.getInternalNetworkAddress(), type);

		try {
			LOGGER.info("configuring node for " + type);
			nodeConfigurationService.provisionService(config);
			LOGGER.info("configuring done");
		} catch (Exception e) {
			throw new ProvisioningException("Caught exception while configuring node", e);
		}

		for (ProjectService service : project.getProjectServiceProfile().getProjectServices()) {
			if (service.getType().equals(type)) {
				service.setServiceHost(serviceHost);
			}
		}

	}

	/**
	 * @param project
	 * @return
	 */
	private ProjectServiceConfiguration createProjectServiceConfiguration(Project project) {
		ProjectServiceConfiguration config = new ProjectServiceConfiguration();
		config.setProjectIdentifier(project.getIdentifier());
		config.setProperty(ProjectServiceConfiguration.PROFILE_HOSTNAME, configuration.getWebHost());
		config.setProperty(ProjectServiceConfiguration.PROFILE_PROTOCOL, configuration.getProfileApplicationProtocol());
		config.setProperty(ProjectServiceConfiguration.PROFILE_BASE_URL, configuration.getProfileBaseUrl());
		config.setProperty(ProjectServiceConfiguration.PROFILE_BASE_SERVICE_URL,
				configuration.getServiceUrlPrefix(project.getIdentifier()));
		config.setProperty(ProjectServiceConfiguration.MARKUP_LANGUAGE, project.getProjectPreferences()
				.getWikiLanguage().toString());
		config.setProperty(ProjectServiceConfiguration.UNIQUE_IDENTIFER,
				project.getIdentifier() + "_" + project.getId());
		return config;
	}

	private ServiceHost convertToInternal(com.tasktop.c2c.server.cloud.domain.ServiceHost serviceHost) {
		return entityManager.find(ServiceHost.class, serviceHost.getId());
	}

	private void updateTemplateServiceConfiguration(Project project) {
		for (ProjectService service : project.getProjectServiceProfile().getProjectServices()) {
			switch (service.getType()) {
			case BUILD:
				// Replace our marker string with the actual project identifier
				service.setInternalUriPrefix(service.getInternalUriPrefix().replace("APPID", project.getIdentifier()));
				break;
			}
		}
	}

	protected void verifyCanProvision(Project project) throws ProvisioningException {
		if (project.getProjectServiceProfile() != null) {
			// can't provision services if they're already provisioned.
			throw new ProvisioningException("Can't provision services: they're already provisioned");
		}
	}

	private List<ProjectService> projectServiceTemplate;

	@Autowired
	@Resource(name = "projectServiceTemplate")
	public void setProjectServiceTemplate(List<ProjectService> projectServices) {
		this.projectServiceTemplate = projectServices;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = { EntityNotFoundException.class,
			NoResultException.class })
	public void initializeApplicationServiceProfileTemplate() {

		ProjectServiceProfile templateServiceProfile = getDefaultTemplate();

		boolean create;

		if (templateServiceProfile != null) {
			if (!updateServiceTemplateOnStart) {
				return;
			}

			for (ProjectService service : templateServiceProfile.getProjectServices()) {
				entityManager.remove(service);
			}
			entityManager.flush();

			templateServiceProfile.getProjectServices().clear();

			create = false;
		} else {
			create = true;
			templateServiceProfile = new ProjectServiceProfile();
			templateServiceProfile.setTemplate(true);
		}

		for (ProjectService service : projectServiceTemplate) {
			templateServiceProfile.add(service);
		}

		if (create) {
			entityManager.persist(templateServiceProfile);
		}

		entityManager.flush();
	}

	@Override
	public ProjectService findServiceByUri(String projectIdentifier, String uri) throws EntityNotFoundException {
		Project project = getProjectByIdentifier(projectIdentifier);
		if (project.getProjectServiceProfile() == null) {
			return null;
		}
		for (ProjectService service : project.getProjectServiceProfile().getProjectServices()) {
			if (service.matchesUri(uri)) {
				return service;
			}
		}
		return null;
	}

	protected Project getProjectByIdentifier(String projectIdentifier) throws EntityNotFoundException {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Project> query = criteriaBuilder.createQuery(Project.class);
		Root<Project> root = query.from(Project.class);
		query.select(root).where(criteriaBuilder.equal(root.get("identifier"), projectIdentifier));

		Project project;
		try {
			project = entityManager.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		}
		return project;
	}

	@Override
	public List<ProjectService> findProjectServiceByType(String projectIdentifier, ServiceType serviceType)
			throws EntityNotFoundException {
		Project project = getProjectByIdentifier(projectIdentifier);
		List<ProjectService> services = new ArrayList<ProjectService>();
		if (project.getProjectServiceProfile() != null) {
			for (ProjectService service : project.getProjectServiceProfile().getProjectServices()) {
				if (service.getType() == serviceType) {
					services.add(service);
				}
			}
		}
		return services;
	}

	public void setNodeProvisioningServiceByType(Map<ServiceType, NodeProvisioningService> nodeProvisioningServiceByType) {
		this.nodeProvisioningServiceByType = nodeProvisioningServiceByType;
	}

	@Override
	public List<ServiceHost> findHostsForAddress(String remoteAddr) {
		@SuppressWarnings("unchecked")
		List<ServiceHost> results = entityManager
				.createQuery(
						"SELECT node FROM " + ServiceHost.class.getSimpleName()
								+ " node WHERE node.internalNetworkAddress = :addr").setParameter("addr", remoteAddr)
				.getResultList();
		return results;
	}

	@Override
	public List<ProjectService> findProjectServicesOlderThan(ServiceType type, Date date) {
		List<ProjectService> projectServices = entityManager
				.createQuery(
						"SELECT projectService FROM "
								+ ProjectService.class.getSimpleName()
								+ " projectService WHERE projectService.type = :type AND projectService.allocationTime < :date")
				.setParameter("type", type).setParameter("date", date).getResultList();

		return projectServices;
	}

	@Value("${updateServiceTemplateOnStart}")
	public void setUpdateServiceTemplateOnStart(boolean updateServiceTemplateOnStart) {
		this.updateServiceTemplateOnStart = updateServiceTemplateOnStart;
	}

	@Override
	public List<ProjectServiceStatus> computeProjectServicesStatus(Project managedProject) {
		List<ProjectServiceStatus> result = new ArrayList<ProjectServiceStatus>();

		for (ProjectService service : managedProject.getProjectServiceProfile().getProjectServices()) {
			try {
				if (service.getServiceHost() == null || !service.getServiceHost().isAvailable()) {
					result.add(createFailureProjectServiceStatus(service));
				} else {

					ProjectServiceManagementService serviceMangementService = projectServiceMangementServiceProvider
							.getNewService(service.getServiceHost().getInternalNetworkAddress(), service.getType());

					result.add(serviceMangementService.retrieveServiceStatus(managedProject.getIdentifier(),
							service.getType()));
				}
			} catch (Exception e) {
				LOGGER.warn("Caught exception trying to contact service", e);
				result.add(createFailureProjectServiceStatus(service));
			}
		}

		return result;
	}

	private ProjectServiceStatus createFailureProjectServiceStatus(ProjectService service) {
		ProjectServiceStatus result = new ProjectServiceStatus();
		result.setProjectIdentifier(service.getProjectServiceProfile().getProject().getIdentifier());
		result.setServiceType(service.getType());
		result.setServiceState(ServiceState.UNAVAILABLE);
		return result;
	}

	@Override
	public void doDeprovisionService(Long projectServiceId) throws EntityNotFoundException {

		ProjectService projectService = entityManager.find(ProjectService.class, projectServiceId);

		if (projectService.getServiceHost() != null) {
			LOGGER.info(String.format("deprovisioning service [%s] on node [%s]", projectService.getType(),
					projectService.getServiceHost().getInternalNetworkAddress()));

			switch (projectService.getType()) {
			case BUILD:
			case MAVEN:
			case SCM:
			case TASKS:
			case WIKI:
			default:
				deprovisionServiceViaServiceManageService(projectService);
				break;
			case BUILD_SLAVE:

				hudsonSlavePoolServiceInternal.doReleaseSlave(projectService.getProjectServiceProfile().getProject()
						.getIdentifier(), projectService.getServiceHost().getId());
				break;
			}
			LOGGER.info("deprovisioning done");

		}

	}

	private void deprovisionServiceViaServiceManageService(ProjectService projectService) {
		ProjectServiceMangementServiceClient nodeConfigurationService = projectServiceMangementServiceProvider
				.getNewService(projectService.getServiceHost().getInternalNetworkAddress(), projectService.getType());

		ProjectServiceConfiguration config = createProjectServiceConfiguration(projectService
				.getProjectServiceProfile().getProject());

		nodeConfigurationService.deprovisionService(config);

		projectService.getProjectServiceProfile().getProjectServices().remove(projectService);
		if (projectService.getServiceHost() != null) {
			projectService.getServiceHost().getProjectServices().remove(projectService);
		}
		entityManager.remove(projectService);
	}
}
