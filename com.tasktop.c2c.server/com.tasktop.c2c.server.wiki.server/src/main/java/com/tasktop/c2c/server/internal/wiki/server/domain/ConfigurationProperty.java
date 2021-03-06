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
package com.tasktop.c2c.server.internal.wiki.server.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
@Entity
public class ConfigurationProperty extends BaseEntity {

	private String name;
	private String value;

	public Long getId() {
		return super.getId();
	}

	@Basic
	@Column(length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(length = 50)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
