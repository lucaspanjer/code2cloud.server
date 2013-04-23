package com.tasktop.c2c.server.internal.profile.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.tenancy.context.TenancyContextHolder;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.cloud.service.NodeProvisioningService;
import com.tasktop.c2c.server.common.service.NoNodeAvailableException;
import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceClient;
import com.tasktop.c2c.server.configuration.service.ProjectServiceMangementServiceProvider;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;
import com.tasktop.c2c.server.profile.service.HudsonService;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.profile.service.provider.HudsonServiceProvider;

/**
 * Implements standard way to manage project services. Uses the NodeProvisioningService to get an available node. And
 * then calls the ProjectServiceManagmentService on that node to provision the service.
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class StandardProjectServiceManagementStrategy implements ProjectServiceManagementStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(StandardProjectServiceManagementStrategy.class);

	@Resource
	protected Map<ServiceType, NodeProvisioningService> nodeProvisioningServiceByType;

	@Autowired(required = false)
	protected List<ProjectServiceProvisioner> projectServiceProvisionerList;

	@Autowired
	protected ProjectServiceMangementServiceProvider projectServiceMangementServiceProvider;

	@Autowired
	protected ServiceAwareTenancyManager tenancyManager;

	@Autowired
	protected ProfileServiceConfiguration configuration;

	@Autowired
	protected InternalProjectServiceService internalProjectServiceService;

	@Override
	public boolean canHandle(ProjectService service) {
		switch (service.getType()) {
		case BUILD:
		case MAVEN:
		case SCM:
		case TASKS:
		case WIKI:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean isReadyToProvision(ProjectService service) {
		return true;
	}

	@Override
	public void provisionService(ProjectService service) throws NoNodeAvailableException, ProvisioningException {

		AuthUtils.assumeSystemIdentity(service.getProjectServiceProfile().getProject().getIdentifier());

		NodeProvisioningService nodeProvisioningService = nodeProvisioningServiceByType.get(service.getType());
		if (nodeProvisioningService == null) {
			LOGGER.info("Not setting up service " + service.getType() + " no node provisionService available");
			return;
		}

		LOGGER.debug("provisioning node for " + service.getType());
		com.tasktop.c2c.server.cloud.domain.ServiceHost domainServiceHost = nodeProvisioningService.provisionNode();
		ServiceHost serviceHost = internalProjectServiceService.convertToInternal(domainServiceHost);
		LOGGER.debug("provisioned to " + serviceHost.getInternalNetworkAddress());
		service.setServiceHost(serviceHost);

		// Pre-provision in the hub if needed
		if (projectServiceProvisionerList != null) {
			for (ProjectServiceProvisioner projectServiceProvisioner : projectServiceProvisionerList) {
				if (projectServiceProvisioner.supports(service)) {
					projectServiceProvisioner.provision(service);
				}
			}
		}

		// Now configure the host for the new project.
		ProjectServiceManagementServiceClient nodeConfigurationService = projectServiceMangementServiceProvider
				.getNewService(domainServiceHost.getInternalNetworkAddress(), service.getType());

		updateTemplateServiceConfiguration(service);

		try {
			tenancyManager.establishTenancyContext(service);
			LOGGER.debug("configuring node for " + service.getType());
			ProjectServiceConfiguration config = createProjectServiceConfiguration(service.getProjectServiceProfile()
					.getProject());
			nodeConfigurationService.provisionService(config);
			waitForServiceToComeUp(service);
			LOGGER.debug("configuring done");
		} catch (Exception e) {
			throw new ProvisioningException("Caught exception while configuring node", e);
		} finally {
			TenancyContextHolder.clearContext();
		}

	}

	@Autowired
	protected HudsonServiceProvider hudsonServiceProvider;

	private void waitForServiceToComeUp(ProjectService service) {
		switch (service.getType()) {
		case BUILD:
			waitForHudson(service);
			break;

		default:
			// nothing to do
			break;
		}

	}

	/**
	 * @param service
	 */
	protected void waitForHudson(ProjectService service) {
		HudsonService hudsonService = hudsonServiceProvider.getHudsonService(service.getProjectServiceProfile()
				.getProject().getIdentifier());
		int sleepAmount = 1000;
		boolean ready = false;
		for (int i = 0; i < 7; i++) { // 1s, 2s, 4s, 8s, 16s, 32s, 1m
			if (hudsonService.isHudsonReady()) {
				ready = true;
				break;
			} else {
				LOGGER.debug("Hudson not ready yet, sleeping for [%d]ms", sleepAmount);
				try {
					Thread.sleep(sleepAmount);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				sleepAmount = sleepAmount * 2;
			}
		}
		if (!ready) {
			LOGGER.warn(String.format("Hudson never came up for project [%s].", service.getProjectServiceProfile()
					.getProject().getIdentifier()));
			// Continue here and let the job commit
		}
	}

	protected void updateTemplateServiceConfiguration(ProjectService service) {
		switch (service.getType()) {
		case BUILD:
			// Replace our marker string with the actual project identifier
			service.setInternalUriPrefix(service.getInternalUriPrefix().replace("APPID",
					service.getProjectServiceProfile().getProject().getIdentifier()));
			break;
		default:
			// Nothing to do
			break;
		}
	}

	private ProjectServiceConfiguration createProjectServiceConfiguration(Project project) {
		ProjectServiceConfiguration config = new ProjectServiceConfiguration();
		config.setProjectIdentifier(project.getIdentifier());
		if (project.getOrganization() != null) {
			config.setOrganizationIdentifier(project.getOrganization().getIdentifier());
		}
		config.setShortProjectIdentifer(project.getShortIdentifier());
		config.setProperty(ProjectServiceConfiguration.PROFILE_HOSTNAME, configuration.getBaseWebHost());
		config.setProperty(ProjectServiceConfiguration.ORG_PROFILE_HOSTNAME, configuration.getWebHost());
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

	@Override
	public void deprovisionService(ProjectService projectService) {
		try {
			tenancyManager.establishTenancyContext(projectService);

			if (projectService.getServiceHost() != null) {
				ProjectServiceManagementServiceClient nodeConfigurationService = projectServiceMangementServiceProvider
						.getNewService(projectService.getServiceHost().getInternalNetworkAddress(),
								projectService.getType());

				ProjectServiceConfiguration config = createProjectServiceConfiguration(projectService
						.getProjectServiceProfile().getProject());

				nodeConfigurationService.deprovisionService(config);
			}
			internalProjectServiceService.removeProjectService(projectService);
		} finally {
			TenancyContextHolder.clearContext();
		}
	}

}
