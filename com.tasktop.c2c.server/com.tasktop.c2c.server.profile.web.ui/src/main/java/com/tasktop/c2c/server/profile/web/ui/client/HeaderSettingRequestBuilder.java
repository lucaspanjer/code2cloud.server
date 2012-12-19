/*******************************************************************************
 * Copyright (c) 2012, Oracle and/or its affiliates. 
 * All rights reserved. 
 ******************************************************************************/
package com.tasktop.c2c.server.profile.web.ui.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class HeaderSettingRequestBuilder extends RpcRequestBuilder {

	protected void doFinish(RequestBuilder rb) {
		super.doFinish(rb);

		// Avoid caching of RPCs in IOS6 clients
		String existingHeader = rb.getHeader("Cache-Control");
		if (existingHeader == null || !existingHeader.equals("no-cache")) {
			rb.setHeader("Cache-Control", "no-cache");
		}
	}

}
