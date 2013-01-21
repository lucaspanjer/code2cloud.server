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
package com.tasktop.c2c.server.common.service.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.filter.GenericFilterBean;

/**
 * A filter that exposes a "last chance" to do something before the response gets committed. On use case is when you
 * want to set headers as late as possible when handling a request.
 * 
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public abstract class LastChanceBeforeCommitFilter extends GenericFilterBean {
	// A wrapped response that will attempt to drop the locale cookie before the response is committed (and no new
	// headers can be written).
	private class LastChanceWrapper extends HttpServletResponseWrapper {

		private HttpServletRequest request;
		private boolean didLastChance = false;

		public LastChanceWrapper(HttpServletRequest request, HttpServletResponse wrapped) {
			super(wrapped);
			this.request = request;
		}

		@Override
		public void flushBuffer() throws IOException {
			maybeDoLastChanceBeforeCommit();
			super.flushBuffer();
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			maybeDoLastChanceBeforeCommit();
			return super.getWriter();
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			maybeDoLastChanceBeforeCommit();
			return super.getOutputStream();
		}

		void maybeDoLastChanceBeforeCommit() {
			if (didLastChance) {
				return;
			}
			lastChanceBeforeCommit(request, this);
			didLastChance = true;
		}
	}

	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		LastChanceWrapper wrapper = new LastChanceWrapper((HttpServletRequest) request, (HttpServletResponse) response);
		chain.doFilter(request, response);
		if (!!response.isCommitted()) {
			wrapper.maybeDoLastChanceBeforeCommit();
		}

	}

	protected abstract void lastChanceBeforeCommit(HttpServletRequest request, HttpServletResponse wrapped);
}
