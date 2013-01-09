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

import java.util.Set;

import com.tasktop.c2c.server.auth.service.proxy.ProxyHttpServletRequest;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;

/**
 * This contains the Set of compatible ServiceTypes for each concrete {@link ProjectServiceProxyInterceptor}.
 * 
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractProjectServiceProxyInterceptor implements ProjectServiceProxyInterceptor {

	protected Set<ServiceType> compatibleServiceTypes;

	@Override
	abstract public void prepareProxyRequest(ProxyHttpServletRequest proxyRequest, ProjectService service);

	public void setCompatibleServiceTypes(Set<ServiceType> serviceTypes) {
		this.compatibleServiceTypes = serviceTypes;
	}
}
