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
package com.tasktop.c2c.server.common.service.tests.http;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.tasktop.c2c.server.web.proxy.HttpMethodProvider;
import com.tasktop.c2c.server.web.proxy.HttpProxy;

public class HttpProxyTest {
	private Mockery context = new Mockery();
	{
		context.setImposteriser(ClassImposteriser.INSTANCE);
	}
	private HttpMethodProvider httpMethodProvider = context.mock(HttpMethodProvider.class);
	private HttpClient httpClient = context.mock(HttpClient.class);
	private List<Header> proxyRequestHeaders = new ArrayList<Header>();
	private List<Header> proxyResponseHeaders = new ArrayList<Header>();

	private ByteArrayInputStream proxyResponseInputStream = new ByteArrayInputStream(new byte[] {});
	// private ByteArrayOutputStream proxyRequestOutputStream = new ByteArrayOutputStream();

	private HttpProxy proxy = new HttpProxy();

	private void setupMock(final HttpStatus status) throws IOException {

		proxy.setHttpClient(httpClient);
		proxy.setHttpMethodProvider(httpMethodProvider);
		final HttpMethod httpMethod = context.mock(HttpMethod.class);

		context.checking(new Expectations() {
			{
				oneOf(httpMethodProvider).getMethod(with(any(String.class)), with(any(String.class)));
				will(returnValue(httpMethod));

				oneOf(httpClient).executeMethod(with(any(HttpMethodBase.class)));
				will(returnValue(status.value()));

				allowing(httpMethod).addRequestHeader(with(any(Header.class)));
				will(new Action() {

					@Override
					public Object invoke(Invocation invocation) throws Throwable {
						Header header = (Header) invocation.getParameter(0);
						proxyRequestHeaders.add(header);
						return null;
					}

					@Override
					public void describeTo(Description arg0) {
					}

				});

				allowing(httpMethod).getRequestHeaders();
				will(new Action() {

					@Override
					public Object invoke(Invocation invocation) throws Throwable {
						return proxyRequestHeaders.toArray(new Header[] {});
					}

					@Override
					public void describeTo(Description arg0) {
					}

				});

				allowing(httpMethod).getResponseHeaders();
				will(new Action() {

					@Override
					public Object invoke(Invocation invocation) throws Throwable {
						return proxyResponseHeaders.toArray(new Header[] {});
					}

					@Override
					public void describeTo(Description arg0) {
					}

				});

				allowing(httpMethod).getResponseHeaders(with(any(String.class)));
				will(new Action() {

					@Override
					public Object invoke(Invocation invocation) throws Throwable {
						String name = (String) invocation.getParameter(0);
						List<Header> result = new ArrayList<Header>();
						for (Header h : proxyResponseHeaders) {
							if (h.getName().equals(name)) {
								result.add(h);
							}
						}
						return result.toArray(new Header[] {});
					}

					@Override
					public void describeTo(Description arg0) {
					}

				});

				allowing(httpMethod).getResponseBodyAsStream();
				will(returnValue(proxyResponseInputStream));

				allowing(httpMethod);

			}
		});

	}

	private List<String> getProxyRequestHeaderValues(String name) {
		for (Header h : proxyRequestHeaders) {
			if (h.getName().equals(name)) {
				List<String> result = new ArrayList<String>();
				// for (HeaderElement he : h.getElements()) {
				// result.add(he.getValue());
				// }
				result.add(h.getValue());
				return result;
			}
		}
		return null;
	}

	@Test
	public void testCanProxy() {
		assertTrue(proxy.canProxyRequest("http://foo.bar/baz", new MockHttpServletRequest()));
		assertTrue(proxy.canProxyRequest("https://foo.bar/baz", new MockHttpServletRequest()));
	}

	@Test
	public void testBasicGet() throws IOException {
		setupMock(HttpStatus.OK);
		MockHttpServletRequest clientRequest = new MockHttpServletRequest("GET", "unused");
		String randomRequestHeader = "RandomRequestHeader";
		String randomResponseHeader = "RandomResponseHeader";
		String connectionHeader = "Connection";

		clientRequest.addHeader(randomRequestHeader, "RandomHeaderValue");
		clientRequest.addHeader(connectionHeader, "ConnectionValue");
		proxyResponseHeaders.add(new Header(randomResponseHeader, "RandomHeaderValue"));
		proxyResponseHeaders.add(new Header(connectionHeader, "ConnectionValue")); // Should not pass along

		MockHttpServletResponse clientResponse = new MockHttpServletResponse();

		proxy.proxyRequest("foo", clientRequest, clientResponse);

		Assert.assertNotNull(getProxyRequestHeaderValues(randomRequestHeader));
		Assert.assertNull(getProxyRequestHeaderValues(connectionHeader));
		Assert.assertTrue(clientResponse.containsHeader(randomResponseHeader));
		Assert.assertFalse(clientResponse.containsHeader(connectionHeader)); // FIXME, unsure if this req is correct

		context.assertIsSatisfied();
	}

	@Test
	public void testFailedGet() throws IOException {
		setupMock(HttpStatus.SERVICE_UNAVAILABLE);
		MockHttpServletRequest clientRequest = new MockHttpServletRequest("GET", "unused");
		MockHttpServletResponse clientResponse = new MockHttpServletResponse();

		proxy.proxyRequest("foo", clientRequest, clientResponse);

		Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), clientResponse.getStatus());

		context.assertIsSatisfied();
	}

	@Test
	public void testPost() throws IOException {
		byte[] proxyResponseContent = "ProxyResponse".getBytes();
		proxyResponseInputStream = new ByteArrayInputStream(proxyResponseContent);

		setupMock(HttpStatus.OK);

		MockHttpServletRequest request = new MockHttpServletRequest("POST", "unused");
		MockHttpServletResponse response = new MockHttpServletResponse();

		byte[] requestContent = "RequestContent".getBytes();
		request.setContent(requestContent);
		proxy.proxyRequest("foo", request, response);

		// Assert.assertArrayEquals(requestContent, proxyRequestOutputStream.toByteArray());
		Assert.assertArrayEquals(proxyResponseContent, response.getContentAsByteArray());

		context.assertIsSatisfied();
	}

	@Test
	public void testSetCookieNameMapping() throws IOException {
		doTestSetCookieNameTranslated("Set-Cookie");
	}

	@Test
	public void testSetCookie2NameMapping() throws IOException {
		// per http://tools.ietf.org/html/rfc2965
		doTestSetCookieNameTranslated("Set-Cookie2");
	}

	protected void doTestSetCookieNameTranslated(String setCookieHeaderName) throws IOException {
		setupMock(HttpStatus.OK);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "unused");
		MockHttpServletResponse response = new MockHttpServletResponse();

		final String jsessionId = UUID.randomUUID().toString();
		final String originalHeaderValue = "JSESSIONID=\"" + jsessionId + "\"; Version=\"1\"; Path=\"/acme\"";

		proxyResponseHeaders.add(new Header(setCookieHeaderName, originalHeaderValue));

		proxy.proxyRequest("foo", request, response);

		String setCookieValue = (String) response.getHeader(setCookieHeaderName);
		Assert.assertNotNull(setCookieValue);
		Assert.assertEquals("almp." + originalHeaderValue, setCookieValue);

		context.assertIsSatisfied();
	}

	@Test
	public void testCookieRequestNonPrefixedCookiesFiltered() throws IOException {
		setupMock(HttpStatus.OK);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "unused");
		MockHttpServletResponse response = new MockHttpServletResponse();

		final String cookieHeaderValue = "$Version=\"1\"; Customer=\"WILE_E_COYOTE\"; $Path=\"/acme\";Part_Number=\"Rocket_Launcher_0001\"; $Path=\"/acme\"; Shipping=\"FedEx\"; $Path=\"/acme\"";
		request.addHeader("Cookie", cookieHeaderValue);

		proxy.proxyRequest("foo", request, response);

		List<String> proxyCookie = getProxyRequestHeaderValues("Cookie");
		Assert.assertNull(proxyCookie);
		context.assertIsSatisfied();
	}

	@Test
	public void testCookieRequestNonPrefixedCookiesFiltered2() throws IOException {
		setupMock(HttpStatus.OK);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "unused");
		MockHttpServletResponse response = new MockHttpServletResponse();

		final String cookieHeaderValue = "$Version=\"1\"; almp.Customer=\"WILE_E_COYOTE\"; $Path=\"/acme1\";Part_Number=\"Rocket_Launcher_0001\"; $Path=\"/acme2\"; Shipping=\"FedEx\"; $Path=\"/acme\"";
		request.addHeader("Cookie", cookieHeaderValue);

		proxy.proxyRequest("foo", request, response);

		List<String> proxyCookie = getProxyRequestHeaderValues("Cookie");
		Assert.assertNotNull(proxyCookie);
		Assert.assertEquals(1, proxyCookie.size());
		Assert.assertEquals("$Version=\"1\"; Customer=\"WILE_E_COYOTE\"; $Path=\"/acme1\";", proxyCookie.get(0));
		context.assertIsSatisfied();
	}

	@Test
	public void testCookieRequestNonPrefixedCookiesFiltered3() throws IOException {
		setupMock(HttpStatus.OK);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "unused");
		MockHttpServletResponse response = new MockHttpServletResponse();

		final String cookieHeaderValue = "$Version=\"1\"; Customer=\"WILE_E_COYOTE\"; $Path=\"/acme1\"; almp.Part_Number=\"Rocket_Launcher_0001\"; $Path=\"/acme2\"; Shipping=\"FedEx\"; $Path=\"/acme\"";
		request.addHeader("Cookie", cookieHeaderValue);

		proxy.proxyRequest("foo", request, response);

		List<String> proxyCookie = getProxyRequestHeaderValues("Cookie");
		Assert.assertNotNull(proxyCookie);
		Assert.assertEquals(1, proxyCookie.size());
		Assert.assertEquals("$Version=\"1\"; Part_Number=\"Rocket_Launcher_0001\"; $Path=\"/acme2\";",
				proxyCookie.get(0));
		context.assertIsSatisfied();
	}

	@Test
	public void testCookieRequestCookieValueUnquoted() throws IOException {
		setupMock(HttpStatus.OK);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "unused");
		MockHttpServletResponse response = new MockHttpServletResponse();

		final String cookieHeaderValue = "$Version=\"1\"; almp.Part_Number=Rocket_Launcher_0001; $Path=\"/acme2\";";
		request.addHeader("Cookie", cookieHeaderValue);

		proxy.proxyRequest("foo", request, response);

		List<String> proxyCookie = getProxyRequestHeaderValues("Cookie");
		Assert.assertNotNull(proxyCookie);
		Assert.assertEquals(1, proxyCookie.size());
		Assert.assertEquals("$Version=\"1\"; Part_Number=Rocket_Launcher_0001; $Path=\"/acme2\";", proxyCookie.get(0));
		context.assertIsSatisfied();
	}

	@Test
	public void testCookieRequestCookieNoTrailingSemi() throws IOException {
		setupMock(HttpStatus.OK);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "unused");
		MockHttpServletResponse response = new MockHttpServletResponse();

		final String cookieHeaderValue = "$Version=\"1\"; almp.Part_Number=\"Rocket_Launcher_0001\"";
		request.addHeader("Cookie", cookieHeaderValue);

		proxy.proxyRequest("foo", request, response);

		List<String> proxyCookie = getProxyRequestHeaderValues("Cookie");
		Assert.assertNotNull(proxyCookie);
		Assert.assertEquals(1, proxyCookie.size());
		Assert.assertEquals("$Version=\"1\"; Part_Number=\"Rocket_Launcher_0001\";", proxyCookie.get(0));
		context.assertIsSatisfied();
	}
}
