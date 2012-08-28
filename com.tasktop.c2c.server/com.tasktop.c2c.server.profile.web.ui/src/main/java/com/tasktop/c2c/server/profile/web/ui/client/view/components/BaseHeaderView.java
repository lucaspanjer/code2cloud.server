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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.Section;
import com.tasktop.c2c.server.common.profile.web.shared.Credentials;
import com.tasktop.c2c.server.common.web.client.view.AbstractComposite;
import com.tasktop.c2c.server.common.web.client.view.Avatar;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.web.ui.client.ProfileEntryPoint;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class BaseHeaderView extends AbstractComposite implements Header {

	@UiField
	public ProjectIconPanel iconPanel;
	@UiField
	public Anchor signIn;
	@UiField
	public Panel userMenu;
	@UiField(provided = true)
	public Image avatarImage = AvatarHolder.avatarImage;
	@UiField
	public Label title;
	@UiField
	public Label ownerBadge;
	@UiField
	public DivElement projectSpecificDiv;
	@UiField
	public Panel breadcrumbNavigation;
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
		signIn.setVisible(!isAuthenticated);

		if (isAuthenticated) {

			String username = ProfileEntryPoint.getInstance().getAppState().getCredentials().getProfile().getUsername();

			// Set the user's name as the title and alt text of the Gravatar image
			avatarImage.setTitle(username);
			avatarImage.setAltText(username);
		} else {
			// Blank out any existing text
			avatarImage.setTitle("");
			avatarImage.setAltText("");
		}
	}

	private List<Breadcrumb> breadcrumbs;

	protected static class AvatarHolder {
		public static String lastAvatarUrl = null;
		public static Image avatarImage = new Image();
	}

	@UiHandler("userMenuClickArea")
	public abstract void showMenu(ClickEvent e);

	public void setProject(Project project) {
		if (project == null) {
			projectSpecificDiv.setClassName("hide-project-nav");
			this.ownerBadge.setText("");
		} else {
			projectSpecificDiv.removeClassName("hide-project-nav");
			iconPanel.setProject(project);

			if (AuthenticationHelper.isAdmin(project.getIdentifier())) {
				this.ownerBadge.setText("owner");
			}
		}
	}

	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	public void setBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
		breadcrumbNavigation.clear();
		boolean first = true;
		for (Breadcrumb breadcrumb : breadcrumbs) {
			if (first == false) {
				InlineHTML span = new InlineHTML();
				span.setStyleName("arrow");
				span.setText("/");
				breadcrumbNavigation.add(span);
			}
			Anchor link = new Anchor(breadcrumb.getLabel(), "#" + breadcrumb.getUri());
			link.setStyleName("crumb");
			breadcrumbNavigation.add(link);

			first = false;
		}

	}

	public void setSection(Section section) {
		iconPanel.setActiveIcon(section);
	}

	public void setPageTitle(String title) {
		this.title.setText(title);
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