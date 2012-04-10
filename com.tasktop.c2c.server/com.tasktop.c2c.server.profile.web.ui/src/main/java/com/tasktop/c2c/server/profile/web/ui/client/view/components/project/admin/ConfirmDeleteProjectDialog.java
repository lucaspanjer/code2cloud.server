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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.ProjectAdminActivity;

/**
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 */
public class ConfirmDeleteProjectDialog extends DialogBox {

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, ConfirmDeleteProjectDialog> {
	}

	@UiField
	protected Button deleteButton;
	@UiField
	protected CheckBox confirmCheckbox;

	private static ConfirmDeleteProjectDialog instance;

	private ProjectAdminActivity presenter;

	public static ConfirmDeleteProjectDialog getInstance(ProjectAdminActivity presenter) {
		if (instance == null) {
			instance = new ConfirmDeleteProjectDialog();
		}
		instance.presenter = presenter;
		instance.confirmCheckbox.setValue(false);
		instance.deleteButton.setEnabled(false);
		return instance;
	}

	private ConfirmDeleteProjectDialog() {
		super(false, true);
		setText("Confirm Delete");
		setWidget(uiBinder.createAndBindUi(this));
		setAnimationEnabled(true); // Why not?
		setGlassEnabled(true);
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				if (isShowing()) {
					center();
				}
			}
		});
		confirmCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				deleteButton.setEnabled(event.getValue());

			}
		});
	}

	@UiHandler("cancelButton")
	void onCancel(ClickEvent event) {
		hide();
	}

	@UiHandler("deleteButton")
	void onDelete(ClickEvent event) {
		presenter.deleteProject();
		hide();
	}

}
