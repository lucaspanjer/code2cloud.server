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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.tasktop.c2c.server.profile.web.ui.client.place.HudsonHeaderPlace;
import com.tasktop.c2c.server.profile.web.ui.client.view.Footer;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class HudsonAPI {

	public static void attachHeader(String elementId, String projectIdentifier) {
		HudsonHeaderPlace place = HudsonHeaderPlace.createPlace(projectIdentifier, elementId);
		place.go();
	}

	public static void attachFooter(String elementId) {

		Footer footer = GWT.create(Footer.class);

		HTML html = new HTML(footer.asWidget().getElement().getString());
		DOM.getElementById(elementId).appendChild(html.getElement());
	}

	public static native void exportStaticMethod() /*-{
													$wnd.attachHeader =
													$entry(@com.tasktop.c2c.server.profile.web.ui.client.HudsonAPI::attachHeader(Ljava/lang/String;Ljava/lang/String;));
													$wnd.attachFooter =
													$entry(@com.tasktop.c2c.server.profile.web.ui.client.HudsonAPI::attachFooter(Ljava/lang/String;));
													$wnd.hudsonApiReady = true;
													}-*/;

	public static native void onApiReady() /*-{
											if($wnd.onHudsonApiReady != null) {
												$wnd.onHudsonApiReady();
												}
											}-*/;

}
