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
package com.tasktop.c2c.server.wiki.web.ui.shared.action;

import net.customware.gwt.dispatch.shared.Action;

import com.tasktop.c2c.server.common.web.shared.CachableReadAction;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class RetrieveConfigurationPropertyAction implements Action<RetrieveConfigurationPropertyResult>,
		CachableReadAction {

	private String projectId;
	private String configurationPropertyName;

	public RetrieveConfigurationPropertyAction(String projectId, String configurationPropertyName) {
		this.configurationPropertyName = configurationPropertyName;
		this.projectId = projectId;
	}

	protected RetrieveConfigurationPropertyAction() {

	}

	public String getConfigurationPropertyName() {
		return configurationPropertyName;
	}

	public String getProjectId() {
		return projectId;
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
		result = prime * result + ((configurationPropertyName == null) ? 0 : configurationPropertyName.hashCode());
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
		RetrieveConfigurationPropertyAction other = (RetrieveConfigurationPropertyAction) obj;
		if (configurationPropertyName == null) {
			if (other.configurationPropertyName != null)
				return false;
		} else if (!configurationPropertyName.equals(other.configurationPropertyName))
			return false;
		return true;
	}

}
