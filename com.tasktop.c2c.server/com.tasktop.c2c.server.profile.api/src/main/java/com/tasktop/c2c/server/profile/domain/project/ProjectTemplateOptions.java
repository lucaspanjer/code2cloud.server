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
package com.tasktop.c2c.server.profile.domain.project;

import com.tasktop.c2c.server.common.service.domain.AbstractEntity;

/**
 * Contains the information needed to apply a project template to a project. Currently just a container of the template
 * and the targetProject, but this will be expanded with additional parameters.
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class ProjectTemplateOptions extends AbstractEntity {
	private ProjectTemplate template;
	private String targetProjectIdentifier;

	public ProjectTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ProjectTemplate template) {
		this.template = template;
	}

	public String getTargetProjectIdentifier() {
		return targetProjectIdentifier;
	}

	public void setTargetProjectIdentifier(String targetProjectIdentifier) {
		this.targetProjectIdentifier = targetProjectIdentifier;
	}

}
