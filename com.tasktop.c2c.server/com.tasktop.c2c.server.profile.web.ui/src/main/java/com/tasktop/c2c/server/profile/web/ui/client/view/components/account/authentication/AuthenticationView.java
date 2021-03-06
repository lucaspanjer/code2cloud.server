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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.account.authentication;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.tasktop.c2c.server.common.profile.web.client.ClientCallback;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.DelegateCell;
import com.tasktop.c2c.server.common.web.client.view.CellTableResources;
import com.tasktop.c2c.server.profile.domain.project.SshPublicKey;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.account.presenter.IAccountView;

public class AuthenticationView extends Composite implements IAccountView<IAccountView.AccountAuthenticationPresenter> {

	interface AuthenticationViewUiBinder extends UiBinder<HTMLPanel, AuthenticationView> {
	}

	private static AuthenticationView instance;

	public static AuthenticationView getInstance() {
		if (instance == null) {
			instance = new AuthenticationView();
		}
		return instance;
	}

	private static AuthenticationViewUiBinder ourUiBinder = GWT.create(AuthenticationViewUiBinder.class);

	public interface SshKeyTemplate extends SafeHtmlTemplates {
		@Template("<div class=\"left lock misc-icon my-ssh-key\"><span></span><a style=\"cursor:pointer\">{0}</a></div>")
		SafeHtml sshKeyName(String name);
	}

	private static final SshKeyTemplate SSH_KEY_TEMPLATE = GWT.create(SshKeyTemplate.class);

	public interface RemoveLinkTemplate extends SafeHtmlTemplates {
		@Template("<div class=\"left\"><a style=\"cursor:pointer\" class=\" red-link\">{0}</a></div>")
		SafeHtml removeLink(String removeText);
	}

	private static final RemoveLinkTemplate REMOVE_LINK_TEMPLATE = GWT.create(RemoveLinkTemplate.class);

	@UiField
	DivElement changePasswordDiv;
	@UiField
	PasswordTextBox oldPasswordField;
	@UiField
	PasswordTextBox newPasswordField;
	@UiField
	PasswordTextBox confirmNewPasswordField;
	@UiField
	Button cancelChangePasswordButton;
	@UiField
	Button saveChangePasswordButton;
	@UiField
	DivElement linkGitHubDiv;
	@UiField
	Anchor linkGitHubButton;
	@UiField
	Anchor addSshKeyButton;
	@UiField
	Anchor changePasswordAnchor;
	@UiField
	HTMLPanel changePasswordPanel;
	@UiField(provided = true)
	public FormPanel githubForm;
	@UiField
	HTML associateGitHubAccountLabel;

	@UiField(provided = true)
	CellTable<SshPublicKey> sshKeyTable;

	private AccountAuthenticationPresenter presenter;
	private CommonProfileMessages commonProfileMessages = AppGinjector.get.instance().getCommonProfileMessages();
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public AuthenticationView() {
		createSshKeyTable();
		// Give our Github form a target of "_self" - that will ensure that it replaces the current page when the
		// redirect to GitHub happens (which is what we want).
		githubForm = new FormPanel(new NamedFrame("_self"));
		initWidget(ourUiBinder.createAndBindUi(this));
		if (!AppGinjector.get.instance().getConfiguration().isEnablePasswordManagment()) {
			UIObject.setVisible(changePasswordDiv, false);
		}
		if (!AppGinjector.get.instance().getConfiguration().isEnableGitHubAuth()) {
			UIObject.setVisible(linkGitHubDiv, false);
		}
		associateGitHubAccountLabel.setHTML(profileMessages.gitHubAssociateAccount(commonProfileMessages.code2Cloud()));
	}

	@Override
	public void setPresenter(AccountAuthenticationPresenter presenter) {
		this.presenter = presenter;
		if (presenter.getProfile().getGithubUsername() != null
				&& !presenter.getProfile().getGithubUsername().trim().isEmpty()) {
			// There's a GitHub username, change the form to allow for delete of the link.
			((Panel) this.githubForm.getWidget()).add(new Hidden("_method", "DELETE"));
			linkGitHubButton.setText(profileMessages.removeGitHubLinkFor(presenter.getProfile().getGithubUsername()));
		}
		sshKeyTable.setRowData(presenter.getSshKeys());
		resetPasswords();
		EditSshKeyDialog.getInstance().hide();
	}

	private void resetPasswords() {
		oldPasswordField.setText(null);
		newPasswordField.setText(null);
		confirmNewPasswordField.setText(null);
	}

	private void createSshKeyTable() {
		sshKeyTable = new CellTable<SshPublicKey>(10, CellTableResources.get.resources);
		sshKeyTable.setTableLayoutFixed(true);
		DelegateCell<String> nameCell = new DelegateCell<String>(new DelegateCell.RenderDelegate<String>() {
			@Override
			public SafeHtml render(Cell.Context context, String value, SafeHtmlBuilder sb) {
				return SSH_KEY_TEMPLATE.sshKeyName(presenter.getSshKeys().get(context.getIndex()).getName());
			}
		}, new DelegateCell.ActionDelegate<String>() {
			@Override
			public void execute(Cell.Context context) {
				SshPublicKey toEdit = presenter.getSshKeys().get(context.getIndex());
				presenter.selectSshKey(toEdit);
				EditSshKeyDialog.getInstance().setPresenter(presenter);
			}
		});
		Column<SshPublicKey, String> nameColumn = new Column<SshPublicKey, String>(nameCell) {
			@Override
			public String getValue(SshPublicKey object) {
				return object.getName();
			}
		};
		sshKeyTable.addColumn(nameColumn);
		sshKeyTable.setColumnWidth(nameColumn, 200, Style.Unit.PX);
		DelegateCell<String> removeKeyCell = new DelegateCell<String>(new DelegateCell.RenderDelegate<String>() {
			public SafeHtml render(Cell.Context context, String value, SafeHtmlBuilder sb) {
				return REMOVE_LINK_TEMPLATE.removeLink(commonProfileMessages.remove());
			}
		}, new DelegateCell.ActionDelegate<String>() {
			@Override
			public void execute(Cell.Context context) {
				SshPublicKey toDelete = presenter.getSshKeys().get(context.getIndex());
				presenter.selectSshKey(toDelete);
				DeleteSshKeyDialog.getInstance().setPresenter(presenter);
			}
		});
		sshKeyTable.addColumn(new Column<SshPublicKey, String>(removeKeyCell) {
			@Override
			public String getValue(SshPublicKey object) {
				return null;
			}
		});
	}

	@UiHandler("addSshKeyButton")
	void addSshKey(ClickEvent event) {
		EditSshKeyDialog.getInstance();
		SshPublicKey sshPublicKey = new SshPublicKey();
		presenter.selectSshKey(sshPublicKey);
		EditSshKeyDialog.getInstance().setPresenter(presenter);
	}

	@UiHandler("linkGitHubButton")
	void onLinkGitHub(ClickEvent event) {
		githubForm.submit();
	}

	@UiHandler("changePasswordAnchor")
	void onChangePassword(ClickEvent event) {
		changePasswordPanel.setVisible(true);
	}

	@UiHandler("cancelChangePasswordButton")
	void onCancelChangePassword(ClickEvent event) {
		changePasswordPanel.setVisible(false);
		resetPasswords();
	}

	@UiHandler("saveChangePasswordButton")
	void onSaveChangedPassword(ClickEvent event) {
		presenter.saveChangedPassword(oldPasswordField.getText(), newPasswordField.getText(),
				confirmNewPasswordField.getText(), new ClientCallback<Boolean>() {
					@Override
					public void onReturn(Boolean success) {
						changePasswordPanel.setVisible(!success);
					}
				});
	}
}
