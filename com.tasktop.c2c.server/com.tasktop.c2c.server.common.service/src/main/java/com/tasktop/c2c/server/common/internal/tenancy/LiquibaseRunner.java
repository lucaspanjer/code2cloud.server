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
package com.tasktop.c2c.server.common.internal.tenancy;

import java.sql.Connection;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.exception.ValidationFailedException;
import liquibase.integration.spring.SpringLiquibase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extend SpringLiquibase to clear checksums in case of a ValidationFailedExceptions (indicating a changeset definition
 * that has already been run, has changed). We try to avoid inducing this behavior in the first place, but it can
 * happen. In which case, this will allow subsequent liquibase changes to still run.
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class LiquibaseRunner extends SpringLiquibase {

	private static final Logger LOG = LoggerFactory.getLogger(LiquibaseRunner.class);

	private boolean clearCheckSums = false;

	private Liquibase liquibase;

	@Override
	protected Liquibase createLiquibase(Connection c) throws LiquibaseException {
		this.liquibase = super.createLiquibase(c);
		if (clearCheckSums) {
			liquibase.clearCheckSums();
		}
		return liquibase;
	}

	public void afterPropertiesSet() throws LiquibaseException {
		try {
			super.afterPropertiesSet();
		} catch (ValidationFailedException e) {
			if (!clearCheckSums) {
				LOG.warn("Failed to run liquibase. Will try again clearing the checksums first", e);
				// Try again, clearing checksums this time
				clearCheckSums = true;
				super.afterPropertiesSet();
			}
		}
	}
}
