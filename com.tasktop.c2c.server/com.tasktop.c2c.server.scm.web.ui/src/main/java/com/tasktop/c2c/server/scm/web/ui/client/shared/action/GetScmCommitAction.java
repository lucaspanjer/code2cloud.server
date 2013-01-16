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
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.gwt.core.client.GWT;
import com.tasktop.c2c.server.common.web.client.util.ExceptionsUtil;
import com.tasktop.c2c.server.common.web.shared.CachableReadAction;
import com.tasktop.c2c.server.common.web.shared.KnowsErrorMessageAction;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmMessages;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetScmCommitAction implements Action<GetScmCommitResult>, CachableReadAction, KnowsErrorMessageAction {
	private String repoName;
	private String commitId;
	private String projectId;

	public GetScmCommitAction(String repoName, String commitId, String projectId) {
		this.repoName = repoName;
		this.commitId = commitId;
		this.projectId = projectId;
	}

	protected GetScmCommitAction() {

	}

	@Override
	public String getErrorMessage(DispatchException e) {
		if (ExceptionsUtil.isEntityNotFound(e)) {
			ScmMessages messages = GWT.create(ScmMessages.class);
			return messages.commitNotFound(commitId);
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commitId == null) ? 0 : commitId.hashCode());
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
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
		GetScmCommitAction other = (GetScmCommitAction) obj;
		if (commitId == null) {
			if (other.commitId != null)
				return false;
		} else if (!commitId.equals(other.commitId))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (repoName == null) {
			if (other.repoName != null)
				return false;
		} else if (!repoName.equals(other.repoName))
			return false;
		return true;
	}

	public String getRepoName() {
		return repoName;
	}

	public String getCommitId() {
		return commitId;
	}

	public String getProjectId() {
		return projectId;
	}

}
