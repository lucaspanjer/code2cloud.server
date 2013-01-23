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
package com.tasktop.c2c.server.web.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.stereotype.Component;

@Component
public class HttpProxy extends WebProxy {
	private static Pattern targetUrlPattern = Pattern.compile("https?://.+");

	private HttpClient httpClient;

	private HttpMethodProvider httpMethodProvider = new HttpMethodProvider() {

		@Override
		public HttpMethod getMethod(String httpMethod, String uri) {
			if (httpMethod.equals("GET")) {
				return new GetMethod(uri);
			} else if (httpMethod.equals("DELETE")) {
				return new DeleteMethod(uri);
			} else if (httpMethod.equals("HEAD")) {
				return new HeadMethod(uri);
			} else if (httpMethod.equals("OPTIONS")) {
				return new OptionsMethod(uri);
			} else if (httpMethod.equals("POST")) {
				return new PostMethod(uri);
			} else if (httpMethod.equals("PUT")) {
				return new PutMethod(uri);
			} else if (httpMethod.equals("TRACE")) {
				return new TraceMethod(uri);
			}
			throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
		}
	};

	public HttpProxy() {
		httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(60 * 1000);
		httpClient.getParams()
				.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
		headerFilter = new CookieHeaderFilter();
		ExcludeHeaderFilter excludeHeaders = new ExcludeHeaderFilter();
		excludeHeaders.getExcludedRequestHeaders().addAll(Arrays.asList("Connection", "Accept-Encoding"));
		excludeHeaders.getExcludedResponseHeaders().addAll(Arrays.asList("Connection", "Transfer-Encoding"));
		headerFilter.setNext(excludeHeaders);
	}

	@Override
	public boolean canProxyRequest(String targetUrl, HttpServletRequest request) {
		return targetUrlPattern.matcher(targetUrl).matches();
	}

	@Override
	protected void proxy(String targetUrl, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {

		final HttpMethod proxyRequest = createProxyRequest(targetUrl, request);

		if (proxyRequest instanceof EntityEnclosingMethod) {
			EntityEnclosingMethod entityEnclosingMethod = (EntityEnclosingMethod) proxyRequest;
			RequestEntity requestEntity = new InputStreamRequestEntity(request.getInputStream(),
					request.getContentLength(), request.getContentType());
			entityEnclosingMethod.setRequestEntity(requestEntity);
		}
		int code = httpClient.executeMethod(proxyRequest);
		response.setStatus(code);
		copyProxyReponse(proxyRequest, response);

	}

	private String uriEncode(String url) {
		return url.replace(" ", "%20");
	}

	private HttpMethod createProxyRequest(String targetUrl, HttpServletRequest request) throws IOException {
		URI targetUri;
		try {
			targetUri = new URI(uriEncode(targetUrl));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		HttpMethod commonsHttpMethod = httpMethodProvider.getMethod(request.getMethod(), targetUri.toString());

		commonsHttpMethod.setFollowRedirects(false);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> headerVals = request.getHeaders(headerName);
			while (headerVals.hasMoreElements()) {
				String headerValue = headerVals.nextElement();
				headerValue = headerFilter.processRequestHeader(headerName, headerValue);
				if (headerValue != null) {
					commonsHttpMethod.addRequestHeader(new Header(headerName, headerValue));
				}

			}
		}

		return commonsHttpMethod;
	}

	void copyProxyReponse(HttpMethod proxyResponse, HttpServletResponse response) throws IOException {
		copyProxyHeaders(proxyResponse.getResponseHeaders(), response);
		response.setContentLength(getResponseContentLength(proxyResponse));
		copy(proxyResponse.getResponseBodyAsStream(), response.getOutputStream());
		if (proxyResponse.getStatusLine() != null) {
			int statCode = proxyResponse.getStatusCode();
			response.setStatus(statCode);
		}
	}

	public int getResponseContentLength(HttpMethod httpMethod) {
		Header[] headers = httpMethod.getResponseHeaders("Content-Length");
		if (headers.length == 0) {
			return -1;
		}

		for (int i = headers.length - 1; i >= 0; i--) {
			Header header = headers[i];
			try {
				return Integer.parseInt(header.getValue());
			} catch (NumberFormatException e) {

			}
			// See if we can have better luck with another header, if present
		}
		return -1;
	}

	private void copyProxyHeaders(Header[] headers, HttpServletResponse response) {
		for (Header h : headers) {
			String header = h.getName();
			String valR = headerFilter.processResponseHeader(header, h.getValue());
			if (valR != null) {
				response.addHeader(header, valR);
			}
		}
	}

	private static int RESPONSE_BUFFER_SIZE = 10 * 1024;

	private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		if (inputStream == null) {
			return;
		}
		byte[] buffer = new byte[RESPONSE_BUFFER_SIZE];
		int numRead;
		while ((numRead = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, numRead);
		}

	}

	public void setHttpClient(HttpClient client) {
		this.httpClient = client;
	}

	public void setHttpMethodProvider(HttpMethodProvider httpMethodProvider) {
		this.httpMethodProvider = httpMethodProvider;
	}

}
