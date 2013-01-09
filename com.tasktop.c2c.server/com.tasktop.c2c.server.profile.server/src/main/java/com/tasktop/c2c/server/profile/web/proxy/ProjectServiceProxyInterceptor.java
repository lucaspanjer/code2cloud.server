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
package com.tasktop.c2c.server.profile.web.proxy;

import com.tasktop.c2c.server.auth.service.proxy.ProxyHttpServletRequest;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * This provides an opportunity to update a {@link ProxyHttpServletRequest} for a {@link ProjectService}.
 * 
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public interface ProjectServiceProxyInterceptor {

	public abstract void prepareProxyRequest(ProxyHttpServletRequest proxyRequest, ProjectService service);
}