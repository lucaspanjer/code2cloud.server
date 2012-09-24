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
package com.tasktop.c2c.server.profile.web.ui.client.view.components;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.NoSelectionModel;
import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectHomePlace;
import com.tasktop.c2c.server.common.profile.web.shared.Credentials;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.view.AbstractComposite;
import com.tasktop.c2c.server.common.web.client.view.NoCellListStyle;
import com.tasktop.c2c.server.common.web.client.widgets.Pager;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectAccessibility;
import com.tasktop.c2c.server.profile.domain.project.ProjectRelationship;
import com.tasktop.c2c.server.profile.web.ui.client.ProfileEntryPoint;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectDashboardPlace;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.components.IProjectDiscoryView;

public class ProjectDiscoveryView extends AbstractComposite implements IProjectDiscoryView {

	interface Binder extends UiBinder<Widget, ProjectDiscoveryView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private static ProjectDiscoveryView instance = null;

	public static ProjectDiscoveryView getInstance() {
		if (instance == null) {
			instance = new ProjectDiscoveryView();
		}
		return instance;
	}

	@UiField
	DivElement filterDivElement;
	@UiField
	Anchor publicFilterAnchor;
	@UiField
	Anchor watcherFilterAnchor;
	@UiField
	Anchor organizationFilterAnchor;
	@UiField
	Anchor memberFilterAnchor;
	@UiField
	Anchor ownerFilterAnchor;
	@UiField
	Anchor allFilterAnchor;

	Anchor watchLink;
	private Anchor[] filterAnchors;

	@UiField
	public AnchorElement createAnchorElement;
	@UiField
	public AnchorElement orgAdminElement;

	@UiField
	Panel projectsPanel;

	@UiField
	public Pager pager;

	private CellList<Project> projectList;
	private NoSelectionModel<Project> model;
	private Presenter presenter;

	private ProjectDiscoveryView() {
		initWidget(uiBinder.createAndBindUi(this));

		projectList = new CellList<Project>(new ProjectCell(), new NoCellListStyle());
		model = new NoSelectionModel<Project>();

		watchLink = new Anchor();
		watchLink.setStyleName("button watch right");
		DOM.setElementProperty(watchLink.getElement(), "id", "watchButton");

		projectList.setSelectionModel(model);
		projectsPanel.add(projectList);
		pager.setDisplay(projectList);
		pager.itemLabel.setText("projects:");
		setupFilterAnchors();
	}

	private void setupFilterAnchors() {
		filterAnchors = new Anchor[] { publicFilterAnchor, watcherFilterAnchor, organizationFilterAnchor,
				memberFilterAnchor, ownerFilterAnchor, allFilterAnchor };
		publicFilterAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedFilterChanged(ProjectRelationship.PUBLIC);

			}
		});
		watcherFilterAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedFilterChanged(ProjectRelationship.WATCHER);

			}
		});
		organizationFilterAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedFilterChanged(ProjectRelationship.ORGANIZATION_PRIVATE);

			}
		});
		memberFilterAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedFilterChanged(ProjectRelationship.MEMBER);

			}
		});
		ownerFilterAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedFilterChanged(ProjectRelationship.OWNER);

			}
		});
		allFilterAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedFilterChanged(ProjectRelationship.ALL);

			}
		});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		update();
	}

	private void setupWatchButton(final Project newProject) {

		if (AuthenticationHelper.isAnonymous() || !newProject.getAccessibility().equals(ProjectAccessibility.PUBLIC)) {
			// Can't watch if you're anonymous.
			watchLink.setVisible(false);
			return;
		} else {
			// Make sure this is visible.
			watchLink.setVisible(true);
		}

		// Check to see if I'm currently watching this project.
		boolean isWatching = AuthenticationHelper.isWatching(newProject.getIdentifier());

		if (isWatching) {
			watchLink.addStyleName("watching");
			watchLink.setHTML("<span></span>Watching");
			watchLink.setEnabled(false);
		} else {
			watchLink.addStyleName("watch");
			watchLink.setHTML("<span></span>Watch");
			watchLink.setEnabled(true);
		}

	}

	private void update() {
		UIObject.setVisible(filterDivElement, !AuthenticationHelper.isAnonymous());
		UIObject.setVisible(createAnchorElement, !AuthenticationHelper.isAnonymous());
		setSelectedFilter(presenter.getProjectRelationship());
		setCurrentQuery(presenter.getCurrentQuery());
		// this.projectList.setRowData(presenter.getCurrentResult().getResultPage());
	}

	/**
	 * @param currentQuery
	 */
	private void setCurrentQuery(String currentQuery) {
		if (currentQuery != null) {

		}
	}

	interface PageTemplate extends SafeHtmlTemplates {

		@Template("<div class=\"discover-project\">{1}<div class=\"discover-watch\">{5}</div><div class=\"project-heading\"><h2>{0}</h2><div class=\"clear\"></div><div class=\"project-activity\"><span class=\"timestamp left\">{4}</span>{2}<div class=\"clear\"></div></div><div class=\"clear\"></div></div><p class=\"project-description\">{3}</p></div>")
		SafeHtml createProjectCell(SafeHtml title, SafeHtml pointer, SafeHtml commitWatchLabels, SafeHtml desc,
				SafeHtml dashboardLink, SafeHtml button);

		@Template("<div class=\"pointer-holder\"><div class=\"pointer\"></div></div>")
		SafeHtml createPointer();

		@Template("<span class=\"misc-icon watchers\"><span></span>{0}</span><span class=\"misc-icon left\"><span></span>{1}</span>")
		SafeHtml createCommitterAndWatcherLabels(int numWatchers, int numCommitters);
	}

	private static final PageTemplate TEMPLATE = GWT.create(PageTemplate.class);

	private final class ProjectCell extends AbstractCell<Project> {

		public ProjectCell() {
			super("click");
		}

		@Override
		public void onBrowserEvent(final Context context, final Element parent, final Project value, NativeEvent event,
				ValueUpdater<Project> valueUpdater) {
			EventTarget target = event.getEventTarget();
			Element element = Element.as(target);
			while (element.hasParentElement()) {
				if (element.getId().equals("watchButton")) {
					ProfileEntryPoint.getInstance().getProfileService()
							.watchProject(value.getIdentifier(), new AsyncCallbackSupport<Void>() {
								@Override
								protected void success(Void result) {
									Credentials credentials = ProfileEntryPoint.getInstance().getAppState()
											.getCredentials();
									credentials.getRoles().add(Role.Community + "/" + value.getIdentifier());

									setValue(context, parent, value);
								}
							});
					return;
				}
				element = element.getParentElement();
			}
		}

		@Override
		public void render(Context context, Project value, SafeHtmlBuilder sb) {

			// Checking for null value and bailing out as recommended at
			// https://code.google.com/webtoolkit/doc/trunk/DevGuideUiCellWidgets.html#custom-cell
			if (value == null) {
				return;
			}

			// First, get the values we will care about during rendering. Make sure these are rendered safe to avoid
			// XSS.
			SafeHtml safeDesc = SafeHtmlUtils.fromString("");
			if (value.getDescription() != null) {
				safeDesc = SafeHtmlUtils.fromString(value.getDescription());
			}
			boolean isSelected = projectList.getSelectionModel().isSelected(value);

			// Only generate a pointer if this item is selected
			SafeHtml pointer = SafeHtmlUtils.EMPTY_SAFE_HTML;
			if (isSelected) {
				// pointer = TEMPLATE.createPointer();
			}

			// TODO

			// FIXME hook up watcher and member counts
			SafeHtml commiterWatcherLabel = TEMPLATE.createCommitterAndWatcherLabels(value.getNumWatchers(),
					value.getNumCommiters());

			String projectLinkClass = "misc-icon";

			if (AuthenticationHelper.isWatching(value.getIdentifier())) {
				projectLinkClass += " watcher";
			} else if (AuthenticationHelper.isCommitter(value.getIdentifier())) {
				projectLinkClass += " commiter";
			}
			String projectLink = "<a href=\"" + ProjectHomePlace.createPlace(value.getIdentifier()).getHref()
					+ "\" class=\"misc-icon\">" + value.getName() + "</a>";

			SafeHtml projectLinkHtml = new SafeHtmlBuilder().appendHtmlConstant(projectLink).toSafeHtml();

			Anchor dashboardLink = new Anchor("View Project Dashboard", ProjectDashboardPlace.createPlace(
					value.getIdentifier()).getHref());
			SafeHtml dashboardLinkHtml = new SafeHtmlBuilder().appendHtmlConstant(dashboardLink.toString())
					.toSafeHtml();

			setupWatchButton(value);
			SafeHtml safeWatchLink = new SafeHtmlBuilder().appendHtmlConstant(watchLink.getElement().getString())
					.toSafeHtml();

			// Now, spit out HTML to render this row.
			sb.append(TEMPLATE.createProjectCell(projectLinkHtml, pointer, commiterWatcherLabel, safeDesc,
					dashboardLinkHtml, safeWatchLink));

		}
	}

	private void selectedFilterChanged(ProjectRelationship projectRelationship) {
		setSelectedFilter(projectRelationship);
		presenter.setProjectRelationship(projectRelationship); // Triggers RPC
	}

	private void setSelectedFilter(ProjectRelationship projectRelationship) {
		if (projectRelationship == null) {
			updateFilterStyle(null);
			return;
		}
		switch (projectRelationship) {
		case ALL:
			updateFilterStyle(allFilterAnchor);
			break;
		case MEMBER:
			updateFilterStyle(memberFilterAnchor);
			break;
		case OWNER:
			updateFilterStyle(ownerFilterAnchor);
			break;
		case ORGANIZATION_PRIVATE:
			updateFilterStyle(organizationFilterAnchor);
			break;
		case PUBLIC:
			updateFilterStyle(publicFilterAnchor);
			break;
		case WATCHER:
			updateFilterStyle(watcherFilterAnchor);
			break;
		}
	}

	private void updateFilterStyle(Anchor selectedFilterAnchor) {
		for (Anchor a : filterAnchors) {
			if (a.equals(selectedFilterAnchor)) {
				a.getElement().addClassName(ProfileGinjector.get.instance().getAppResources().appCss().selected());
			} else {
				a.getElement().removeClassName(ProfileGinjector.get.instance().getAppResources().appCss().selected());
			}
		}
	}

	public HasData<Project> getProjectsDisplay() {
		return projectList;
	}

	public void setOrganizationFilterVisible(boolean visible) {
		organizationFilterAnchor.setVisible(visible);
	}

	public void setPublicFilterVisible(boolean visible) {
		publicFilterAnchor.setVisible(visible);
	}

	public void setWatcherFilterVisible(boolean visible) {
		watcherFilterAnchor.setVisible(visible);
	}

	public void setOrgAdminButtonVisible(boolean visible) {
		UIObject.setVisible(orgAdminElement, visible);
	}
}
