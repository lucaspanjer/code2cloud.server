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
package com.tasktop.c2c.server.profile.web.ui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.tasktop.c2c.server.common.profile.web.client.AppState;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.ProfileServiceAsync;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMappingRegistry;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.view.CommonGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjectorBinding;
import com.tasktop.c2c.server.profile.web.ui.client.navigation.ProfilePageMappings;
import com.tasktop.c2c.server.tasks.client.TaskPageMappings;
import com.tasktop.c2c.server.wiki.web.ui.client.WikiPageMappings;

public class ProfileEntryPoint implements EntryPoint {

	private static ProfileEntryPoint instance;
	protected AppGinjector injector;

	public ProfileEntryPoint() {
		instance = this;

		// FIXME this could be in a better place
		AsyncCallbackSupport.setErrorHandler(new ExceptionMessageProvider());

		initializeInjector();
		registerPageMappings();
	}

	protected void registerPageMappings() {
		PageMappingRegistry pageRegistry = AppGinjector.get.instance().getPageMappingRegistry();
		pageRegistry.register(new ProfilePageMappings());
		pageRegistry.register(new TaskPageMappings());
		pageRegistry.register(new WikiPageMappings());
	}

	public static ProfileEntryPoint getInstance() {
		return instance;
	}

	protected void initializeInjector() {
		if (GWT.isClient()) {
			injector = GWT.create(AppGinjectorBinding.class);
		} else {
			injector = AppGinjector.get.instance(); // For testing
		}
		AppGinjector.get.override(injector);
		CommonGinjector.get.override(injector);
		ProfileGinjector.get.override(injector);
	}

	public EventBus getEventBus() {
		return injector.getEventBus();
	}

	public ProfileServiceAsync getProfileService() {
		return injector.getProfileService();
	}

	public BuildServiceAsync getBuildService() {
		return injector.getBuildService();
	}

	public DeploymentServiceAsync getDeploymentService() {
		return injector.getDeploymentService();
	}

	public AppState getAppState() {
		return injector.getAppState();
	}

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(injector.getNotificationPanel());
		injector.getApp().run(RootPanel.get());
	}

	protected void updateServiceTarget(ServiceDefTarget target, String servletMappingBase, String servletMappingSuffix) {
		String ep = target.getServiceEntryPoint();

		String expectedPath = Path.getBasePath() + "/" + servletMappingBase + "/" + servletMappingSuffix;
		int i = ep.lastIndexOf(expectedPath);

		if (i != -1) {
			ep = ep.substring(0, i) + Path.getBasePath() + "/profile" + "/" + servletMappingSuffix;
		}
		target.setServiceEntryPoint(ep);
	}
}
