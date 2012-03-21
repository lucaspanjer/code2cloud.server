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
package com.tasktop.c2c.server.wiki.server.tests;

import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-hsql.xml" })
@Transactional
public class HsqlWikiServiceTest extends BaseWikiServiceTest {

	@Qualifier("rawDataSource")
	@Autowired
	private DataSource dataSource;

	@Test
	public void testHsqlQueries() throws SQLException {
		dataSource.getConnection().createStatement().executeQuery("SELECT * FROM Page");
		dataSource.getConnection().createStatement().executeQuery("SELECT * FROM PAGE");

		try {
			dataSource.getConnection().createStatement().executeQuery("SELECT * FROM \"Page\"");
			Assert.fail("expected ex");
		} catch (SQLException e) {
			// expected
		}

		dataSource.getConnection().createStatement().executeQuery("SELECT * FROM \"PAGE\"");

		dataSource.getConnection().createStatement()
				.executeQuery("SELECT ID, CREATIONDATE, IDENTITY, MODIFICATIONDATE, NAME, VERSION FROM PERSON");
	}
}
