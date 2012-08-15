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
package com.tasktop.c2c.server.web.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter out hub cookies from the requests to internal services. This will prefix internal cookie names if needed to
 * avoid a name clash.
 * 
 * @author david.green (Tasktop Technologies Inc.)
 * @author clint.morgan (Tasktop Technologies Inc.)
 * 
 */
public class CookieHeaderFilter extends HeaderFilter {

	private static final Pattern COOKIE_VALUE_PATTERN = Pattern.compile("([^=]+)(=.+)");

	/**
	 * Prefix added to cookies provided by internal services which clash (have the same name) as those cookeis used by
	 * the hub.
	 */
	public static final String INTERNAL_COOKIE_NAME_PREFIX = "almp.";

	public static final List<String> HUB_COOKIE_NAMES = Arrays.asList("JSESSIONID",
			"SPRING_SECURITY_REMEMBER_ME_COOKIE");

	public String processRequestHeader(String headerName, String headerValue) {
		final boolean isCookie = headerName.equalsIgnoreCase("cookie");
		if (isCookie) {
			return filterCookie(headerName, headerValue);
		} else {
			return super.processRequestHeader(headerName, headerValue);
		}
	}

	public String processResponseHeader(String headerName, String headerValue) {
		if (headerName.equalsIgnoreCase("Set-Cookie") || headerName.equalsIgnoreCase("Set-Cookie2")) {
			return filterSetCookie(headerName, headerValue);
		} else {
			return super.processResponseHeader(headerName, headerValue);
		}
	}

	protected String filterSetCookie(String headerName, String headerValue) {
		// Mapping of cookie names: prefix proxy cookie names to avoid conflicts
		Matcher matcher = COOKIE_VALUE_PATTERN.matcher(headerValue);
		if (matcher.matches()) {
			String name = matcher.group(1);
			String suffix = matcher.group(2);
			if (shouldPrefix(name)) {
				return INTERNAL_COOKIE_NAME_PREFIX + name + suffix;
			} else {
				return name + suffix;
			}
		} else {
			// drop the set-cookie if the value is malformed
			return null;
		}
	}

	protected boolean shouldPrefix(String cookieName) {
		return HUB_COOKIE_NAMES.contains(cookieName.toUpperCase());
	}

	private String filterCookie(String headerName, String headerValue) {
		Matcher matcher = Pattern.compile("(\\$?[^\\s=\"]+)=((\"?).*?\\3)(?:;|,|(?:\\s*$))").matcher(headerValue);

		int cookieCount = 0;
		List<String> cookieTokens = null;
		boolean capturingTokens = true;
		while (matcher.find()) {
			String name = matcher.group(1);
			String value = matcher.group(2);
			boolean capture = false;
			if (name.startsWith("$")) {
				if (capturingTokens) {
					capture = true;
				}
			} else if (name.toLowerCase().startsWith(INTERNAL_COOKIE_NAME_PREFIX) || !shouldPrefix(name)) {
				capturingTokens = true;
				capture = true;
				++cookieCount;
				if (name.toLowerCase().startsWith(INTERNAL_COOKIE_NAME_PREFIX)) {
					name = name.substring(INTERNAL_COOKIE_NAME_PREFIX.length());
				}
			} else {
				capturingTokens = false;
			}

			if (capture) {
				if (cookieTokens == null) {
					cookieTokens = new ArrayList<String>(6);
				}
				cookieTokens.add(name + '=' + value);
			}
		}
		if (cookieCount > 0) {
			String newHeaderValue = "";
			for (String token : cookieTokens) {
				if (newHeaderValue.length() > 0) {
					newHeaderValue += ' ';
				}
				newHeaderValue += token;
				newHeaderValue += ';';
			}
			return newHeaderValue;
		}
		return null;
	}
}
