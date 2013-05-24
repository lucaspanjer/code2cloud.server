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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.profile.domain.build.JobSummary;
import com.tasktop.c2c.server.profile.web.ui.client.widgets.build.BuildResources;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class HudsonJobRow extends Composite {

	interface Binder extends UiBinder<Widget, HudsonJobRow> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);
	public static BuildResources buildResources = GWT.create(BuildResources.class);

	@UiField
	protected SimplePanel colorPanel;
	@UiField
	protected Anchor jobAnchor;
	@UiField
	protected Element lastBuildDiv;
	@UiField
	protected Anchor lastBuildAnchor;

	public HudsonJobRow(JobSummary job) {
		initWidget(uiBinder.createAndBindUi(this));
		colorPanel.setWidget(getWidgetFromColour(job.getColor()));
		jobAnchor.setText(job.getName());
		jobAnchor.setHref(job.getUrl());
		UIObject.setVisible(lastBuildDiv, job.getLastBuild() != null);
		if (job.getLastBuild() != null) {
			lastBuildAnchor.setHref(job.getLastBuild().getUrl());
			lastBuildAnchor.setText("#" + job.getLastBuild().getNumber());
			// lastBuildTime.setText(Format.stringValueDateTime(new Date(job.getLastBuild().getTimestamp())));
		}

	}

	private Widget getWidgetFromColour(String color) {
		if (color.equals("blue") || color.equals("green")) {
			return new Image(buildResources.stableBuild());
		} else if (color.equals("red")) {
			return new Image(buildResources.failedBuild());
		} else if (color.equals("yellow")) {
			return new Image(buildResources.unstableBuild());
		} else if (color.equals("grey")) {
			return new Image(buildResources.canceledBuild());
		} else if (color.equals("aborted")) {
			return new Image(buildResources.canceledBuild());
		} else if (color.equals("disabled")) {
			return new Image(buildResources.disabledBuild());
		} else if (color.equals("blue_anime") || color.equals("green_anime")) {
			return new Image(buildResources.stableBuilding());
		} else if (color.equals("red_anime")) {
			return new Image(buildResources.failedBuilding());
		} else if (color.equals("yellow_anime")) {
			return new Image(buildResources.unstableBuilding());
		} else if (color.equals("grey_anime")) {
			return new Image(buildResources.canceledBuilding());
		} else if (color.equals("aborted_anime")) {
			return new Image(buildResources.canceledBuilding());
		} else {
			return new Label(color);
		}
	}

}
