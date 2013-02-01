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
package com.tasktop.c2c.server.profile.web.ui.client.presenter.components;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.notification.OperationMessage;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceType;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.ProjectDeploymentPlace;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.ControlDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.ControlDeploymentAction.Action;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.CreateDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeleteDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentConfigOptionsResult;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentResult;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.DeploymentStatusResult;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetDeploymentConfigOptionsAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.GetProjectBuildsResult;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.UpdateDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.ValidateDeploymentAction;
import com.tasktop.c2c.server.profile.web.ui.client.shared.action.ValidateDeploymentResult;
import com.tasktop.c2c.server.profile.web.ui.client.view.deployment.DeploymentsView;
import com.tasktop.c2c.server.profile.web.ui.client.view.deployment.IDeploymentsView;

public class DeploymentsPresenter extends AbstractProfilePresenter implements SplittableActivity,
		IDeploymentsView.Presenter {

	private final IDeploymentsView view;
	private String projectIdentifier;
	private List<DeploymentConfiguration> deploymentConfigurations;
	private GetProjectBuildsResult buildInformation;
	private List<DeploymentServiceType> serviceTypes;
	private CommonProfileMessages commonProfileMessages = AppGinjector.get.instance().getCommonProfileMessages();
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public DeploymentsPresenter(IDeploymentsView view) {
		super(view);
		this.view = view;
	}

	public DeploymentsPresenter() {
		this(new DeploymentsView());
	}

	protected IDeploymentsView getView() {
		return view;
	}

	public void setPlace(Place place) {
		ProjectDeploymentPlace depPlace = (ProjectDeploymentPlace) place;
		this.projectIdentifier = depPlace.getProject().getIdentifier();
		this.deploymentConfigurations = depPlace.getDeploymentConfigurations();
		this.buildInformation = depPlace.getBuildInformation();
		this.serviceTypes = depPlace.getServiceTypes();
		view.setPresenter(this);

		if (AuthenticationHelper.hasRoleForProject(Role.Admin, projectIdentifier)
				|| AuthenticationHelper.hasRoleForProject(Role.User, projectIdentifier)) {
			view.setEnableEdit(true);
		} else {
			view.setEnableEdit(false);
		}

		view.setDeploymentConfigurations(deploymentConfigurations);
		view.setServiceTypes(serviceTypes);
		if (!deploymentConfigurations.isEmpty()) {
			view.setSelectedConfig(deploymentConfigurations.get(0));
		}
		view.setJobNames(buildInformation.getBuildJobNames());
	}

	@Override
	protected void bind() {

	}

	@Override
	public void doStart() {
		doOperation(commonProfileMessages.starting(), commonProfileMessages.started(), Action.START);
	}

	private void doOperation(String opMessage, String successMessage, Action type) {
		OperationMessage message = new OperationMessage(opMessage);
		message.setSuccessText(successMessage);
		final DeploymentConfiguration configuration = view.getOriginalValue();
		getDispatchService().execute(new ControlDeploymentAction(projectIdentifier, configuration, type),
				new AsyncCallbackSupport<DeploymentStatusResult>(message) {

					@Override
					protected void success(DeploymentStatusResult result) {
						view.updateStatus(configuration, result.get());

					}
				});
	}

	@Override
	public void doStop() {
		doOperation(commonProfileMessages.stopping(), commonProfileMessages.stopped(), Action.STOP);
	}

	@Override
	public void doRestart() {
		doOperation(commonProfileMessages.restarting(), commonProfileMessages.restarted(), Action.RESTART);
	}

	@Override
	public void save() {
		OperationMessage message = new OperationMessage(commonProfileMessages.saving());
		getDispatchService().execute(new CreateDeploymentAction(projectIdentifier, view.getNewValue()),

		new AsyncCallbackSupport<DeploymentResult>(message) {

			@Override
			protected void success(DeploymentResult result) {
				if (result.get().hasError()) {
					ProfileGinjector.get.instance().getNotifier()
							.displayMessage(Message.createErrorMessage(profileMessages.errorWhileSaving()));
				} else {
					ProfileGinjector.get.instance().getNotifier()
							.displayMessage(Message.createSuccessMessage(commonProfileMessages.saved()));
				}
				view.newDeploymentCreated(result.get());
			}
		});
	}

	@Override
	public void update() {
		getDispatchService().execute(new UpdateDeploymentAction(projectIdentifier, view.getEditValue()),
				new AsyncCallbackSupport<DeploymentResult>(new OperationMessage(commonProfileMessages.saving())) {

					@Override
					protected void success(DeploymentResult result) {
						if (result.get().hasError()) {
							ProfileGinjector.get.instance().getNotifier()
									.displayMessage(Message.createErrorMessage(profileMessages.errorWhileSaving()));
						} else {
							ProfileGinjector.get.instance().getNotifier()
									.displayMessage(Message.createSuccessMessage(commonProfileMessages.saved()));
						}
						view.updated(result.get());
					}
				});
	}

	@Override
	public void delete(final DeploymentConfiguration config, boolean alsoDeleteFromCF) {
		OperationMessage message = new OperationMessage(commonProfileMessages.deleting());
		message.setSuccessText(commonProfileMessages.deleted());
		getDispatchService().execute(new DeleteDeploymentAction(projectIdentifier, config, alsoDeleteFromCF),
				new AsyncCallbackSupport<DeploymentResult>(message) {
					@Override
					protected void success(DeploymentResult result) {
						view.deleted(config);
					}
				});

	}

	@Override
	public void jobNameChanged(final String jobName) {
		getDispatchService().execute(new GetProjectBuildsAction(projectIdentifier, jobName),

		new AsyncCallbackSupport<GetProjectBuildsResult>() {

			@Override
			protected void success(GetProjectBuildsResult result) {
				view.setBuilds(jobName, result.getBuilds());
			}

		});
	}

	@Override
	public void validateCredentials() {
		getDispatchService().execute(new ValidateDeploymentAction(projectIdentifier, view.getValue()),
				new AsyncCallbackSupport<ValidateDeploymentResult>() {
					@Override
					protected void success(ValidateDeploymentResult result) {
						view.setCredentialsValid(result.get());
					}
				});
	}

	@Override
	public void editStarted(DeploymentConfiguration config) {
		getDispatchService().execute(new GetDeploymentConfigOptionsAction(projectIdentifier, config),
				new AsyncCallbackSupport<DeploymentConfigOptionsResult>() {

					@Override
					protected void success(DeploymentConfigOptionsResult result) {

						view.setConfigOptions(result);
					}
				});

	}

	@Override
	public void newDeployment() {

	}

}
