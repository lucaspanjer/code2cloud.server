package com.tasktop.c2c.server.scm.web.ui.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ScmResources extends ClientBundle {

	@Source("css/scm.css")
	ScmCssResources style();

	@Source("images/add.gif")
	ImageResource addIcon();

	@Source("images/delete.gif")
	ImageResource deleteIcon();

	@Source("images/changelog_obj.gif")
	ImageResource changeIcon();

	public static ScmResources get = GWT.create(ScmResources.class);

}