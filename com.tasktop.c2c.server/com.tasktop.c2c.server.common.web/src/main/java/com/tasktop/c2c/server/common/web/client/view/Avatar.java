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
package com.tasktop.c2c.server.common.web.client.view;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.tasktop.c2c.server.common.web.client.navigation.Path;

/**
 * Utility for working with avatars.
 * 
 * @author David Green (Tasktop Technologies Inc.)
 */
public class Avatar {
	public enum Size {
		// NOTE: changes to these sizes need a corresponding file images/default_avatarNN.png where NN is the size
		LARGE(80), MEDIUM(50), SMALL_PLUS(33), SMALL(25), MICRO(15);

		private final int size;

		private Size(int size) {
			this.size = size;
		}

		int getSize() {
			return size;
		}
	}

	private static final String[] alternateAvatarUrls;
	static {
		String[] alternates = new String[Size.values().length];
		String urlBase = new UrlBuilder().setProtocol(Window.Location.getProtocol()).setHost(Window.Location.getHost())
				.setPath(Path.getBasePath()).buildString();

		for (Size size : Size.values()) {
			alternates[size.ordinal()] = urlBase + "/images/default_avatar" + size.getSize() + ".png";
		}
		alternateAvatarUrls = alternates;
	}

	public static String computeAvatarUrl(String gravatarHash, Size size) {
		if (gravatarHash == null) {
			gravatarHash = "00000000000000000000000000000000";
		}
		return "/" + Path.getBasePath() + "api/image/" + gravatarHash + ".jpg?s=" + size.getSize() + "&d="
				+ alternateAvatarUrls[size.ordinal()];
	}

}
