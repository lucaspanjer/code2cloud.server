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
package com.tasktop.c2c.server.profile.web.ui.client.view.deployment;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceType;
import com.tasktop.c2c.server.profile.web.ui.client.view.deployment.DeploymentsView.Presenter;

public class NewDeploymentView extends Composite {
	interface Binder extends UiBinder<Widget, NewDeploymentView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	TextBox name;
	@UiField(provided = true)
	ValueListBox<DeploymentServiceType> serviceType = new ValueListBox<DeploymentServiceType>(
			new AbstractRenderer<DeploymentServiceType>() {

				@Override
				public String render(DeploymentServiceType type) {
					return type.getName();
				}

			});
	@UiField
	protected DivElement serviceTypeDiv;
	@UiField
	protected DivElement credentialsDiv;
	@UiField
	protected CredentialsEditView credentialsEditView;
	@UiField
	protected Button saveButton;
	@UiField
	protected Button cancelButton;

	private Presenter presenter;

	public NewDeploymentView() {
		initWidget(uiBinder.createAndBindUi(this));
		credentialsEditView.setUserUrlEditable(true);

		serviceType.addValueChangeHandler(new ValueChangeHandler<DeploymentServiceType>() {

			@Override
			public void onValueChange(ValueChangeEvent<DeploymentServiceType> event) {
				serviceTypeSelected(event.getValue());
			}
		});
	}

	public DeploymentConfiguration getValue() {
		DeploymentConfiguration value = new DeploymentConfiguration();
		value.setName(name.getText().trim());
		value.setServiceType(serviceType.getValue());
		credentialsEditView.updateValue(value);

		return value;
	}

	@UiHandler("saveButton")
	protected void onSave(ClickEvent ce) {
		presenter.save();
	}

	public void setCredentialsValid(boolean valid) {
		credentialsEditView.setCredentialsValid(valid);
		setSaveEnabled(valid);
	}

	public void addValidatePasswordClickHandler(ClickHandler handler) {
		credentialsEditView.validatePasswordButton.addClickHandler(handler);
	}

	/**
	 * 
	 */
	public void clear() {
		name.setText("");
		credentialsEditView.clear();
		setSaveEnabled(false);
		serviceTypeSelected(serviceType.getValue());
	}

	private void setSaveEnabled(boolean enabled) {
		saveButton.setEnabled(enabled);
		if (enabled) {
			saveButton.removeStyleName("disabled");
		} else {
			saveButton.addStyleName("disabled");
		}
	}

	protected void serviceTypeSelected(DeploymentServiceType type) {
		UIObject.setVisible(credentialsDiv, type.isSupportsCredentials());
		setSaveEnabled(!type.isSupportsCredentials());
	}

	/**
	 * @param serviceTypes
	 */
	public void setServiceTypes(List<DeploymentServiceType> serviceTypes) {
		serviceType.setValue(serviceTypes.get(0));
		serviceType.setAcceptableValues(serviceTypes);
		UIObject.setVisible(serviceTypeDiv, serviceTypes.size() > 1);
	}

	/**
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
