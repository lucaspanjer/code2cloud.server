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

import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplateProperty;

/**
 * Responsible for cloning elements from a project template's service into a target project's service
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceCloner {

	public class CloneContext {
		private Profile user;
		private ProjectService templateService;
		private ProjectService targetService;
		private List<ProjectTemplateProperty> properties;

		public Profile getUser() {
			return user;
		}

		public void setUser(Profile user) {
			this.user = user;
		}

		public ProjectService getTemplateService() {
			return templateService;
		}

		public void setTemplateService(ProjectService templateService) {
			this.templateService = templateService;
		}

		public ProjectService getTargetService() {
			return targetService;
		}

		public void setTargetService(ProjectService targetService) {
			this.targetService = targetService;
		}

		public List<ProjectTemplateProperty> getProperties() {
			return properties;
		}

		public void setProperties(List<ProjectTemplateProperty> properties) {
			this.properties = properties;
		}

		public ProjectTemplateProperty getProperty(String propertyId) {
			if (properties != null) {
				for (ProjectTemplateProperty property : properties) {
					if (property.getId().equals(propertyId)) {
						return property;
					}
				}

			}
			return null;
		}

	}

	boolean canClone(ProjectService service);

	void doClone(CloneContext context);

	boolean isReadyToClone(CloneContext context);

	List<ProjectTemplateProperty> getProperties(ProjectService sourceService);
}
