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
package com.tasktop.c2c.server.common.profile.web.client.place;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.notification.Notifier;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 * @param <T>
 */
public abstract class AbstractPlace extends Place implements IPlace {

	protected Message displayOnArrival;
	protected Notifier notifier = ProfileGinjector.get.instance().getNotifier();
	protected CommonProfileMessages messages = ProfileGinjector.get.instance().getCommonProfileMessages();

	public AbstractPlace displayOnArrival(Message displayOnArrival) {
		this.displayOnArrival = displayOnArrival;
		return this;
	}

	@Override
	public String getToken() {
		return "";
	}

	@Override
	public abstract String getPrefix();

	public String getHref() {

		if (!GWT.isClient()) {
			// For tests
			return "http://localhost/#" + getHistoryToken();
		}
		UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
		urlBuilder.setPath(Path.getBasePath());
		return urlBuilder.setHash(getHistoryToken()).buildString();
	}

	@Override
	public final String getHistoryToken() {
		StringBuilder sb = new StringBuilder();
		sb.append(getPrefix());
		String token = getToken();
		if (StringUtils.hasText(token)) {
			sb.append(IPlace.TOKEN_SEPERATOR).append(token);
		}
		return sb.toString();
	}

	public AbstractPlace() {
		super();
	}

	protected void reset() {

	}

	@Override
	public String toString() {
		return getHistoryToken();
	}

	protected void onNotAuthorised() {
		if (AuthenticationHelper.isAnonymous()) {
			// This place needs to be reset if it is going to be used again
			reset();
			SignInPlace.createPlace(this).go();
		} else {
			Message message = Message.createErrorMessage(getNotAuthorizedMessage());
			if (AuthenticationHelper.isAccountDisabled()) {
				ProfileGinjector.get.instance().getNotificationPanel().displayMessage(message);
			} else {
				IPlace place = ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace();
				place.displayOnArrival(message);
				place.go();
			}
		}
	}

	private String getNotAuthorizedMessage() {
		if (ProfileGinjector.get.instance().getAppState().getCredentials().getProfile().getAccountDisabled()) {
			return messages.accountDisabled();
		}
		return messages.noPermissionsToVisitPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getHref().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractPlace other = (AbstractPlace) obj;
		return getHref().equals(other.getHref());
	}

}
