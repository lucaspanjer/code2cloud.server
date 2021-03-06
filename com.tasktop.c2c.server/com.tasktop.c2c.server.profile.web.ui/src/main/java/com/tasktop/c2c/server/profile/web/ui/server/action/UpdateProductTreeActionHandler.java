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
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.tasks.shared.action.UpdateProductTreeAction;
import com.tasktop.c2c.server.tasks.shared.action.UpdateProductTreeResult;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class UpdateProductTreeActionHandler extends
		AbstractTaskActionHandler<UpdateProductTreeAction, UpdateProductTreeResult> {

	@Override
	public UpdateProductTreeResult execute(UpdateProductTreeAction action, ExecutionContext context)
			throws DispatchException {
		try {
			return new UpdateProductTreeResult(getService(action.getProjectId()).updateProductTree(action.getProduct()));
		} catch (ValidationException e) {
			handle(e);
		} catch (EntityNotFoundException e) {
			throw new ActionException(e);
		}
		throw new IllegalStateException();
	}

}
