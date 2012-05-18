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
package com.tasktop.c2c.server.scm.web.ui.client.shared.action;

import net.customware.gwt.dispatch.shared.Action;

import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.web.shared.CachableReadAction;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetScmLogAction implements Action<GetScmLogResult>, CachableReadAction {
	private String repoName;
	private String projectId;
	private String branchName;
	private Region region;

	public GetScmLogAction(String repoName, String branchName, String projectId, Region region) {
		this.repoName = repoName;
		this.branchName = branchName;
		this.projectId = projectId;
		this.region = region;
	}

	protected GetScmLogAction() {

	}

	public String getRepoName() {
		return repoName;
	}

	public String getProjectId() {
		return projectId;
	}

	public Region getRegion() {
		return region;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((repoName == null) ? 0 : repoName.hashCode());
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
		GetScmLogAction other = (GetScmLogAction) obj;
		if (branchName == null) {
			if (other.branchName != null)
				return false;
		} else if (!branchName.equals(other.branchName))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (repoName == null) {
			if (other.repoName != null)
				return false;
		} else if (!repoName.equals(other.repoName))
			return false;
		return true;
	}

	public String getBranchName() {
		return branchName;
	}

}
