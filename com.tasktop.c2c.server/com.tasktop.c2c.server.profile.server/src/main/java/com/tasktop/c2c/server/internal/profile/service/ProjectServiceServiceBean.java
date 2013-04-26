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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tasktop.c2c.server.cloud.domain.PoolStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus.ServiceState;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.cloud.service.NodeProvisioningService;
import com.tasktop.c2c.server.common.service.AbstractJpaServiceBean;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.NoNodeAvailableException;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.job.JobService;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementService;
import com.tasktop.c2c.server.configuration.service.ProjectServiceMangementServiceProvider;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.ProjectServiceProfile;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;

@Service("projectServiceService")
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceServiceBean extends AbstractJpaServiceBean implements ProjectServiceService,
		InternalProjectServiceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceServiceBean.class.getSimpleName());

	private static final long DOWN_SERVICE_RECHECK_DELAY = 10 * 1000l;

	@Autowired
	private ProfileServiceConfiguration configuration;

	@Autowired(required = false)
	private List<ProjectServiceProvisioner> projectServiceProvisionerList;

	@Autowired
	private JobService jobService;

	@Resource
	private Map<ServiceType, NodeProvisioningService> nodeProvisioningServiceByType;

	@Autowired
	private ProjectServiceMangementServiceProvider projectServiceMangementServiceProvider;

	@Autowired
	private ServiceHostCheckingStrategy serviceHostCheckingStrategy;

	@Autowired
	private ProjectServiceMigrationStrategy projectServiceMigrationStrategy;

	@Autowired
	private ServiceHostBalancingStrategy serviceHostBalancingStrategy;

	@Autowired
	@Qualifier("projectServiceManagementStrategyList")
	private ProjectServiceManagementStrategy projectServiceManagementStrategy;

	@Value("${alm.hub.enableHA}")
	private boolean enableHA = false;

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

		entityManager.persist(serviceProfile);
		entityManager.flush(); // To get the id's of the services

		// Schedule the jobs
		for (ProjectService service : serviceProfile.getProjectServices()) {
			jobService.schedule(new ProjectServicesProvisioningJob(service.getId()));
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
	public void doProvisionServices(Long projectServiceId) throws EntityNotFoundException, ProvisioningException,
			NoNodeAvailableException {
		ProjectService serviceToProvision = entityManager.find(ProjectService.class, projectServiceId);

		if (serviceToProvision == null) {
			LOGGER.error(String.format("Asked to provision missing service of [%s]", projectServiceId));
			return;
		}

		if (!projectServiceManagementStrategy.canHandle(serviceToProvision)) {
			LOGGER.info(String.format("No strategy to provision service [%s]", serviceToProvision.getType()));
			return;
		}

		if (!projectServiceManagementStrategy.isReadyToProvision(serviceToProvision)) {
			ProjectServicesProvisioningJob retryJob = new ProjectServicesProvisioningJob(projectServiceId);
			retryJob.setDeliveryDelayInMilliseconds(30 * 1000l);
			jobService.schedule(retryJob);
			return;
		}

		projectServiceManagementStrategy.provisionService(serviceToProvision);

	}

	@Override
	public ServiceHost convertToInternal(com.tasktop.c2c.server.cloud.domain.ServiceHost serviceHost) {
		return entityManager.find(ServiceHost.class, serviceHost.getId());
	}

	protected void verifyCanProvision(Project project) throws ProvisioningException {
		// REVIEW, how would this happen?
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
	public void initializeProjectServiceProfileTemplate() {

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

	@Autowired
	private ServiceAwareTenancyManager tenancyManager;

	@Override
	public List<ProjectServiceStatus> computeProjectServicesStatus(Project managedProject) {

		// Need to establish the project tenancy context so that it gets propagated to internal services (used to
		// compute database names and file locations).
		tenancyManager.establishTenancyContextFromProjectIdentifier(managedProject.getIdentifier());

		List<ProjectServiceStatus> result = new ArrayList<ProjectServiceStatus>();

		// REVIEW what happens with builds slave here.
		for (ProjectService service : managedProject.getProjectServiceProfile().getProjectServices()) {
			try {
				if (service.getServiceHost() == null || !service.getServiceHost().isAvailable()) {
					result.add(createFailureProjectServiceStatus(service));
				} else {
					tenancyManager.establishTenancyContext(service);
					ProjectServiceManagementService serviceMangementService = projectServiceMangementServiceProvider
							.getNewService(service.getServiceHost().getInternalNetworkAddress(), service.getType());

					result.add(serviceMangementService.retrieveServiceStatus(managedProject.getIdentifier()));
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
		String hostAddress = "<No Host>";
		if (projectService.getServiceHost() != null) {
			hostAddress = projectService.getServiceHost().getInternalNetworkAddress();
		}

		if (!projectServiceManagementStrategy.canHandle(projectService)) {
			LOGGER.info(String.format("No strategy to deprovision service [%s]", projectService.getType()));
			removeProjectService(projectService);
			return;
		}

		LOGGER.info(String.format("deprovisioning service [%s] on node [%s]", projectService.getType(), hostAddress));
		projectServiceManagementStrategy.deprovisionService(projectService);
		LOGGER.info("deprovisioning done");
	}

	@Override
	public void removeProjectService(ProjectService service) {
		if (service.getServiceHost() != null) {
			service.getServiceHost().getProjectServices().remove(service);
		}

		service.getProjectServiceProfile().getProjectServices().remove(service);

		entityManager.remove(service);
	}

	@Secured(Role.Admin)
	@Override
	public List<PoolStatus> computePoolStatus() {
		List<PoolStatus> result = new ArrayList<PoolStatus>(nodeProvisioningServiceByType.size());
		for (NodeProvisioningService nps : new HashSet<NodeProvisioningService>(nodeProvisioningServiceByType.values())) {
			result.add(nps.getStatus());
		}
		return result;
	}

	@Override
	public void handleConnectFailure(ProjectService service) {
		if (!enableHA) {
			return;
		}
		ServiceHost failedHost = entityManager.find(ServiceHost.class, service.getServiceHost().getId());
		entityManager.lock(failedHost, LockModeType.PESSIMISTIC_WRITE);
		entityManager.refresh(failedHost);

		if (!failedHost.isAvailable()) {
			LOGGER.warn(String.format(
					"Host [%s] is already marked as un-available. Not handling connection failure here.",
					failedHost.getInternalNetworkAddress()));
			return;
		}

		failedHost.setAvailable(false);
		CheckServiceHostStatusJob job = new CheckServiceHostStatusJob(failedHost.getId());
		job.setDeliveryDelayInMilliseconds(DOWN_SERVICE_RECHECK_DELAY);
		jobService.schedule(job);
		jobService.schedule(new HandleServiceHostFailureJob(failedHost.getId()));
	}

	@Override
	public void handleServiceHostFailure(Long serviceHostId) {
		ServiceHost host = entityManager.find(ServiceHost.class, serviceHostId);

		// Attempt to migrate services off the failed host
		for (ProjectService service : new ArrayList<ProjectService>(host.getProjectServices())) {
			try {
				NodeProvisioningService nodeProvisioningService = nodeProvisioningServiceByType.get(service.getType());
				if (nodeProvisioningService == null) {
					continue;
				}
				ServiceHost newHost = convertToInternal(nodeProvisioningService.provisionNode());
				tryMigrateService(service, newHost);
			} catch (NoNodeAvailableException e) {
				// continue
			}
		}
	}

	private void tryMigrateService(ProjectService service, ServiceHost newHost) {
		if (projectServiceMigrationStrategy.canMigrate(service)) {
			projectServiceMigrationStrategy.migrate(service, newHost);
		}
	}

	protected void balanceProjectServicesOntoHost(ServiceHost host) {
		if (!host.isAvailable()) {
			throw new IllegalStateException("Asked to migrate to down host");
		}

		// Only consider other hosts with the exact same config
		@SuppressWarnings("unchecked")
		List<ServiceHost> otherHosts = entityManager
				.createQuery(
						"SELECT host FROM " + ServiceHost.class.getSimpleName()
								+ " host WHERE host.serviceHostConfiguration = :config AND host != :host")
				.setParameter("config", host.getServiceHostConfiguration()).setParameter("host", host).getResultList();

		serviceHostBalancingStrategy.balance(host, otherHosts);
	}

	@Override
	public void checkServiceHostStatus(Long servicHostId) {
		ServiceHost serviceHost = entityManager.find(ServiceHost.class, servicHostId);

		if (serviceHost.isAvailable()) {
			LOGGER.info(String.format("Service host [%s] has been marked as back up",
					serviceHost.getInternalNetworkAddress()));
			return;
		}

		if (serviceHostCheckingStrategy.canCheckServiceHost(serviceHost)) {
			final boolean allServicesUp = serviceHostCheckingStrategy.checkServiceHost(serviceHost);

			if (allServicesUp) {
				LOGGER.info(String.format("Service host [%s] is back up. Marking as available",
						serviceHost.getInternalNetworkAddress()));
				serviceHost.setAvailable(true);
				balanceProjectServicesOntoHost(serviceHost);
			} else {
				CheckServiceHostStatusJob job = new CheckServiceHostStatusJob(servicHostId);
				job.setDeliveryDelayInMilliseconds(DOWN_SERVICE_RECHECK_DELAY);
				jobService.schedule(job);
			}
		} else {
			LOGGER.warn(String.format("Could not check host availability for host id:[%s] ip:[%s]. "
					+ "Host will stay marked as un-available.", serviceHost.getId(),
					serviceHost.getInternalNetworkAddress()));
		}
	}
}
