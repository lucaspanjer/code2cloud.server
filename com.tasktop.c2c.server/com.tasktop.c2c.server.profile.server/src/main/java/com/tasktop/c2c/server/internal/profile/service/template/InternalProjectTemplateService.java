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

import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateMetadata;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface InternalProjectTemplateService {

	void doCloneProjectService(Long sourceProjectServiceId, Long targetProjectServiceId, Long userId,
			List<ProjectTemplateProperty> properties, ProjectTemplateMetadata metadata);

}
