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

import java.util.Arrays;
import java.util.List;

/**
 * Simple collection of a group of page mappings.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class PageMappings {
	private List<PageMapping> mappings;

	public PageMappings(PageMapping... mappings) {
		this.mappings = Arrays.asList(mappings);
	}

	/**
	 * @return the mappings
	 */
	public List<PageMapping> getMappings() {
		return mappings;
	}

}
