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
import java.util.List;

/**
 * Represents a source file with additional information.
 * 
 * @author phrebejk
 */
@SuppressWarnings("serial")
public class Blame implements Serializable {

	public String path;

	public String revision;

	public List<Line> lines;

	public List<Commit> commits;

	public static class Line implements Serializable {
		public int commit;
		public String text;
	}

}
