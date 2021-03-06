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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.util.enums.BuildResultMessageSelector;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentType;
import com.tasktop.c2c.server.profile.domain.build.BuildArtifact;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.deployment.IDeploymentsView.Presenter;

public class ArtifactEditView extends Composite {
	interface Binder extends UiBinder<Widget, ArtifactEditView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	public static interface JobNumberChangedHandler {
		void jobNumberChanged(String jobName, String jobNumber);
	}

	public interface IsDirtyHandler {
		void isDirty(boolean isAutoDeploy);
	}

	private static final class StringRenderer extends AbstractRenderer<String> {

		private final String nullText;

		public StringRenderer(String nullText) {
			this.nullText = nullText;
		}

		@Override
		public String render(String object) {
			if (object == null) {
				return nullText;
			}
			return object;
		}

	}

	private static final class BuildRenderer extends AbstractRenderer<BuildDetails> {

		private final String nullText;
		private CommonProfileMessages commonProfileMessages = AppGinjector.get.instance().getCommonProfileMessages();

		public BuildRenderer(String nullText) {
			this.nullText = nullText;
		}

		@Override
		public String render(BuildDetails object) {
			if (object == null) {
				return nullText;
			}
			if (object.getResult() == null) {
				return object.getNumber() + "";
			}
			String buildResult = new BuildResultMessageSelector().getInternationalizedMessage(object.getResult(),
					commonProfileMessages);
			return object.getNumber() + " " + commonProfileMessages.parentheses(buildResult);
		}

	}

	private static final class BuildKeyProvider implements ProvidesKey<BuildDetails> {

		@Override
		public Object getKey(BuildDetails item) {
			if (item == null) {
				return null;
			}
			return item.getNumber();
		}

	}

	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	@UiField
	RadioButton automaticType;
	@UiField
	RadioButton manualType;
	@UiField
	CheckBox deployUnstableBuilds;
	@UiField(provided = true)
	ValueListBox<String> jobNameListBox = new ValueListBox<String>(new StringRenderer(profileMessages.selectAJob()));
	@UiField(provided = true)
	ValueListBox<BuildDetails> buildsListBox = new ValueListBox<BuildDetails>(new BuildRenderer(
			profileMessages.selectABuild()), new BuildKeyProvider());
	@UiField
	Label jobNumberLabel;
	@UiField(provided = true)
	ValueListBox<String> artifactListBox = new ValueListBox<String>(new StringRenderer(
			profileMessages.selectAnArtifact()));
	@UiField
	TextBox artifactTextBox;
	@UiField
	Label artifactLabel;
	@UiField
	Label artifactPathLabel;

	@UiField
	Label infoMessage;

	private Presenter presenter;

	public ArtifactEditView() {
		initWidget(uiBinder.createAndBindUi(this));
		automaticType.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateFieldsAfterTypeChange();
			}
		});
		manualType.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateFieldsAfterTypeChange();
			}
		});
		artifactListBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				updateDeploymentInfoMessages();
				artifactTextBox.setText(artifactListBox.getValue());
			}
		});
		buildsListBox.addValueChangeHandler(new ValueChangeHandler<BuildDetails>() {

			@Override
			public void onValueChange(ValueChangeEvent<BuildDetails> event) {
				setArtifactOptions(event.getValue());
				updateDeploymentInfoMessages();
			}
		});
		jobNameListBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				presenter.jobNameChanged(jobNameListBox.getValue());
				artifactListBox.setValue(null);
				artifactListBox.setAcceptableValues(Collections.EMPTY_LIST);
				buildsListBox.setValue(null);
				buildsListBox.setAcceptableValues(Collections.EMPTY_LIST);
				updateDeploymentInfoMessages();
			}
		});
	}

	protected boolean isValidDeploymentArtifact(BuildArtifact a) {
		return a.getRelativePath().endsWith("war") || a.getRelativePath().endsWith("ear");
	}

	private void setArtifactOptions(BuildDetails details) {
		artifactListBox.setValue(null);
		List<String> artifactPaths = new ArrayList<String>(details.getArtifacts().size());
		for (BuildArtifact artifact : details.getArtifacts()) {
			if (isValidDeploymentArtifact(artifact)) {
				artifactPaths.add(artifact.getRelativePath());
			}
		}
		artifactListBox.setAcceptableValues(addNullFirst(artifactPaths));
	}

	private void updateFieldsAfterTypeChange() {
		boolean isManual = !automaticType.getValue();

		deployUnstableBuilds.setVisible(!isManual);
		buildsListBox.setVisible(isManual);
		jobNumberLabel.setVisible(isManual);
		artifactTextBox.setVisible(!isManual);
		artifactPathLabel.setVisible(!isManual);
		updateDeploymentInfoMessages();
	}

	private IsDirtyHandler isDirtyHandler = null;

	public void setIsDirtyHandler(IsDirtyHandler handler) {
		this.isDirtyHandler = handler;
	}

	private void onDirty(boolean isAutoDeploy) {
		if (isDirtyHandler != null) {
			isDirtyHandler.isDirty(automaticType.getValue());
		}
	}

	private void updateDeploymentInfoMessages() {
		if (automaticType.getValue()) {
			infoMessage.setText(profileMessages.deploymentAfterNextBuild());
			onDirty(true);
		} else if (manualType.getValue()) {
			infoMessage.setText(profileMessages.deploymentAfterSaving());
			onDirty(false);
		}

		infoMessage.setVisible(true);
	}

	public void setValue(DeploymentConfiguration originalValue) {
		if (originalValue.getDeploymentType() != null) {
			switch (originalValue.getDeploymentType()) {
			case AUTOMATED:
				automaticType.setValue(true);
				break;
			case MANUAL:
				manualType.setValue(true);
				break;
			}
		} else {
			manualType.setValue(false);
			automaticType.setValue(false);
		}
		updateFieldsAfterTypeChange();

		deployUnstableBuilds.setValue(originalValue.isDeployUnstableBuilds());
		jobNameListBox.setValue(originalValue.getBuildJobName());
		BuildDetails details;
		if (originalValue.getBuildJobNumber() != null) {
			details = new BuildDetails();
			details.setNumber(Integer.parseInt(originalValue.getBuildJobNumber()));
		} else {
			details = null;
		}

		buildsListBox.setValue(details);
		buildsListBox.setAcceptableValues(Collections.EMPTY_LIST); // New values will come in afterward
		artifactListBox.setValue(originalValue.getBuildArtifactPath());
		artifactListBox.setAcceptableValues(Collections.EMPTY_LIST); // New values will come in afterward
		presenter.jobNameChanged(originalValue.getBuildJobName()); // Trigger RPC to calculate new values.

		// If we have no URL right now, use a default.
		String origArtifactPath = originalValue.getBuildArtifactPath();
		if (origArtifactPath == null || origArtifactPath.trim().length() == 0) {
			artifactTextBox.setText("**/target/*.war");
		} else {
			artifactTextBox.setValue(origArtifactPath);
		}

		infoMessage.setVisible(false);
	}

	/**
	 * @param value
	 */
	public void updateValue(DeploymentConfiguration value) {
		value.setBuildJobName(jobNameListBox.getValue());

		if (automaticType.getValue()) {
			value.setDeploymentType(DeploymentType.AUTOMATED);
			value.setBuildArtifactPath(artifactTextBox.getValue());
			value.setDeployUnstableBuilds(deployUnstableBuilds.getValue());
		} else if (manualType.getValue()) {
			value.setDeploymentType(DeploymentType.MANUAL);
			value.setBuildJobNumber(buildsListBox.getValue().getNumber() + "");
			value.setBuildArtifactPath(artifactListBox.getValue());
		}

	}

	/**
	 * @param jobNames
	 */
	public void setJobNames(List<String> jobNames) {
		jobNameListBox.setAcceptableValues(addNullFirst(jobNames));
	}

	/**
	 * @param jobName
	 * @param builds
	 */
	public void setBuilds(String jobName, List<BuildDetails> builds) {
		if (builds != null) {
			buildsListBox.setAcceptableValues(addNullFirst(builds));

			if (!builds.isEmpty() && buildsListBox.getValue() == null) {
				BuildDetails latestBuild = builds.get(0);
				buildsListBox.setValue(latestBuild);
				setArtifactOptions(latestBuild);
			}
		}
	}

	private <T> Collection<T> addNullFirst(List<T> list) {
		List<T> result = new ArrayList<T>(list.size() + 1);
		result.add(null);
		result.addAll(list);
		return result;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
