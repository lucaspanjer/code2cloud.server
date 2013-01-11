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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentStatus;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.deployment.DeploymentsView.Presenter;

public class DeploymentReadOnlyView extends Composite {
	interface Binder extends UiBinder<Widget, DeploymentReadOnlyView> {
	}

	public interface DeleteHandler {
		void delete(DeploymentConfiguration config, boolean alsoDeleteService);
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	Label title;
	@UiField
	Label status;
	@UiField
	protected Anchor startButton;
	@UiField
	protected Anchor stopButton;
	@UiField
	protected Anchor restartButton;
	@UiField
	Anchor deleteButton;
	@UiField
	Anchor editButton;

	@UiField
	CredentialsReadOnlyView credentialsReadOnlyView;
	@UiField
	SettingsReadOnlyView settingsReadOnlyView;
	@UiField
	ServicesReadOnlyView servicesReadOnlyView;
	@UiField
	ArtifactReadOnlyView artifactReadOnlyView;

	@UiField
	protected DivElement servicesDiv;
	@UiField
	protected DivElement infoDiv;
	@UiField
	protected FlowPanel mappedUrls;
	@UiField
	protected DivElement settingsDiv;
	@UiField
	protected DivElement credentialsDiv;

	private DeploymentConfiguration originalValue;

	private Presenter presenter;
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public DeploymentReadOnlyView() {
		initWidget(uiBinder.createAndBindUi(this));

		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final DeleteDeploymentDialog deleteDialog = DeleteDeploymentDialog.getInstance();

				deleteDialog.center();
				deleteDialog.show();
			}
		});

		DeleteDeploymentDialog.getInstance().deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.delete(getOriginalValue(), DeleteDeploymentDialog.getInstance().alsoDelete.getValue());
				DeleteDeploymentDialog.getInstance().hide();

			}
		});

	}

	public DeploymentConfiguration getOriginalValue() {
		return this.originalValue;
	}

	public void setValue(DeploymentConfiguration deployment) {

		this.originalValue = deployment;

		title.setText(deployment.getName());

		updateStatus(deployment.getStatus());

		if (deployment.getServiceType().isSupportsCredentials()) {
			credentialsReadOnlyView.setValue(deployment);
		}
		if (deployment.getServiceType().isSupportsSettings()) {
			settingsReadOnlyView.setValue(deployment);
		}
		if (deployment.getServiceType().isSupportsServices()) {
			servicesReadOnlyView.setValue(deployment);
		}
		artifactReadOnlyView.setValue(deployment);

		UIObject.setVisible(credentialsDiv, deployment.getServiceType().isSupportsCredentials());
		UIObject.setVisible(settingsDiv, deployment.getServiceType().isSupportsSettings());
		UIObject.setVisible(infoDiv, !deployment.getServiceType().isSupportsSettings());
		mappedUrls.clear();
		if (deployment.getMappedUrls() != null && !deployment.getMappedUrls().isEmpty()) {
			boolean needSep = false;
			for (String url : deployment.getMappedUrls()) {
				if (needSep) {
					mappedUrls.add(new Label(profileMessages.comma() + " "));
				} else {
					needSep = true;
				}
				mappedUrls.add(new Anchor(url, url, profileMessages.newLc()));
			}
		} else {
			mappedUrls.add(new Label(profileMessages.noneBracketed()));
		}
		UIObject.setVisible(servicesDiv, deployment.getServiceType().isSupportsServices());
		if (deployment.getServiceType().isAlwaysDeleteInService()) {
			UIObject.setVisible(DeleteDeploymentDialog.getInstance().deleteInServiceDiv, false);
			DeleteDeploymentDialog.getInstance().alsoDelete.setValue(true);
		} else {
			UIObject.setVisible(DeleteDeploymentDialog.getInstance().deleteInServiceDiv, true);
			DeleteDeploymentDialog.getInstance().alsoDelete.setValue(false);

		}
	}

	/**
	 * @param deployment
	 */
	public void updateStatus(DeploymentStatus deploymentStatus) {
		setEnabledWithStyle(startButton, true);
		setEnabledWithStyle(stopButton, true);
		setEnabledWithStyle(restartButton, true);

		if (deploymentStatus == null || deploymentStatus.getResult() == null) {
			status.setText(profileMessages.unknown());
		} else {
			switch (deploymentStatus.getResult()) {
			case STARTED:
				status.setText(profileMessages.started());
				setEnabledWithStyle(startButton, false);
				break;
			case STOPPED:
				status.setText(profileMessages.stopped());
				setEnabledWithStyle(stopButton, false);
				setEnabledWithStyle(restartButton, false);
				break;
			case UPDATING:
				status.setText(profileMessages.updating());
				break;
			}
		}
	}

	private void setEnabledWithStyle(Anchor button, boolean enabled) {
		if (enabled) {
			button.removeStyleName("disabled");
		} else {
			button.addStyleName("disabled");
		}
		button.setEnabled(enabled);
	}

	/**
	 * @param editEnabled
	 */
	public void setEnableEdit(boolean editEnabled) {
		editButton.setVisible(editEnabled);
		startButton.setVisible(editEnabled);
		stopButton.setVisible(editEnabled);
		restartButton.setVisible(editEnabled);
		deleteButton.setVisible(editEnabled);
	}

	/**
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("startButton")
	protected void onStartClicked(ClickEvent e) {
		if (startButton.isEnabled()) {
			presenter.doStart();
		}
	}

	@UiHandler("stopButton")
	protected void onStopClicked(ClickEvent e) {
		if (stopButton.isEnabled()) {
			presenter.doStop();
		}
	}

	@UiHandler("restartButton")
	protected void onRestartClicked(ClickEvent e) {
		if (restartButton.isEnabled()) {
			presenter.doRestart();
		}
	}

}
