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
package com.tasktop.c2c.server.hudson.configuration.service;

import hudson.model.TopLevelItem;
import hudson.model.Hudson;
import hudson.security.ACL;

import java.io.IOException;

import org.eclipse.hudson.security.team.Team;
import org.eclipse.hudson.security.team.TeamManager.TeamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Deprovisioner;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class HudsonServiceDeprovisioner implements Deprovisioner {

	private static final Logger LOG = LoggerFactory.getLogger(HudsonServiceDeprovisioner.class.getName());

	@Override
	public void deprovision(ProjectServiceConfiguration configuration) {

		String projectId = configuration.getProjectIdentifier();

		SecurityContext initialContext = SecurityContextHolder.getContext();
		try {
			SecurityContextHolder.createEmptyContext();
			SecurityContextHolder.getContext().setAuthentication(ACL.SYSTEM);
			Team team = Hudson.getInstance().getTeamManager().findTeam(projectId);

			// XXX Maybe there is a faster way than looking at every job?
			for (TopLevelItem item : Hudson.getInstance().getItems()) {
				if (team.isJobOwner(item.getId())) {
					try {
						item.delete();
					} catch (IOException e) {
						throw new RuntimeException(e);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}

			Hudson.getInstance().getTeamManager().removeTeam(team.getName());

		} catch (TeamNotFoundException e) {
			// could happen if no jobs ever created
			LOG.debug("Could not deprovision, missing team", e);
			return;
		} catch (IOException e) {
			LOG.error("Problem removing team", e);
			throw new RuntimeException(e);
		} finally {
			SecurityContextHolder.setContext(initialContext);
		}
	}

}
