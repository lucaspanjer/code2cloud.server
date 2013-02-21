/*******************************************************************************
 * Copyright (c) 2010, 2012 Oracle and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Oracle and/or its affiliates.
 ******************************************************************************/
package com.tasktop.c2c.server.scm.domain;

import java.io.Serializable;

/**
 * 
 * @author phrebejk
 */
@SuppressWarnings("serial")
public class Item implements Serializable {

	private String sha;
	private Type type;

	public Item() {
	}

	public Item(String sha, Type type) {
		this.sha = sha;
		this.type = type;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Types of git objects
	 * <ul>
	 * <li>XXX : add COMMIT
	 */
	public static enum Type {
		BLOB, TREE, COMMIT;
	}

}
