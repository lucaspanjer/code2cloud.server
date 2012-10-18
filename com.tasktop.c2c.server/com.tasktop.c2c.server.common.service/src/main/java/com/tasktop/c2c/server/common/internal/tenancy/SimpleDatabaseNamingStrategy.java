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
package com.tasktop.c2c.server.common.internal.tenancy;

import com.tasktop.c2c.server.common.service.web.TenancyUtil;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class SimpleDatabaseNamingStrategy implements DatabaseNamingStrategy {
	private String suffix;
	private String prefix = null;
	private boolean useShortProjectIdentifier = false;

	public String getCurrentTenantDatabaseName() {
		String result;
		if (useShortProjectIdentifier) {
			result = TenancyUtil.getCurrentTenantShortProjectIdentifer();
		} else {
			result = TenancyUtil.getCurrentTenantProjectIdentifer();
		}

		if (result == null) {
			throw new IllegalStateException("Could not get database name from tenancy context");
		}

		result = result + suffix;

		if (prefix != null) {
			result = prefix + result;
		}
		return result;

	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setUseShortProjectIdentifier(boolean useShortProjectIdentifier) {
		this.useShortProjectIdentifier = useShortProjectIdentifier;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
