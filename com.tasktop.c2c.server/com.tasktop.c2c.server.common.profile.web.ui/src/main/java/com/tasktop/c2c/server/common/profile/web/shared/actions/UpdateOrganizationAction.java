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
import com.tasktop.c2c.server.profile.domain.project.Organization;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class UpdateOrganizationAction implements Action<UpdateOrganizationResult>, WriteAction {
	private Organization organization;

	public UpdateOrganizationAction(Organization organization) {
		this.organization = organization;
	}

	protected UpdateOrganizationAction() {
	}

	public Organization getOrganization() {
		return organization;
	}

}
