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

import java.util.List;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.shared.Credentials;
import com.tasktop.c2c.server.common.web.client.view.AbstractComposite;
import com.tasktop.c2c.server.common.web.client.view.Avatar;
import com.tasktop.c2c.server.profile.domain.project.Project;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class BaseHeaderView extends AbstractComposite implements Header {

	@UiField
	public IProjectIconPanel iconPanel;
	@UiField
	public Panel userMenu;

	@UiField
	public DivElement projectSpecificDiv;
	protected Presenter presenter;

	public BaseHeaderView() {

	}

	public void setCredentials(Credentials c) {
		if (c == null) {
			setAuthenticated(false);
		} else {
			setAuthenticated(true);
		}
	}

	protected void setAuthenticated(boolean isAuthenticated) {
		// If this user is authenticated, then display the "Projects" link.
		userMenu.setVisible(isAuthenticated);
	}

	protected static class AvatarHolder {
		public static String lastAvatarUrl = null;
		public static Image avatarImage = new Image();
	}

	public void setProject(Project project) {
		if (project == null) {
			projectSpecificDiv.setClassName("hide-project-nav");
		} else {
			projectSpecificDiv.removeClassName("hide-project-nav");
		}
		iconPanel.setProject(project);
	}

	public void setBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		// no-op
	}

	public void setSection(Section section) {
		iconPanel.setActiveIcon(section);
	}

	public void setGravatarHash(String hash) {
		String avatar = Avatar.computeAvatarUrl(hash, Avatar.Size.SMALL);
		if (!avatar.equals(AvatarHolder.lastAvatarUrl)) {
			AvatarHolder.avatarImage.setUrl(avatar);
			AvatarHolder.lastAvatarUrl = avatar;
		}
	}

	public void setPresenter(Presenter p) {
		this.presenter = p;
	}

	public void setSearchText(String currentQuery) {
		// no-op
	}

}