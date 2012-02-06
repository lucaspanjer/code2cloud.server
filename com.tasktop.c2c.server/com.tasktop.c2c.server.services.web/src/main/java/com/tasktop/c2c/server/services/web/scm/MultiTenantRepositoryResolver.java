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
package com.tasktop.c2c.server.services.web.scm;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.springframework.tenancy.context.TenancyContextHolder;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class MultiTenantRepositoryResolver extends FileResolver<HttpServletRequest> implements
		RepositoryResolver<HttpServletRequest> {

	public MultiTenantRepositoryResolver() {
		setExportAll(true);
	}

	@Override
	public Repository open(HttpServletRequest req, String name) throws RepositoryNotFoundException,
			ServiceNotEnabledException {
		String fullname = resolveTenantName(name);
		return super.open(req, fullname);
	}

	/**
	 * @return
	 */
	private String resolveTenantName(String name) {
		return TenancyContextHolder.getContext().getTenant().getIdentity() + "/" + GitConstants.HOSTED_GIT_DIR + "/"
				+ name;
	}

	/**
	 * @param gitRoot
	 *            the gitRoot to set
	 */
	public void setGitRoot(String gitRoot) {
		exportDirectory(new File(gitRoot));
	}

}
