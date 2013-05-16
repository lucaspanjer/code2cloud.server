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

import java.util.List;

/**
 * 
 * @author phrebejk
 */
@SuppressWarnings("serial")
public class Blob extends Item {

	public Blob() {
	}

	public Blob(String sha) {
		super(sha, Type.BLOB);
	}

	private List<String> lines;
	private boolean large;
	private boolean binary;

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public boolean isLarge() {
		return large;
	}

	public void setLarge(boolean large) {
		this.large = large;
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	public String getFullContent() {
		if (lines == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();

		for (String line : lines) {
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}

}
