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
package com.tasktop.c2c.server.tasks.domain;

import java.io.Serializable;

public class TaskUserProfile extends AbstractDomainObject implements Comparable<TaskUserProfile>, Serializable {
	private static final long serialVersionUID = 1L;

	private String loginName;
	private String realname;

	private String gravatarHash;

	public TaskUserProfile() {
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public void setGravatarHash(String gravatarHash) {
		this.gravatarHash = gravatarHash;
	}

	public String getGravatarHash() {
		return gravatarHash;
	}

	@Override
	public String toString() {
		String str = getRealname();
		if (str == null || str.trim().length() == 0) {
			// If there's no name, then still return a string - if we don't do this, then Spring's validation system
			// blows up since it does object.toString(), so even if there's a valid non-null value in a field we still
			// get a validation failure.
			return "no name, ID: " + getId();
		} else {
			return str;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * ((loginName == null) ? 0 : loginName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		TaskUserProfile other = (TaskUserProfile) obj;
		if (loginName == null) {
			if (other.loginName != null)
				return false;
		} else if (!loginName.equals(other.loginName))
			return false;
		return true;
	}

	public int compareTo(TaskUserProfile taskUserProfile) {
		int realNameComparison = this.getRealname().compareTo(taskUserProfile.getRealname());
		if (realNameComparison == 0) {
			return this.getLoginName().compareTo(taskUserProfile.getLoginName());
		}
		return realNameComparison;
	}
}
