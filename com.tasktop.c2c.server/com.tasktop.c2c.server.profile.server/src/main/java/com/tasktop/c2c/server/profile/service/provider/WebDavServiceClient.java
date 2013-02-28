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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;

import com.tasktop.c2c.server.auth.service.proxy.ProxyPreAuthHttpRequestFactory;
import com.tasktop.c2c.server.common.service.HttpStatusCodeException;
import com.tasktop.c2c.server.common.service.http.MultiUserClientHttpRequestFactory;
import com.tasktop.c2c.server.common.service.web.AbstractRestServiceClient;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class WebDavServiceClient extends AbstractRestServiceClient implements WebDavService {

	private String pathPrefixToIgnore = "/services/maven";

	protected HttpClient getClient() {
		if (super.template.getRequestFactory() instanceof MultiUserClientHttpRequestFactory) {
			return ((MultiUserClientHttpRequestFactory) super.template.getRequestFactory()).getClient();
		}

		if (super.template.getRequestFactory() instanceof ProxyPreAuthHttpRequestFactory) {
			return ((ProxyPreAuthHttpRequestFactory) super.template.getRequestFactory()).getHttpClient();
		}
		throw new IllegalStateException("Can't get httpclient");
	}

	protected void postProcessMethod(HttpMethodBase httpMethod) {
		if (super.template.getRequestFactory() instanceof ProxyPreAuthHttpRequestFactory) {
			((ProxyPreAuthHttpRequestFactory) super.template.getRequestFactory())
					.postProcessCommonsHttpMethod(httpMethod);
		}
	}

	/**
	 * List all files, ignoring directories.
	 * 
	 * @return
	 * @throws IOException
	 * @throws DavException
	 */
	@Override
	public List<String> listAllFiles() throws IOException, DavException {
		PropFindMethod propFind = new PropFindMethod(getBaseUrl() + "/", DavConstants.PROPFIND_ALL_PROP,
				DavConstants.DEPTH_INFINITY);
		postProcessMethod(propFind);

		int status = getClient().executeMethod(propFind);

		if (status != 207) {
			throw new HttpStatusCodeException(status);
		}

		MultiStatus multiStatus = propFind.getResponseBodyAsMultiStatus();

		List<String> result = new ArrayList<String>();
		for (MultiStatusResponse response : multiStatus.getResponses()) {
			if (response.getHref().endsWith("/")) {
				// Directory, continue
				continue;
			}
			String path = response.getHref();
			if (path.startsWith(pathPrefixToIgnore)) {
				path = path.substring(pathPrefixToIgnore.length());
			}

			result.add(path);

		}
		return result;
	}

	@Override
	public InputStream getFileContent(String path) throws IOException {

		GetMethod get = new GetMethod(getBaseUrl() + "/" + path);
		postProcessMethod(get);

		int status = getClient().executeMethod(get);

		if (status != 200) {
			throw new HttpStatusCodeException(status);
		}

		return get.getResponseBodyAsStream();
	}

	@Override
	public void writeFile(String path, InputStream content) throws HttpException, IOException {

		// Mkdirs
		for (int i = path.indexOf("/"); i != -1; i = path.indexOf("/", i + 1)) {
			String directory = path.substring(0, i);

			MkColMethod mk = new MkColMethod(getBaseUrl() + "/" + directory);
			postProcessMethod(mk);

			int status = getClient().executeMethod(mk);

			if (status != 201 && status != 405) {
				throw new HttpStatusCodeException(status);
			}

		}

		PutMethod put = new PutMethod(getBaseUrl() + "/" + path);
		postProcessMethod(put);

		put.setRequestEntity(new InputStreamRequestEntity(content));

		int status = getClient().executeMethod(put);

		if (status != 201) {
			throw new HttpStatusCodeException(status);
		}
	}
}
