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
package com.tasktop.c2c.server.common.web.client.view;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.user.client.ui.HasEnabled;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public class CompositeHasEnabled implements HasEnabled {

	private Collection<HasEnabled> hasEnabledCollection;

	public CompositeHasEnabled(HasEnabled... hasEnabled) {
		hasEnabledCollection = Arrays.asList(hasEnabled);
	}

	/** Is enabled only if all the members are enabled. */
	@Override
	public boolean isEnabled() {
		for (HasEnabled hasEnabled : hasEnabledCollection) {
			if (!hasEnabled.isEnabled()) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		for (HasEnabled hasEnabled : hasEnabledCollection) {
			hasEnabled.setEnabled(enabled);
		}
	}

}
