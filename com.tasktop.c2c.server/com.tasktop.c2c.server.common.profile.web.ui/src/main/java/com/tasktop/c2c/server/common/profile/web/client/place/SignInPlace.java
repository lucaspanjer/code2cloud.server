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

import com.google.gwt.place.shared.Place;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class SignInPlace extends AnonymousPlace implements WindowTitlePlace, HeadingPlace {

	private static final String AFTER_TOKEN = "?after=";

	public static PageMapping SignIn = new PageMapping(new SignInPlace.Tokenizer(), "signin", "signin" + AFTER_TOKEN
			+ "{after}");

	private static class Tokenizer extends AbstractPlaceTokenizer<SignInPlace> {

		@Override
		public SignInPlace getPlace(String token) {

			if (token.contains(AFTER_TOKEN)) {
				String afterUrl = token.substring(token.lastIndexOf(AFTER_TOKEN) + AFTER_TOKEN.length());
				if (afterUrl != null) {
					Place afterPlace = ProfileGinjector.get.instance().getPageMappingRegistry()
							.getPlaceForUrl(afterUrl);
					if (afterPlace != null) {
						return SignInPlace.createPlace((IPlace) afterPlace);
					}
				}
			}

			return SignInPlace.createPlace();
		}
	}

	private final IPlace afterSuccessfulSignIn;
	private final boolean afterInUrl;

	public IPlace getAfterSuccessfulSignIn() {
		return afterSuccessfulSignIn;
	}

	@Override
	public String getHeading() {
		return super.commonProfileMessages.signIn();
	}

	public String getPrefix() {
		if (afterSuccessfulSignIn != null && afterInUrl) {
			return SignIn.getUrl() + AFTER_TOKEN + afterSuccessfulSignIn.getHistoryToken(); // REVIEW? should be
																							// getHref?
		}
		return SignIn.getUrl();
	}

	private SignInPlace(IPlace afterSuccessfulSignIn) {
		if (afterSuccessfulSignIn == null) {
			this.afterSuccessfulSignIn = ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace();
			afterInUrl = false;
		} else {
			this.afterSuccessfulSignIn = afterSuccessfulSignIn;
			afterInUrl = true;
		}
	}

	public static SignInPlace createPlace() {
		return new SignInPlace(null);
	}

	public static SignInPlace createPlace(IPlace afterSuccessfulSignIn) {
		return new SignInPlace(afterSuccessfulSignIn);
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(super.commonProfileMessages.signIn());
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		onPlaceDataFetched();
	}

}
