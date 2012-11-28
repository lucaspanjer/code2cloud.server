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
package com.tasktop.c2c.server.internal.tasks.service;

import com.tasktop.c2c.server.common.service.BaseProfileConfiguration;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;

/**
 * Simple container class for the configuration needed by task service.
 */
public class TaskServiceConfiguration extends BaseProfileConfiguration {

	private Integer maxAttachmentSize;
	private Integer maxAttachmentFilenameSize;
	private boolean treatEmptyStringAsNullInDatabase;

	public String getProfileProjectIdentifier() {
		return TenancyUtil.getCurrentTenantProjectIdentifer();
	}

	public String getExternalTaskServiceUrl() {
		return getServiceUrlPrefix(getProfileProjectIdentifier()) + "tasks";
	}

	public String getWebUrlForTask(Integer taskId) {
		return String.format("%s/%s/%s/task/%s", getProfileBaseUrl(), PROJECTS_WEB_PATH, getProfileProjectIdentifier(),
				taskId);
	}

	public String getWebUrlForAttachment(Integer attachmentId) {
		return getExternalTaskServiceUrl() + "/attachment/" + attachmentId;
	}

	public Integer getMaxAttachmentSize() {
		return maxAttachmentSize;
	}

	public void setMaxAttachmentSize(Integer maxAttachmentSize) {
		this.maxAttachmentSize = maxAttachmentSize;
	}

	public Integer getMaxAttachmentFilenameSize() {
		return maxAttachmentFilenameSize;
	}

	public void setMaxAttachmentFilenameSize(Integer maxAttachmentFilenameSize) {
		this.maxAttachmentFilenameSize = maxAttachmentFilenameSize;
	}

	public boolean isTreatEmptyStringAsNullInDatabase() {
		return treatEmptyStringAsNullInDatabase;
	}

	public void setTreatEmptyStringAsNullInDatabase(boolean treatEmptyStringAsNullInDatabase) {
		this.treatEmptyStringAsNullInDatabase = treatEmptyStringAsNullInDatabase;
	}
}
