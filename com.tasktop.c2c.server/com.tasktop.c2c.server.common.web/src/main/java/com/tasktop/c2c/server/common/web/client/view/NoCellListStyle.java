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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Style;

// No cell styles. This appears to be the only way to get the CellList to not plug in a bunch of default, undesired
// style info.
public class NoCellListStyle implements CellList.Style, CellList.Resources {
	@Override
	public String cellListEvenItem() {
		return "ZorkNarf";
	}

	@Override
	public String cellListKeyboardSelectedItem() {
		return "ZorkNarf";
	}

	@Override
	public String cellListOddItem() {
		return "ZorkNarf";
	}

	@Override
	public String cellListSelectedItem() {
		return "ZorkNarf";
	}

	@Override
	public String cellListWidget() {
		return "ZorkNarf";
	}

	@Override
	public String getName() {
		return "NoStyle";
	}

	@Override
	public boolean ensureInjected() {
		return true;
	}

	@Override
	public String getText() {
		return "";
	}

	@Override
	public ImageResource cellListSelectedBackground() {
		return null;
	}

	@Override
	public Style cellListStyle() {
		return this;
	}
}