/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.internal.profile.service.template;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.profile.domain.build.JobSummary;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata.BuildJobConfigReplacement;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;
import com.tasktop.c2c.server.profile.service.HudsonService;
import com.tasktop.c2c.server.profile.service.ProfileServiceConfiguration;
import com.tasktop.c2c.server.profile.service.provider.HudsonServiceProvider;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class HudsonBuildServiceCloner extends BaseProjectServiceCloner {

	@Autowired
	protected HudsonServiceProvider hudsonServiceProvider;

	@Autowired
	protected ProfileServiceConfiguration profileServiceConfiguration;

	/**
	 * @param serviceType
	 */
	protected HudsonBuildServiceCloner() {
		super(ServiceType.BUILD);
	}

	@Override
	public void doClone(CloneContext context) {

		ProjectService templateService = context.getTemplateService();
		ProjectService targetService = context.getTargetService();

		AuthUtils.assumeSystemIdentity(templateService.getProjectServiceProfile().getProject().getIdentifier());
		HudsonService templateHudsonService = hudsonServiceProvider.getHudsonService(templateService
				.getProjectServiceProfile().getProject().getIdentifier());

		List<JobSummary> jobs = templateHudsonService.getStatus().getJobs();

		List<String> jobConfigXmls = new ArrayList<String>();
		for (JobSummary job : jobs) {
			String configXml = templateHudsonService.getJobConfigXml(job.getName());
			String newId = targetService.getProjectServiceProfile().getProject().getIdentifier() + "_" + job.getName();
			newId = newId.toLowerCase();
			configXml = replaceIdElement(configXml, newId);
			configXml = rewriteConfigUrls(configXml, templateService.getProjectServiceProfile().getProject(),
					targetService.getProjectServiceProfile().getProject());
			configXml = rewriteForTemplateMetadata(job.getName(), configXml, context);
			jobConfigXmls.add(configXml);
		}

		AuthUtils.assumeSystemIdentity(targetService.getProjectServiceProfile().getProject().getIdentifier());
		HudsonService targetHudsonService = hudsonServiceProvider.getHudsonService(targetService
				.getProjectServiceProfile().getProject().getIdentifier());

		for (int i = 0; i < jobs.size(); i++) {
			targetHudsonService.createNewJob(jobs.get(i).getName(), jobConfigXmls.get(i));
		}

	}

	// / This is needed to work around hudson issue https://q.tasktop.com/alm/#projects/c2c/task/5618
	static String replaceIdElement(String configXml, String newId) {
		return configXml.replaceFirst("<id>(.*)</id>", "<id>" + newId + "</id>");
	}

	protected String rewriteConfigUrls(String configXml, Project templateProject, Project targetProject) {
		String templateScmUrlPath = profileServiceConfiguration.getHostedScmUrlPath(templateProject.getIdentifier());
		String targetScmUrlPath = profileServiceConfiguration.getHostedScmUrlPath(targetProject.getIdentifier());
		configXml = configXml.replace(templateScmUrlPath, targetScmUrlPath);

		if (profileServiceConfiguration.isPrefixHostnameWithOrgId()) {
			String origninalOrgTenant = TenancyUtil.getCurrentTenantOrganizationIdentifer();
			try {
				TenancyUtil.setOrganizationTenancyContext(templateProject.getOrganization().getIdentifier());
				String templateHostName = profileServiceConfiguration.getWebHost();
				TenancyUtil.setOrganizationTenancyContext(targetProject.getOrganization().getIdentifier());
				String targetHostName = profileServiceConfiguration.getWebHost();
				configXml = configXml.replace(templateHostName, targetHostName);
			} finally {
				TenancyUtil.setOrganizationTenancyContext(origninalOrgTenant);
			}
		}

		return configXml;
	}

	protected String rewriteForTemplateMetadata(String jobName, String configXml, CloneContext context) {
		ProjectTemplateMetadata metadata = context.getProjectTemplateMetadata();

		if (metadata == null || metadata.getBuildJobReplacements() == null) {
			return configXml;
		}

		for (BuildJobConfigReplacement replacement : metadata.getBuildJobReplacements()) {
			if (replacement.getJobName().equals(jobName)) {
				ProjectTemplateProperty property = context.getProperty(replacement.getProperty().getId());
				if (property == null) {
					// TODO ERROR?
					continue;
				}
				configXml = configXml.replace(replacement.getPatternToReplace(), property.getValue());
			}
		}

		return configXml;
	}

}
