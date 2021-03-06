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

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.AnonymousPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class RequestPasswordResetPlace extends AnonymousPlace implements HeadingPlace, WindowTitlePlace {

	public static PageMapping RequestPasswordReset = new PageMapping(new RequestPasswordResetPlace.Tokenizer(),
			"requestPasswordReset");

	private static class Tokenizer extends AbstractPlaceTokenizer<RequestPasswordResetPlace> {

		@Override
		public RequestPasswordResetPlace getPlace(String token) {
			return RequestPasswordResetPlace.createPlace();
		}

	}

	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public static RequestPasswordResetPlace createPlace() {
		return new RequestPasswordResetPlace();
	}

	@Override
	public String getHeading() {
		return profileMessages.passwordReset();
	}

	@Override
	public String getPrefix() {
		return RequestPasswordReset.getUrl();
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(profileMessages.passwordReset());
	}

	protected void handleBatchResults() {
		super.onPlaceDataFetched();
	}

}
