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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.web.client.view.AbstractComposite;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceType;
import com.tasktop.c2c.server.deployment.domain.DeploymentStatus;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentConfigOptionsResult;

public class DeploymentsView extends AbstractComposite implements IDeploymentsView {

	interface DeploymentsViewUiBinder extends UiBinder<Widget, DeploymentsView> {
	}

	private static DeploymentsViewUiBinder uiBinder = GWT.create(DeploymentsViewUiBinder.class);

	@UiField
	public FlowPanel deploymentsListPanel;

	@UiField
	public DeploymentReadOnlyView deploymentReadOnlyView;
	@UiField
	public DeploymentEditView deploymentEditView;
	@UiField(provided = true)
	public INewDeploymentView newDeploymentView = GWT.create(INewDeploymentView.class);
	@UiField
	public Widget newButton;
	@UiField
	public Panel noDeploymentsMessagePanel;
	@UiField
	public Label errorLabel;
	@UiField
	public Panel deploymentErrorPanel;

	public static DeploymentResources resources = GWT.create(DeploymentResources.class);

	private List<DeploymentConfiguration> deployments;

	private Presenter presenter;

	public DeploymentsView() {
		bindUI();
		deploymentEditView.setVisible(false);
		deploymentReadOnlyView.setVisible(false);
		newDeploymentView.asWidget().setVisible(false);
		deploymentErrorPanel.setVisible(false);

		deploymentReadOnlyView.editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				deploymentReadOnlyView.setVisible(false);
				deploymentEditView.setValue(deploymentReadOnlyView.getOriginalValue());
				deploymentEditView.setVisible(true);
			}
		});

		deploymentEditView.addCancelClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				deploymentReadOnlyView.setVisible(true);
				deploymentEditView.setVisible(false);
			}
		});
	}

	protected void bindUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setDeploymentConfigurations(List<DeploymentConfiguration> result) {
		this.deployments = result;
		noDeploymentsMessagePanel.setVisible(deployments.size() == 0);
		deploymentsListPanel.clear();

		for (final DeploymentConfiguration config : result) {
			addConfig(config, false, -1);
		}
	}

	private void addConfig(final DeploymentConfiguration config, boolean selected, int index) {
		final DeploymentRowView row = new DeploymentRowView(config);
		row.anchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setSelectedConfig(config);
			}
		});
		if (selected) {
			row.setSelected(true);
		}
		if (index == -1) {
			deploymentsListPanel.add(row);
		} else {
			deploymentsListPanel.insert(row, index);
		}
	}

	public void setSelectedConfig(DeploymentConfiguration selectedObject) {
		// First, clear out any messages in the toaster.
		ProfileGinjector.get.instance().getNotifier().clearMessages();

		updateSelection(selectedObject);
		deploymentEditView.setVisible(false);
		newDeploymentView.asWidget().setVisible(false);
		deploymentReadOnlyView.setVisible(true);
		deploymentReadOnlyView.setValue(selectedObject);
		updateError(selectedObject);
	}

	private void updateSelection(DeploymentConfiguration selected) {
		for (Widget w : deploymentsListPanel) {
			DeploymentRowView row = (DeploymentRowView) w;
			row.setSelected(row.getDeployment().equals(selected));
		}
	}

	@UiHandler("newButton")
	public void newDeployment(ClickEvent ce) {
		presenter.newDeployment();
		updateSelection(null);
		newDeploymentView.clear();
		updateError(null);
		noDeploymentsMessagePanel.setVisible(false);
		deploymentReadOnlyView.setVisible(false);
		deploymentEditView.setVisible(false);
		newDeploymentView.asWidget().setVisible(true);
	}

	/**
	 * @param result
	 */
	public void newDeploymentCreated(DeploymentConfiguration result) {
		addConfig(result, true, -1);
		setSelectedConfig(result);
		updateError(result);
	}

	/**
	 * @param toDelete
	 */
	public void deleted(DeploymentConfiguration toDelete) {
		removeDeployment(toDelete);
		deploymentReadOnlyView.setVisible(false);
		deploymentEditView.setVisible(false);
	}

	/**
	 * @param deployment
	 */
	private int removeDeployment(DeploymentConfiguration deployment) {
		int i = 0;
		for (Widget w : deploymentsListPanel) {
			DeploymentRowView row = (DeploymentRowView) w;
			if (row.getDeployment().equals(deployment)) {
				row.removeFromParent();
				return i;
			}
			i++;
		}
		return -1;
	}

	public void updated(DeploymentConfiguration updated) {
		int index = removeDeployment(updated);
		addConfig(updated, true, index);
		deploymentReadOnlyView.setValue(updated);
		deploymentReadOnlyView.setVisible(true);
		deploymentEditView.setVisible(false);
		updateError(updated);
	}

	/**
	 * @param result
	 */
	public void updateStatus(DeploymentConfiguration config, DeploymentStatus result) {
		config.setStatus(result);
		int index = removeDeployment(config);
		addConfig(config, true, index);
		deploymentReadOnlyView.updateStatus(result);
	}

	private void updateError(DeploymentConfiguration deployment) {
		if (deployment != null && deployment.hasError()) {
			errorLabel.setText(deployment.getErrorString());
			deploymentErrorPanel.setVisible(true);
		} else {
			deploymentErrorPanel.setVisible(false);
		}
	}

	/**
	 * @param b
	 */
	public void setEnableEdit(boolean editEnabled) {
		newButton.setVisible(editEnabled);
		deploymentReadOnlyView.setEnableEdit(editEnabled);

	}

	public void setServiceTypes(List<DeploymentServiceType> serviceTypes) {
		newDeploymentView.setServiceTypes(serviceTypes);

	}

	/**
	 * @param deploymentsPresenter
	 */
	public void setPresenter(Presenter deploymentsPresenter) {
		this.presenter = deploymentsPresenter;
		deploymentReadOnlyView.setPresenter(presenter);
		deploymentEditView.setPresenter(presenter);
		newDeploymentView.setPresenter(presenter);
	}

	public void setConfigOptions(DeploymentConfigOptionsResult result) {
		deploymentEditView.setMemoryValues(result.getAvailableMemories());
		deploymentEditView.setServices(result.getAvailableServices());
		deploymentEditView.setServiceConfigurations(result.getAvailableServiceConfigurations());

	}

	@Override
	public void setBuilds(String jobName, List<BuildDetails> builds) {
		deploymentEditView.artifactEditView.setBuilds(jobName, builds);
	}

	@Override
	public DeploymentConfiguration getNewValue() {
		return newDeploymentView.getValue();
	}

	@Override
	public void setCredentialsValid(Boolean valid) {
		deploymentEditView.credentialsEditView.setCredentialsValid(valid);
		newDeploymentView.setCredentialsValid(valid);

	}

	@Override
	public DeploymentConfiguration getEditValue() {
		return deploymentEditView.getValue();
	}

	@Override
	public DeploymentConfiguration getOriginalValue() {
		return deploymentReadOnlyView.getOriginalValue();
	}

	@Override
	public void setJobNames(List<String> buildJobNames) {
		deploymentEditView.artifactEditView.setJobNames(buildJobNames);
	}

	@Override
	public DeploymentConfiguration getValue() {
		if (newDeploymentView.asWidget().isVisible()) {
			return newDeploymentView.getValue();
		} else if (deploymentEditView.isVisible()) {
			return deploymentEditView.getValue();
		}
		return null;
	}
}
