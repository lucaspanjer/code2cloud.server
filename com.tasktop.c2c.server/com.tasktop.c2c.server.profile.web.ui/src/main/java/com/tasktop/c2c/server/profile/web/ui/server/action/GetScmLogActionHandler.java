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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.ui.server.AbstractProfileActionHandler;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.profile.service.provider.ScmServiceProvider;
import com.tasktop.c2c.server.scm.service.ScmService;
import com.tasktop.c2c.server.scm.web.ui.client.shared.action.GetScmLogAction;
import com.tasktop.c2c.server.scm.web.ui.client.shared.action.GetScmLogResult;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GetScmLogActionHandler extends AbstractProfileActionHandler<GetScmLogAction, GetScmLogResult> {

	@Autowired
	private ScmServiceProvider scmServiceProvider;

	@Override
	public GetScmLogResult execute(GetScmLogAction action, ExecutionContext context) throws DispatchException {
		try {
			setTenancyContext(action.getProjectId());
			ScmService scmService = scmServiceProvider.getService(action.getProjectId());
			if (action.getBranchName() == null) {
				return new GetScmLogResult(scmService.getLog(action.getRepoName(), action.getRegion()));
			} else {
				return new GetScmLogResult(scmService.getLogForBranch(action.getRepoName(), action.getBranchName(),
						action.getRegion()));
			}
		} catch (EntityNotFoundException e) {
			handle(e);
		}
		throw new IllegalStateException();
	}
}
