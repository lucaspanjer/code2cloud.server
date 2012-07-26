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

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class HttpProxySetup implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(HttpProxySetup.class);

	private BaseProfileConfiguration profileConfiguration;

	private boolean dontProxyToProfile = true;

	public void setupProxyFromEnvironment() {
		String http_proxy = System.getenv().get("http_proxy");

		if (http_proxy != null) {
			if (!http_proxy.startsWith("http://")) {
				http_proxy = "http://" + http_proxy;
			}

			logger.info("Configuring http proxy as: " + http_proxy);
			try {
				URL proxyUrl = new URL(http_proxy);
				System.setProperty("http.proxyHost", proxyUrl.getHost());
				if (proxyUrl.getPort() != 80) {
					System.setProperty("http.proxyPort", proxyUrl.getPort() + "");
				}

			} catch (MalformedURLException e) {
				logger.error("Error configuring proxy", e);
			}

		}

		String https_proxy = System.getenv().get("https_proxy");
		if (https_proxy != null) {
			if (!https_proxy.startsWith("https://")) {
				https_proxy = "https://" + https_proxy;
			}

			logger.info("Configuring https proxy as: " + https_proxy);
			try {
				URL proxyUrl = new URL(https_proxy);
				System.setProperty("https.proxyHost", proxyUrl.getHost());
				if (proxyUrl.getPort() != 443) {
					System.setProperty("https.proxyPort", proxyUrl.getPort() + "");
				}
			} catch (MalformedURLException e) {
				logger.error("Error configuring proxy", e);
			}

		}

		String no_proxy = System.getenv().get("no_proxy");

		if (no_proxy != null) {
			no_proxy = no_proxy.replace(",", "|");
		}

		if (dontProxyToProfile) {
			if (no_proxy == null) {
				no_proxy = "";
			} else {
				no_proxy = no_proxy + ",";
			}
			no_proxy = no_proxy + profileConfiguration.getWebHost() + "|*." + profileConfiguration.getWebHost();
		}

		if (no_proxy != null && !no_proxy.isEmpty()) {
			logger.info("Configuring no_proxy as: " + no_proxy);

			System.setProperty("http.nonProxyHosts", no_proxy);
			System.setProperty("https.nonProxyHosts", no_proxy);
		}

	}

	public void afterPropertiesSet() throws Exception {
		setupProxyFromEnvironment();
	}

	public void setProfileConfiguration(BaseProfileConfiguration profileConfiguration) {
		this.profileConfiguration = profileConfiguration;
	}

	public void setDontProxyToProfile(boolean dontProxyToProfile) {
		this.dontProxyToProfile = dontProxyToProfile;
	}

}
