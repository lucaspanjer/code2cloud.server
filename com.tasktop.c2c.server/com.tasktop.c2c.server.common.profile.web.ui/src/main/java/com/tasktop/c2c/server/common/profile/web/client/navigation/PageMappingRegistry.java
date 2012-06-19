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
package com.tasktop.c2c.server.common.profile.web.client.navigation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.place.shared.Place;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.PathMapping;
import com.tasktop.c2c.server.common.web.client.navigation.PathMapping.PathInfo;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class PageMappingRegistry {
	private List<PageMapping> registeredMappings = new ArrayList<PageMapping>();

	public List<PageMapping> getRegisteredMappings() {
		return registeredMappings;
	}

	public void register(PageMapping mapping) {
		registeredMappings.add(mapping);
	}

	public Place getPlaceForUrl(String url) {
		PageMapping pageMappings = getPageMappingsForUrl(url);
		if (pageMappings == null) {
			return null;
		} else {
			return pageMappings.getTokenizer().getPlace(url);
		}
	}

	public Args getPathArgsForUrl(String url) {
		PageMapping pageMappings = getPageMappingsForUrl(url);
		if (pageMappings == null) {
			return null;
		} else {
			PathInfo info = PathMapping.computePathInfo(url);
			return pageMappings.getPath(info).configureArgs(url);
		}
	}

	private PageMapping getPageMappingsForUrl(String url) {
		PathInfo info = PathMapping.computePathInfo(url);
		for (PageMapping curMapping : registeredMappings) {
			if (curMapping.matches(info)) {
				return curMapping;
			}
		}
		return null;
	}

	public void register(PageMappings pageMappings) {
		for (PageMapping mapping : pageMappings.getMappings()) {
			register(mapping);
		}

	}

}
