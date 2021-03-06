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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.tasktop.c2c.server.common.profile.web.client.place.IPlace;
import com.tasktop.c2c.server.common.profile.web.shared.Credentials.AuthType;
import com.tasktop.c2c.server.common.web.client.event.EmbeddedNavigationEvent;
import com.tasktop.c2c.server.common.web.client.event.EmbeddedNavigationEventHandler;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.profile.web.ui.client.event.LogoutEvent;
import com.tasktop.c2c.server.profile.web.ui.client.event.LogoutEventHandler;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.Footer;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class App {

	private static final AppGinjector injector = AppGinjector.get.instance();

	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public void run(HasWidgets.ForIsWidget root) {
		addHandlers();

		root.add(injector.getAppShell());

		Footer footer = GWT.create(Footer.class);
		root.add(footer);

		ActivityManager headerActivityManager = new ActivityManager(injector.getHeaderActivityMapper(),
				injector.getEventBus());
		headerActivityManager.setDisplay(injector.getAppShell().getHeaderRegion());
		ActivityManager mainActivityManager = new ActivityManager(injector.getMainActivityMapper(),
				injector.getEventBus());
		mainActivityManager.setDisplay(injector.getAppShell().getMainContentRegion());

		injector.getPlaceHistoryHandler().handleCurrentHistory();
	}

	public static void registerGAPageVisit() {
		if (injector.getConfiguration().isEnableGoogleAnalytics()) {
			registerGAPageVisitInternal();
		}
	}

	private static native void registerGAPageVisitInternal()
	/*-{
		var url = decodeURIComponent($wnd.location.toString());
		$wnd._gaq.push([ '_trackPageview', url.split('#')[1] ]);
	}-*/;

	private static Logger appLogger = Logger.getLogger("App");

	private void addHandlers() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				injector.getNotifier().displayMessage(Message.createErrorMessage(profileMessages.clientSideErrorOccurred()));
				appLogger.log(Level.SEVERE, profileMessages.clientSideError(), e);
			}
		});

		final EventBus eventBus = injector.getEventBus();
		eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			public void onPlaceChange(PlaceChangeEvent event) {
				registerGAPageVisit();
			}
		});

		eventBus.addHandler(EmbeddedNavigationEvent.TYPE, new EmbeddedNavigationEventHandler() {
			@Override
			public void onNavigate(String navigation) {
				if (!navigation.equals(History.getToken())) {
					History.newItem(navigation, false);
				}
			}
		});

		eventBus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {

			@Override
			public void onLogout() {
				if (injector.getAppState().getCredentials() == null) {
					return;
				}
				AuthType authType = injector.getAppState().getCredentials().getAuthType();
				if (authType == null || AuthType.LOCAL.equals(authType)) {
					logoutFromLocalAuth();
				} else if (AuthType.SSO.equals(authType)) {
					logougFromSsoAuth();
				}

				// Now that we've made our call, wipe out our local credentials.
				injector.getAppState().setCredentials(null);
			}
		});

	}

	protected void logoutFromLocalAuth() {
		injector.getProfileService().logout(new AsyncCallbackSupport<Boolean>() {
			@Override
			public void success(Boolean result) {
				injector.getAppState().setCredentials(null);
				IPlace place = injector.getPlaceProvider().getAfterSignoutPlace();
				place.displayOnArrival(Message.createSuccessMessage(profileMessages.signedOut()));
				place.go();
			}
		});
	}

	protected void logougFromSsoAuth() {
		Window.Location.assign(injector.getPlaceProvider().getSSOLogoutHref());
	}
}
