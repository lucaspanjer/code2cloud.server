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
package com.tasktop.c2c.server.profile.web.ui.client;

import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.tasktop.c2c.server.profile.web.ui.client.action.DispatchServiceAsync;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class HudsonAPIEntryPoint extends ProfileEntryPoint {

	@Override
	public void onModuleLoad() {
		updateServiceTarget((ServiceDefTarget) DispatchServiceAsync.getRealService(), "hudsonApi", "dispatch");
		HudsonAPI.exportStaticMethod();
		HudsonAPI.onApiReady();
	}

}
