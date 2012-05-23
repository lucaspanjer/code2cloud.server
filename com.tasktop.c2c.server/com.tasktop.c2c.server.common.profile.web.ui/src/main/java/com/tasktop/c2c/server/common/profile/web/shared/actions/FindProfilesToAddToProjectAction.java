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
package com.tasktop.c2c.server.common.profile.web.shared.actions;

import net.customware.gwt.dispatch.shared.Action;

import com.tasktop.c2c.server.common.web.shared.CachableReadAction;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class FindProfilesToAddToProjectAction implements Action<FindProfilesResult>, CachableReadAction {
	private String projectId;
	private String queryString;
	private int limit;

	public FindProfilesToAddToProjectAction(String projectId, String queryString, int limit) {
		this.projectId = projectId;
		this.queryString = queryString;
		this.limit = limit;
	}

	protected FindProfilesToAddToProjectAction() {
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	public String getQueryString() {
		return queryString;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + limit;
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((queryString == null) ? 0 : queryString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FindProfilesToAddToProjectAction other = (FindProfilesToAddToProjectAction) obj;
		if (limit != other.limit)
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (queryString == null) {
			if (other.queryString != null)
				return false;
		} else if (!queryString.equals(other.queryString))
			return false;
		return true;
	}

}
