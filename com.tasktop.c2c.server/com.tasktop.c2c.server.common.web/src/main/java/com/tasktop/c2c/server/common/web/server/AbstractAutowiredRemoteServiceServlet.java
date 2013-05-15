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
package com.tasktop.c2c.server.common.web.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.tasktop.c2c.server.common.service.ConcurrentUpdateException;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.web.shared.AuthenticationRequiredException;
import com.tasktop.c2c.server.common.web.shared.NoSuchEntityException;
import com.tasktop.c2c.server.common.web.shared.ValidationFailedException;

@SuppressWarnings("serial")
public abstract class AbstractAutowiredRemoteServiceServlet extends RemoteServiceServlet {
	// IMPLEMENTATION NOTE: we can't rely on init() for initialization, since init() may be called without
	// a wrapping servlet filter to provide request context.
	private ServiceStrategy serviceStrategy = new InitializingServiceStrategy();

	public AbstractAutowiredRemoteServiceServlet() {
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serviceStrategy.service(request, response);
	}

	private interface ServiceStrategy {
		public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
				IOException;
	}

	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			RPCRequest rpcRequest = RPC.decodeRequest(payload, this.getClass(), this);
			onAfterRequestDeserialized(rpcRequest);
			return invokeAndEncodeResponse(rpcRequest.getMethod(), rpcRequest.getParameters(),
					rpcRequest.getSerializationPolicy(), rpcRequest.getFlags());
		} catch (IncompatibleRemoteServiceException ex) {
			log("An IncompatibleRemoteServiceException was thrown while processing this call.", ex);
			return RPC.encodeResponseForFailure(null, ex);
		}
	}

	private String invokeAndEncodeResponse(Method serviceMethod, Object[] args,
			SerializationPolicy serializationPolicy, int flags) throws SerializationException {
		if (serviceMethod == null) {
			throw new NullPointerException("serviceMethod");
		}

		if (serializationPolicy == null) {
			throw new NullPointerException("serializationPolicy");
		}

		String responsePayload;
		try {
			Object result = serviceMethod.invoke(this, args);
			responsePayload = RPC.encodeResponseForSuccess(serviceMethod, result, serializationPolicy, flags);
		} catch (IllegalAccessException e) {
			SecurityException securityException = new SecurityException("Cannot access " + serviceMethod.getName());
			securityException.initCause(e);
			throw securityException;
		} catch (IllegalArgumentException e) {
			SecurityException securityException = new SecurityException("Cannot access " + serviceMethod.getName());
			securityException.initCause(e);
			throw securityException;
		} catch (InvocationTargetException e) {
			// Try to encode the caught exception
			//
			Throwable cause = e.getCause();
			cause = convertException(cause);
			responsePayload = RPC.encodeResponseForFailure(serviceMethod, cause, serializationPolicy, flags);
		}

		return responsePayload;
	}

	/**
	 * Convert exceptions to an alternative that will be recognized by the client application
	 */
	protected Throwable convertException(Throwable cause) {
		if ((cause instanceof AuthenticationCredentialsNotFoundException)
				|| (cause instanceof InsufficientAuthenticationException)) {
			cause = new AuthenticationRequiredException();
		}
		return cause;
	}

	private class InitializingServiceStrategy implements ServiceStrategy {

		@Override
		public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
				IOException {
			synchronized (this) {
				if (serviceStrategy == this) {
					Object beanToInitialize = AbstractAutowiredRemoteServiceServlet.this;
					try {
						WebApplicationContext context = WebApplicationContextUtils
								.getRequiredWebApplicationContext(getServletContext());
						context.getAutowireCapableBeanFactory().autowireBean(beanToInitialize);
					} catch (Throwable t) {
						String message = "Cannot autowire " + beanToInitialize.getClass().getName() + ": "
								+ t.getMessage();
						LoggerFactory.getLogger(beanToInitialize.getClass().getName()).error(message, t);
						getServletContext().log(message, t);
						if (t instanceof RuntimeException) {
							throw (RuntimeException) t;
						}
						throw new IllegalStateException(t);
					} finally {
						serviceStrategy = new DefaultServiceStrategy();
					}
				}
			}
			serviceStrategy.service(request, response);
		}

	}

	private class DefaultServiceStrategy implements ServiceStrategy {
		@Override
		public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
				IOException {
			doService(request, response);
		}
	}

	private void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		super.service(request, response);
	}

	protected void handle(ValidationException exception) throws ValidationFailedException {
		throw new ValidationFailedException(exception.getMessages());
	}

	// TODO : handle this correctly
	protected void handle(ConcurrentUpdateException exception) throws ValidationFailedException {
		throw new ValidationFailedException(Arrays.asList("The object has been modified since it was loaded"));
	}

	protected void handle(EntityNotFoundException e) throws NoSuchEntityException {
		throw new NoSuchEntityException();
	}
}
