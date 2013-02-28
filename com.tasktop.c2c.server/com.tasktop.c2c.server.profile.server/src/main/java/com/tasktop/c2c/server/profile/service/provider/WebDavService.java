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
package com.tasktop.c2c.server.profile.service.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.webdav.DavException;

/**
 * @author clint (Tasktop Technologies Inc.)
 *
 */
public interface WebDavService {

	/**
	 * List all files, ignoring directories.
	 * 
	 * @return
	 * @throws IOException
	 * @throws DavException
	 */
	List<String> listAllFiles() throws IOException, DavException;

	InputStream getFileContent(String path) throws IOException;

	void writeFile(String path, InputStream content) throws HttpException, IOException;

}