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
package com.tasktop.c2c.server.profile.web.ui.server;

import java.util.ArrayList;
import java.util.List;

import com.tasktop.c2c.server.common.profile.web.shared.Profile;

public class WebDomain {

	public static com.tasktop.c2c.server.profile.domain.internal.Profile copy(Profile p) {
		com.tasktop.c2c.server.profile.domain.internal.Profile profile = new com.tasktop.c2c.server.profile.domain.internal.Profile();
		profile.setEmail(p.getEmail());
		profile.setFirstName(p.getFirstName());
		profile.setLastName(p.getLastName());
		profile.setUsername(p.getUsername());
		profile.setPassword(p.getPassword());
		profile.setId(p.getId());
		return profile;
	}

	public static Profile copy(com.tasktop.c2c.server.profile.domain.internal.Profile profile) {
		Profile p = new Profile();
		p.setId(profile.getId());
		p.setUsername(profile.getUsername());
		p.setFirstName(profile.getFirstName());
		p.setLastName(profile.getLastName());
		p.setEmail(profile.getEmail());
		p.setGravatarHash(profile.getGravatarHash());
		// NOTE: we never copy password here
		return p;
	}

	public static List<Profile> copy(List<com.tasktop.c2c.server.profile.domain.internal.Profile> profiles) {
		List<Profile> copies = new ArrayList<Profile>();
		for (com.tasktop.c2c.server.profile.domain.internal.Profile profile : profiles) {
			copies.add(copy(profile));
		}
		return copies;
	}
}
