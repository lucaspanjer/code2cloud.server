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
package com.tasktop.c2c.server.common.service;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class Log4JConfiguration implements InitializingBean {

	private String location;

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		File file = new File(location);
		if (file.exists()) {
			DOMConfigurator.configure(location);
		}
	}
}
