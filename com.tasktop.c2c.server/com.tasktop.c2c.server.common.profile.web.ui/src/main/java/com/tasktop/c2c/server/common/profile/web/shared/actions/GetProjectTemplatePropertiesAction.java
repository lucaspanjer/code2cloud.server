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
package com.tasktop.c2c.server.common.profile.web.shared.actions;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.web.client.util.ExceptionsUtil;
import com.tasktop.c2c.server.common.web.shared.CachableReadAction;
import com.tasktop.c2c.server.common.web.shared.KnowsErrorMessageAction;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class GetProjectTemplatePropertiesAction implements Action<GetProjectTemplatePropertiesResult>,
		CachableReadAction, KnowsErrorMessageAction {

	private ProjectTemplate projectTemplate;

	/**
	 * @param projectTemplate
	 */
	public GetProjectTemplatePropertiesAction(ProjectTemplate projectTemplate) {
		this.projectTemplate = projectTemplate;
	}

	protected GetProjectTemplatePropertiesAction() {
	}

	public ProjectTemplate getProjectTemplate() {
		return projectTemplate;
	}

	@Override
	public String getErrorMessage(DispatchException e) {
		if (ExceptionsUtil.isEntityNotFound(e)) {
			return ProfileGinjector.get.instance().getCommonProfileMessages()
					.projectNotFound(projectTemplate.getProjectId());
		}
		return null;
	}

}
