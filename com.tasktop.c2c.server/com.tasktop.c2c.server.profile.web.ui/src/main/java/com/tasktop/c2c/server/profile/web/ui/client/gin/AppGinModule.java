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

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.tasktop.c2c.server.common.profile.web.client.AppState;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMappingRegistry;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectsPlace;
import com.tasktop.c2c.server.common.web.client.notification.NotificationPanel;
import com.tasktop.c2c.server.common.web.client.notification.Notifier;
import com.tasktop.c2c.server.profile.web.ui.client.AppShell;
import com.tasktop.c2c.server.profile.web.ui.client.action.DispatchServiceAsync;
import com.tasktop.c2c.server.profile.web.ui.client.activity.MainActivityMapper;
import com.tasktop.c2c.server.profile.web.ui.client.place.AppHistoryMapper;
import com.tasktop.c2c.server.profile.web.ui.client.place.AppPlaceController;
import com.tasktop.c2c.server.profile.web.ui.client.view.StandardApplicationView;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class AppGinModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(AppShell.class).toProvider(StandardApplicationViewProvider.class).in(Singleton.class);
		bind(NotificationPanel.class).in(Singleton.class);
		bind(AppState.class).in(Singleton.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).to(AppHistoryMapper.class).in(Singleton.class);
		bind(MainActivityMapper.class).in(Singleton.class);
		bind(PlaceHistoryHandler.class).toProvider(PlaceHistoryHandlerProvider.class).in(Singleton.class);
		bind(PlaceController.class).to(AppPlaceController.class).in(Singleton.class);
		bind(Notifier.class).to(NotificationPanel.class).in(Singleton.class);
		bind(Scheduler.class).toProvider(SchedulerProvider.class).in(Singleton.class);
		bind(DispatchAsync.class).to(DispatchServiceAsync.class).in(Singleton.class);
		bind(PageMappingRegistry.class).in(Singleton.class);

	}

	public static class SchedulerProvider implements Provider<Scheduler> {

		@Override
		public Scheduler get() {
			return Scheduler.get();
		}
	}

	public static class StandardApplicationViewProvider implements Provider<StandardApplicationView> {

		@Override
		public StandardApplicationView get() {
			return StandardApplicationView.getInstance();
		}
	}

	public static class PlaceHistoryHandlerProvider implements Provider<PlaceHistoryHandler> {

		private final PlaceHistoryMapper placeHistoryMapper;
		private final PlaceController placeController;
		private final EventBus eventBus;

		@Inject
		PlaceHistoryHandlerProvider(PlaceHistoryMapper placeHistoryMapper, PlaceController placeController,
				EventBus eventBus) {
			this.placeHistoryMapper = placeHistoryMapper;
			this.placeController = placeController;
			this.eventBus = eventBus;
		}

		public PlaceHistoryHandler get() {
			PlaceHistoryHandler placeHistoryHandler = new PlaceHistoryHandler(placeHistoryMapper);
			placeHistoryHandler.register(placeController, eventBus, ProjectsPlace.createPlace());
			return placeHistoryHandler;
		}
	}

}
