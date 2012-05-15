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
package com.tasktop.c2c.server.scm.web.ui.client.shared.action;

import net.customware.gwt.dispatch.shared.AbstractSimpleResult;

import com.tasktop.c2c.server.scm.domain.Commit;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetScmCommitResult extends AbstractSimpleResult<Commit> {
	protected GetScmCommitResult() {
		super();
	}

	public GetScmCommitResult(Commit value) {
		super(value);
	}

}
