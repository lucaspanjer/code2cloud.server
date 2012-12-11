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
package com.tasktop.c2c.server.profile.web.ui.client.place;

import com.google.gwt.place.impl.AbstractPlaceHistoryMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.place.IPlace;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * @author jtyrrell
 * 
 */
public class AppHistoryMapper extends AbstractPlaceHistoryMapper<Place> {

	private ProfileMessages messages = AppGinjector.get.instance().getProfileMessages();

	@Override
	protected PrefixAndToken getPrefixAndToken(Place newPlace) {
		if (newPlace instanceof IPlace) {
			return new CustomPrefixAndToken(((IPlace) newPlace).getPrefix(), ((IPlace) newPlace).getToken());
		}
		return null;
	}

	public static class CustomPrefixAndToken extends PrefixAndToken {

		public CustomPrefixAndToken(String prefix, String token) {
			super(prefix, token);
		}

		@Override
		public String toString() {
			if (StringUtils.hasText(token)) {
				return ((prefix.length() == 0) ? token : prefix + IPlace.TOKEN_SEPERATOR + token);
			} else {
				return prefix;
			}
		}
	}

	@Override
	protected PlaceTokenizer<?> getTokenizer(String prefix) {
		return null;
	}

	public Place getPlace(String token) {
		Place retPlace = ProfileGinjector.get.instance().getPageMappingRegistry().getPlaceForUrl(token);

		if (retPlace != null) {
			return retPlace;
		}

		AppGinjector.get.instance().getNotifier()
				.displayMessage(Message.createErrorMessage(messages.unexpectedError() + " (HTTP 404)"));
		return AppGinjector.get.instance().getPlaceController().getWhere();
	}
}
