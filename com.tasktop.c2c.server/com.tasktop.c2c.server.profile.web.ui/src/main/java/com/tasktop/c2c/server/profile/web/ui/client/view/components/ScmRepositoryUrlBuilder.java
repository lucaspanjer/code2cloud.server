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
package com.tasktop.c2c.server.profile.web.ui.client.view.components;

import java.util.Arrays;

import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;

/**
 * Build a URL string for a source code repository given its existing URL(s), the user name, and potentially other data.
 * 
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class ScmRepositoryUrlBuilder {

	public String buildUrl(ScmRepository repository, String url, String organizationIdentifier) {
		if (AuthenticationHelper.isAnonymous() || !repository.getScmLocation().equals(ScmLocation.CODE2CLOUD)) {
			return url;
		}
		// Encoded protocols
		for (String protocol : Arrays.asList("http://", "https://", "ssh://")) {
			if (url.startsWith(protocol)) {
				String encodedUserName = com.google.gwt.http.client.URL.encodePathSegment(ProfileGinjector.get
						.instance().getAppState().getCredentials().getProfile().getUsername());

				return protocol + encodedUserName + "@" + url.substring(protocol.length());
			}
		}

		return url;
	}
}
