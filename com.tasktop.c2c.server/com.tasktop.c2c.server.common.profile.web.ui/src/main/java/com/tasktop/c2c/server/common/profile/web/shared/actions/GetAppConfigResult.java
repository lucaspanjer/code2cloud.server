/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
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

import java.util.Map;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class GetAppConfigResult implements Result {
	private Map<String, String> properties;

	public GetAppConfigResult(Map<String, String> properties) {
		this.properties = properties;
	}

	protected GetAppConfigResult() {
		// For GWT serialization
	}

	public Map<String, String> getProperties() {
		return properties;
	}

}
