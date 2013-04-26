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
package com.tasktop.c2c.server.profile.domain.project;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ProjectTemplateProperty implements Serializable {

	public enum PropertyType {
		STRING, URL, BOOLEAN;
	}

	private String id;
	private String name;
	private PropertyType type;
	private List<? extends Object> availableValues;
	private Object value;
	private boolean required;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<? extends Object> getAvailableValues() {
		return availableValues;
	}

	public void setAvailableValues(List<? extends Object> availableValues) {
		this.availableValues = availableValues;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}