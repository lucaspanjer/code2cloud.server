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
package com.tasktop.c2c.server.wiki.web.ui.shared.action;

import net.customware.gwt.dispatch.shared.Action;

import com.tasktop.c2c.server.common.web.shared.CachableReadAction;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ListAttachmentsAction implements Action<ListAttachmentsResult>, CachableReadAction {

	private String projectId;
	private Integer pageId;
	private String pagePath;

	public ListAttachmentsAction(String projectId, Integer pageId) {
		this.projectId = projectId;
		this.pageId = pageId;
	}

	public ListAttachmentsAction(String projectId, String pagePath) {
		this.projectId = projectId;
		this.pagePath = pagePath;
	}

	protected ListAttachmentsAction() {

	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @return the pageId
	 */
	public Integer getPageId() {
		return pageId;
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
		result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
		result = prime * result + ((pagePath == null) ? 0 : pagePath.hashCode());
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
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
		ListAttachmentsAction other = (ListAttachmentsAction) obj;
		if (pageId == null) {
			if (other.pageId != null)
				return false;
		} else if (!pageId.equals(other.pageId))
			return false;
		if (pagePath == null) {
			if (other.pagePath != null)
				return false;
		} else if (!pagePath.equals(other.pagePath))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}

	/**
	 * @return the pagePath
	 */
	public String getPagePath() {
		return pagePath;
	}

}
