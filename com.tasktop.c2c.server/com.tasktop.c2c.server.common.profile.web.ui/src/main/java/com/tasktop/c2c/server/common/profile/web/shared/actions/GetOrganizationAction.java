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
import net.customware.gwt.dispatch.shared.DispatchException;

import com.tasktop.c2c.server.common.web.client.util.ExceptionsUtil;
import com.tasktop.c2c.server.common.web.shared.CachableReadAction;
import com.tasktop.c2c.server.common.web.shared.KnowsErrorMessageAction;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetOrganizationAction implements Action<GetOrganizationResult>, CachableReadAction,
		KnowsErrorMessageAction {
	private String organizationId;

	public GetOrganizationAction(String organizationId) {
		this.organizationId = organizationId;
	}

	protected GetOrganizationAction() {
	}

	/**
	 * @return the projectId
	 */
	public String getOrganizationId() {
		return organizationId;
	}

	/**
	 * @param projectId
	 *            the projectId to set
	 */
	public void setOrganizationId(String projectId) {
		this.organizationId = projectId;
	}

	@Override
	public String getErrorMessage(DispatchException e) {
		if (ExceptionsUtil.isEntityNotFound(e)) {
			return "Organization \"" + organizationId + "\" not found.";
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((organizationId == null) ? 0 : organizationId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GetOrganizationAction other = (GetOrganizationAction) obj;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
			return false;
		return true;
	}

}
