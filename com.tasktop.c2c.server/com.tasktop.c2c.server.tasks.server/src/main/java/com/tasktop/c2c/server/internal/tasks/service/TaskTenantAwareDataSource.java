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

import org.springframework.tenancy.datasource.TenantAwareDataSource;

import com.tasktop.c2c.server.common.service.web.TenancyUtil;

public class TaskTenantAwareDataSource extends TenantAwareDataSource {

	private Language language;

	@Override
	protected String getDatabaseName() {
		String projectId = TenancyUtil.getCurrentTenantProjectIdentifer();
		String dbName = projectId + "_tasks";
		if (language == Language.HSQL || language == Language.ORACLE) {
			dbName = dbName.toUpperCase();
		}
		return dbName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.tenancy.datasource.AbstractDatabaseSwitchingDataSource#setLanguage(org.springframework.tenancy
	 * .datasource.AbstractDatabaseSwitchingDataSource.Language)
	 */
	@Override
	public void setLanguage(Language l) {
		super.setLanguage(l);
		this.language = l;
	}
}
