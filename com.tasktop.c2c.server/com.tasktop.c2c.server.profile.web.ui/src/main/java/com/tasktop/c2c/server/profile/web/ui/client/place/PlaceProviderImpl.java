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
package com.tasktop.c2c.server.profile.web.ui.client.place;

import com.tasktop.c2c.server.common.profile.web.client.place.IPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.OrganizationProjectsPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.PlaceProvider;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectsPlace;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class PlaceProviderImpl implements PlaceProvider {

	@Override
	public IPlace getDefaultPlace() {
		return ProjectsPlace.createPlace();
	}

	@Override
	public IPlace getOrganizationPlace(String orgId) {
		return OrganizationProjectsPlace.createPlaceForOrg(orgId);
	}

	@Override
	public IPlace getOrganizationNewProjectPlace(String orgId) {
		return OrganizationNewProjectPlace.createPlace(orgId);
	}

	@Override
	public IPlace getOrganizationAdminPlace(String orgId) {
		return OrganizationAdminPlace.createPlace(orgId);
	}

	@Override
	public IPlace getAfterSignoutPlace() {
		return ProjectsPlace.createPlace();
	}

}
