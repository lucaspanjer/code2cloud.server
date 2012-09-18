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
package com.tasktop.c2c.server.profile.web.ui.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.web.client.event.AppScrollEvent;
import com.tasktop.c2c.server.profile.web.ui.client.AppShell;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;

public class StandardApplicationView extends Composite implements AppShell {

	interface Binder extends UiBinder<HTMLPanel, StandardApplicationView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	AcceptsOneWidget headerContainer;
	@UiField
	AcceptsOneWidget contentContainer;

	private boolean scrollNotificationScheduled = false;

	private static StandardApplicationView instance;

	public static StandardApplicationView getInstance() {
		if (instance == null) {
			ProfileGinjector.get.instance().getAppResources().appCss().ensureInjected();
			instance = new StandardApplicationView();
		}
		return instance;
	}

	private StandardApplicationView() {
		if (!GWT.isClient()) {
			return;
		}
		initWidget(uiBinder.createAndBindUi(this));
		// It would be nice for this not to be here, but for now it is.
		addDomHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				// Debouncing logic
				if (!scrollNotificationScheduled) {
					scrollNotificationScheduled = true;
					AppGinjector.get.instance().getScheduler().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
						@Override
						public boolean execute() {
							scrollNotificationScheduled = false;
							AppGinjector.get.instance().getEventBus().fireEvent(new AppScrollEvent());
							return false;
						}
					}, 100);
				}
			}
		}, ScrollEvent.getType());
	}

	@Override
	public AcceptsOneWidget getHeaderRegion() {
		return headerContainer;
	}

	@Override
	public AcceptsOneWidget getMainContentRegion() {
		return contentContainer;
	}

}
