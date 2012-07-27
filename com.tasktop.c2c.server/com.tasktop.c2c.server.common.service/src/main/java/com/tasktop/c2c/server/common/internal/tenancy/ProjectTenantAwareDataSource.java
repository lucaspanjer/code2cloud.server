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
package com.tasktop.c2c.server.common.internal.tenancy;

import org.springframework.tenancy.datasource.TenantAwareDataSource;

public class ProjectTenantAwareDataSource extends TenantAwareDataSource {

	private DatabaseNamingStrategy databaseNamingStrategy;

	@Override
	protected String getDatabaseName() {

		String dbName = databaseNamingStrategy.getCurrentTenantDatabaseName();
		if (language == Language.HSQL || language == Language.ORACLE) {
			dbName = dbName.toUpperCase();
		}
		return dbName;
	}

	public void setDatabaseNamingStrategy(DatabaseNamingStrategy databaseNamingStrategy) {
		this.databaseNamingStrategy = databaseNamingStrategy;
	}

}
