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
package com.tasktop.c2c.server.common.profile.web.shared.actions;

import net.customware.gwt.dispatch.shared.Action;


import com.tasktop.c2c.server.common.web.shared.WriteAction;
import com.tasktop.c2c.server.profile.domain.project.Project;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class UpdateProjectAction implements Action<UpdateProjectResult>, WriteAction {
	private Project project;

	public UpdateProjectAction(Project project) {
		this.project = project;
	}

	protected UpdateProjectAction() {
	}

	public Project getProject() {
		return project;
	}

}