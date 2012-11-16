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

import com.google.gwt.user.client.ui.RootPanel;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;

/**
 * @author Clint (Tasktop Technologies Inc.)
 * 
 */
public class BootAPI {

	public static void boot() {
		RootPanel.get().add(AppGinjector.get.instance().getNotificationPanel());
		AppGinjector.get.instance().getApp().run(RootPanel.get());
	}

	public static native void exportStaticMethod() /*-{
													$wnd.c2cBoot =
													$entry(@com.tasktop.c2c.server.profile.web.ui.client.BootAPI::boot());
													$wnd.bootApiReady = true;
													}-*/;

	public static native void onApiReady() /*-{
											if($wnd.onBootApiReady != null) {
												$wnd.onBootApiReady();
											}
											}-*/;

}
