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
package com.tasktop.c2c.server.common.web.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Controller
public class AvatarImageController {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AvatarImageController.class);

	private String gravatarBaseJsonUrl = "http://www.gravatar.com/";
	private String gravatarBaseImageUrl = "http://www.gravatar.com/avatar/";
	private int avatarRequestTimeout = 1000;

	private HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());

	public AvatarImageController() {
		client.getHttpConnectionManager().getParams().setConnectionTimeout(avatarRequestTimeout);
		client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
	}

	@RequestMapping(value = "{hash}.jpg", method = RequestMethod.GET)
	public void handleRequest(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("hash") String hash, @RequestParam(value = "d", required = false) String defaultUrl,
			@RequestParam(value = "s", required = false) String size) throws HttpException, IOException {
		String jsonUrl = gravatarBaseJsonUrl + hash + ".json";

		GetMethod get = new GetMethod(jsonUrl);

		int rc = -1;

		try {
			rc = client.executeMethod(get);
		} catch (Exception e) {
			LOGGER.info("Error while contacting gravatar API", e);
		} finally {
			get.releaseConnection();
		}

		if (rc == 200) {
			if (size == null) {
				size = "40";
			}
			response.sendRedirect(gravatarBaseImageUrl + hash + ".jpg?s=" + size);
		} else {
			if (defaultUrl != null) {
				response.sendRedirect(defaultUrl);
			} else {
				response.sendError(404);
			}
		}
	}
}
