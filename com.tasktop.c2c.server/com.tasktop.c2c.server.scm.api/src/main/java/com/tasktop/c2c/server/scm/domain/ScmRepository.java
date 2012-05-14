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
package com.tasktop.c2c.server.scm.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ScmRepository implements Serializable {

	private String name; // CUrrently only used on create
	private String url;
	private ScmType type;
	private ScmLocation scmLocation;
	private String alternateUrl;

	public String getUrl() {
		return url;
	}

	public void setUrl(String newUrl) {
		this.url = newUrl;
	}

	public ScmType getType() {
		return type;
	}

	public void setType(ScmType newType) {
		this.type = newType;
	}

	public ScmLocation getScmLocation() {
		return scmLocation;
	}

	public void setScmLocation(ScmLocation scmLocation) {
		this.scmLocation = scmLocation;
	}

	public void setAlternateUrl(String alternateUrl) {
		this.alternateUrl = alternateUrl;
	}

	/**
	 * some repositories have multiple URLs
	 * 
	 * @return the alternate URL, or null if there is none
	 */
	public String getAlternateUrl() {
		return alternateUrl;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScmRepository other = (ScmRepository) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
