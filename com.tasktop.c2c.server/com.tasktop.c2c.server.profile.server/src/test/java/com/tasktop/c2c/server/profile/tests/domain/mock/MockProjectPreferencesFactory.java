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
package com.tasktop.c2c.server.profile.tests.domain.mock;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.tasktop.c2c.server.profile.domain.internal.ProjectPreferences;
import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
public class MockProjectPreferencesFactory {

	private static int created = 0;

	public static ProjectPreferences create(EntityManager entityManager) {
		return create(entityManager, 1).get(0);
	}

	public static List<ProjectPreferences> create(EntityManager entityManager, int count) {
		List<ProjectPreferences> mocks = new ArrayList<ProjectPreferences>(count);
		for (int x = 0; x < count; ++x) {
			ProjectPreferences mock = populate(new ProjectPreferences());
			if (entityManager != null) {
				entityManager.persist(mock);
			}
			mocks.add(mock);
			if (entityManager != null) {
				entityManager.flush();
			}
		}
		return mocks;
	}

	private synchronized static ProjectPreferences populate(ProjectPreferences mock) {
		++created;
		mock.setWikiLanguage(WikiMarkupLanguage.TEXTILE);
		return mock;
	}

}
