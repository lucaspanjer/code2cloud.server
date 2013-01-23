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
package com.tasktop.c2c.server.profile.service.provider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.tasktop.c2c.server.auth.service.AuthenticationToken;
import com.tasktop.c2c.server.auth.service.proxy.ProxyClient;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.service.ProjectServiceService;

public class InternalProxyInvocationHandler implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <T> T wrap(T service, AuthenticationToken token, ProjectServiceService projectServiceService,
			ProjectService projectService) {
		final InternalProxyInvocationHandler handler = new InternalProxyInvocationHandler(service);
		handler.setAuthenticationToken(token);
		handler.setProjectServiceService(projectServiceService);
		handler.setProjectService(projectService);
		return (T) Proxy.newProxyInstance(service.getClass().getClassLoader(), computeInterfaces(service), handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T wrap(T service, InternalProxyInvocationHandler handler) {
		handler.implementation = service;
		return (T) Proxy.newProxyInstance(service.getClass().getClassLoader(), computeInterfaces(service), handler);
	}

	private static Class<?>[] computeInterfaces(Object o) {
		Class<? extends Object> clazz = o.getClass();
		List<Class<?>> allInterfaces = new ArrayList<Class<?>>();
		while (clazz != Object.class) {
			for (Class<?> c : clazz.getInterfaces()) {
				allInterfaces.add(c);
			}
			clazz = clazz.getSuperclass();
		}
		return allInterfaces.toArray(new Class<?>[allInterfaces.size()]);
	}

	private AuthenticationToken authenticationToken;
	private ProjectService projectService;
	private ProjectServiceService projectServiceService;
	private Object implementation;

	public InternalProxyInvocationHandler() {
	}

	public InternalProxyInvocationHandler(Object implementation) {
		this.implementation = implementation;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		ProxyClient.setAuthenticationToken(getAuthenticationToken());
		try {
			return method.invoke(implementation, args);
		} catch (InvocationTargetException e) {
			if (isConnectionFailure(e.getCause())) {
				projectServiceService.handleConnectFailure(projectService);
			}
			throw e.getCause();
		} finally {
			ProxyClient.setAuthenticationToken(null);
		}
	}

	private boolean isConnectionFailure(Throwable e) {
		return e instanceof SocketException || (e.getCause() != null && isConnectionFailure(e.getCause()));
	}

	public AuthenticationToken getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(AuthenticationToken authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public void setProjectServiceService(ProjectServiceService projectServiceService) {
		this.projectServiceService = projectServiceService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
}
