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
package com.googlecode.google_code_prettify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GoogleCodePrettifyUtil {
	private static GoogleCodePrettifyBundle bundle = GWT.create(GoogleCodePrettifyBundle.class);

	private static boolean injected = false;

	public static void run() {
		bundle.prettifyCss().ensureInjected();

		if (!injected) {
			ScriptInjector.fromString(bundle.prettifyJavaScript().getText()).setWindow(window()).inject();
			injected = true;
		}

		ProfileGinjector.get.instance().getScheduler().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				doPrettify();

			}
		});
	}

	private static native JavaScriptObject window()/*-{
													return $wnd;
													}-*/;

	private static native void doPrettify() /*-{
														$wnd.prettyPrint();
														}-*/;
}
