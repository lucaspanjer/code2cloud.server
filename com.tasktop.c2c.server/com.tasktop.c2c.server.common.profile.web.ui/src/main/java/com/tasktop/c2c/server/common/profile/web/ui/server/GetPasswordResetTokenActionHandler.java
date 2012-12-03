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
package com.tasktop.c2c.server.common.profile.web.ui.server;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.GetPasswordResetTokenAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetPasswordResetTokenResult;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GetPasswordResetTokenActionHandler extends
		AbstractProfileActionHandler<GetPasswordResetTokenAction, GetPasswordResetTokenResult> {

	public GetPasswordResetTokenActionHandler() {
		super();
	}

	@Override
	public GetPasswordResetTokenResult execute(GetPasswordResetTokenAction action, ExecutionContext context)
			throws DispatchException {
		try {
			return new GetPasswordResetTokenResult(profileWebService.getPasswordResetToken(action.getToken()));
		} catch (EntityNotFoundException e) {
			throw new ActionException(e);
		}
	}
}
