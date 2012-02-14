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

import javax.persistence.EntityManager;

import com.tasktop.c2c.server.profile.domain.internal.Profile;
import com.tasktop.c2c.server.profile.domain.internal.Project;
import com.tasktop.c2c.server.profile.domain.internal.ProjectProfile;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class MockProjectProfileFactory {

	public static ProjectProfile create(Project project, Profile profile, EntityManager entityManager) {
		ProjectProfile result = new ProjectProfile();
		result.setOwner(false);
		result.setUser(true);
		result.setProfile(profile);
		result.setProject(project);

		if (entityManager != null) {
			entityManager.persist(result);
			project.getProjectProfiles().add(result);
			profile.getProjectProfiles().add(result);
		}

		return result;
	}
}
