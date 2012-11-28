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
package com.tasktop.c2c.server.internal.wiki.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriUtils;

import com.tasktop.c2c.server.common.service.BaseProfileConfiguration;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;

public class WikiServiceConfiguration extends BaseProfileConfiguration {

	private Integer maxAttachmentSize;

	public String getProfileProjectIdentifier() {
		return TenancyUtil.getCurrentTenantProjectIdentifer();
	}

	public String computeWebUrlForPage(String pagePath) {

		return getProfileBaseUrl() + "/" + computePathForPage(pagePath);
	}

	public String computePathForPage(String pagePath) {
		try {
			return PROJECTS_WEB_PATH + "/" + getProfileProjectIdentifier() + "/wiki/p/"
					+ URLEncoder.encode(pagePath, "utf-8").replace("%20", "+").replace("%2F", "/");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}

	public String computeWebUrlForAttachment(Long id, String name) {
		try {
			String pathString = String.format("%swiki/%s/attachment/%s",
					getServiceUrlPrefix(getProfileProjectIdentifier()), id, name);
			return UriUtils.encodeHttpUrl(pathString, "utf-8");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public String computeAttachmentsUrlForPage(Long id) {
		return getServiceUrlPrefix(getProfileProjectIdentifier()) + "wiki/" + id + "/attachment";
	}

	public String retrievePropertyConfiguration(String propertyName) {
		return getServiceUrlPrefix(getProfileProjectIdentifier()) + "wiki/configuration/" + propertyName;
	}

	public Integer getMaxAttachmentSize() {
		return maxAttachmentSize;
	}

	public void setMaxAttachmentSize(Integer maxAttachmentSize) {
		this.maxAttachmentSize = maxAttachmentSize;
	}
}
