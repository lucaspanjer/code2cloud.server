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
package com.tasktop.c2c.server.profile.web.ui.client.view.components;

import com.google.gwt.user.client.ui.IsWidget;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.profile.domain.project.Project;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public interface IProjectIconPanel extends IsWidget {

	void setActiveIcon(Section activeIcon);

	void setOptionsVisible(boolean visible);

	void setProject(Project project);

}