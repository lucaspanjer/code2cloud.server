/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
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

import java.util.List;

import net.customware.gwt.dispatch.shared.AbstractSimpleResult;

import com.tasktop.c2c.server.profile.domain.project.Organization;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class GetOwnedOrganizationsResult extends AbstractSimpleResult<List<Organization>> {

	public GetOwnedOrganizationsResult() {
	}

	public GetOwnedOrganizationsResult(List<Organization> value) {
		super(value);
	}
}
