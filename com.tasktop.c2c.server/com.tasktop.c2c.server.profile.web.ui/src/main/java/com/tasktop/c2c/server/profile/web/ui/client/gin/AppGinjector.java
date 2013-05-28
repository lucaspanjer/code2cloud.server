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
package com.tasktop.c2c.server.profile.web.ui.client.gin;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.web.client.view.CommonGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.App;
import com.tasktop.c2c.server.profile.web.ui.client.AppConfiguration;
import com.tasktop.c2c.server.profile.web.ui.client.AppShell;
import com.tasktop.c2c.server.profile.web.ui.client.activity.HeaderActivityMapper;
import com.tasktop.c2c.server.profile.web.ui.client.activity.MainActivityMapper;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.ScmRepositoryUrlBuilder;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public interface AppGinjector extends ProfileGinjector {
	PlaceHistoryHandler getPlaceHistoryHandler();

	PlaceHistoryMapper getPlaceHistoryMapper();

	AppShell getAppShell();

	PlaceController getPlaceController();

	App getApp();

	MainActivityMapper getMainActivityMapper();

	HeaderActivityMapper getHeaderActivityMapper();

	ProfileMessages getProfileMessages();

	AppConfiguration getConfiguration();

	ScmRepositoryUrlBuilder getScmRepositoryUrlBuilder();

	public static class get {
		private static AppGinjector instance = null;

		public static synchronized AppGinjector instance() {
			return instance;
		}

		public static synchronized AppGinjector override(AppGinjector appGinjector) {
			instance = appGinjector;
			CommonGinjector.get.override(appGinjector);
			ProfileGinjector.get.override(appGinjector);
			return appGinjector;
		}
	}

}
