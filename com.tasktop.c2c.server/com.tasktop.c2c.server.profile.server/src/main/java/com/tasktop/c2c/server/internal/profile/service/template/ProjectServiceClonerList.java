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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component("projectServiceClonerList")
public class ProjectServiceClonerList implements ProjectServiceCloner {

	@Autowired
	private List<ProjectServiceCloner> cloners;

	@Override
	public boolean canClone(ProjectService service) {
		return getCloner(service) != null;
	}

	private ProjectServiceCloner getCloner(ProjectService service) {
		for (ProjectServiceCloner cloner : cloners) {
			if (cloner.canClone(service)) {
				return cloner;
			}
		}
		return null;
	}

	@Override
	public void doClone(ProjectService templateService, ProjectService targetProjectService) {
		ProjectServiceCloner cloner = getCloner(templateService);
		cloner.doClone(templateService, targetProjectService);
	}

	@Override
	public boolean isReadyToClone(ProjectService sourceService, ProjectService targetProjectService) {
		return getCloner(sourceService).isReadyToClone(sourceService, targetProjectService);
	}

}
