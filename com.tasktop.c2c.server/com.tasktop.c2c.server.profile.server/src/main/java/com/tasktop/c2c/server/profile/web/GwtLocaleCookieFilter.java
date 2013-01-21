/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.profile.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tasktop.c2c.server.auth.service.AuthenticationServiceUser;
import com.tasktop.c2c.server.common.service.web.LastChanceBeforeCommitFilter;

/**
 * This reads the preferred language of the authenticated user and sets the value in a "gwtLocale" cookie, read by
 * Google Widget Toolkit for internationalization.
 * 
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class GwtLocaleCookieFilter extends LastChanceBeforeCommitFilter {

	private static final String COOKIE_NAME = "gwtLocale";

	protected void lastChanceBeforeCommit(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		if (AuthenticationServiceUser.getCurrent() != null) {
			String language = AuthenticationServiceUser.getCurrentUserLanguage();
			String cookieLanguage = null;
			if (httpRequest.getCookies() != null) {
				for (Cookie cookie : httpRequest.getCookies()) {
					if (COOKIE_NAME.equals(cookie.getName())) {
						cookieLanguage = cookie.getValue();
					}
				}
			}
			if (cookieLanguage == null || !language.equals(cookieLanguage)) {
				Cookie gwtLocaleCookie = new Cookie(COOKIE_NAME, AuthenticationServiceUser.getCurrentUserLanguage());
				gwtLocaleCookie.setMaxAge(1209600);
				gwtLocaleCookie.setPath("/");
				httpResponse.addCookie(gwtLocaleCookie);
			}
		}

	}

}
