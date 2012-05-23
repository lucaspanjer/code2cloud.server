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
package com.tasktop.c2c.server.common.profile.web.client.presenter.person;

import java.util.ArrayList;
import java.util.List;

import com.tasktop.c2c.server.common.web.client.widgets.chooser.person.Person;
import com.tasktop.c2c.server.profile.domain.project.Profile;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class PersonUtil {

	public static Person toPerson(Profile userProfile) {
		if (userProfile == null) {
			return null;
		}
		return new Person(userProfile.getUsername(), userProfile.getFirstName() + " " + userProfile.getLastName(),
				userProfile.getId() == null ? null : userProfile.getId().intValue());
	}

	public static List<Person> toPeople(List<Profile> userProfiles) {
		if (userProfiles == null) {
			return new ArrayList<Person>();
		}
		List<Person> people = new ArrayList<Person>(userProfiles.size());
		for (Profile userProfile : userProfiles) {
			people.add(toPerson(userProfile));
		}
		return people;
	}

	public static Profile toProfile(Person person) {
		if (person == null) {
			return null;
		}
		Profile profile = new Profile();
		profile.setUsername(person.getIdentity());
		profile.setId(person.getId() == null ? null : person.getId().longValue());
		return profile;
	}

}
