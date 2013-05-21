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
package com.tasktop.c2c.server.common.profile.web.ui.server;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.GetAppConfigAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetAppConfigResult;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
// Note the type parameters are just to work around super assuptions and still allow for extension
// net.customware.gwt.dispatch.server.standard.GenericUtils.getFirstTypeParameterDeclaredOnSuperclass(GenericUtils.java:41)
public class GetAppConfigActionHandler<A extends GetAppConfigAction, R extends GetAppConfigResult> extends
		AbstractProfileActionHandler<GetAppConfigAction, GetAppConfigResult> {

	@Override
	public GetAppConfigResult execute(GetAppConfigAction action, ExecutionContext context) throws DispatchException {

		Map<String, String> properties = new HashMap<String, String>();
		// TODO
		return new GetAppConfigResult(properties);
	}

}
