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
package com.tasktop.c2c.server.profile.web.ui.server.action;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.FindProfilesResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.FindProfilesToAddToProjectAction;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.profile.domain.project.Profile;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class FindProfilesActionHandler extends
		AbstractProfileActionHandler<FindProfilesToAddToProjectAction, FindProfilesResult> {

	public FindProfilesActionHandler() {
		super();
	}

	@Override
	public FindProfilesResult execute(FindProfilesToAddToProjectAction action, ExecutionContext context)
			throws DispatchException {
		// FIXME should filter out those already in the project
		QueryResult<com.tasktop.c2c.server.profile.domain.internal.Profile> internalResult = profileService
				.findProfiles(action.getQueryString(), new Region(0, action.getLimit()), null);
		QueryResult<Profile> result = new QueryResult<Profile>();
		result.setOffset(internalResult.getOffset());
		result.setResultPage(webServiceDomain.copyProfiles(internalResult.getResultPage()));
		result.setTotalResultSize(internalResult.getTotalResultSize());
		result.setPageSize(internalResult.getPageSize());

		return new FindProfilesResult(result);
	}
}
