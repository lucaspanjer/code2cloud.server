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
package com.tasktop.c2c.server.wiki.web.ui.shared.action;

import net.customware.gwt.dispatch.shared.AbstractSimpleResult;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */

public class RetrieveConfigurationPropertyResult extends AbstractSimpleResult<String> {
	protected RetrieveConfigurationPropertyResult() {
		super();
	}

	public RetrieveConfigurationPropertyResult(String value) {
		super(value);
	}

}
