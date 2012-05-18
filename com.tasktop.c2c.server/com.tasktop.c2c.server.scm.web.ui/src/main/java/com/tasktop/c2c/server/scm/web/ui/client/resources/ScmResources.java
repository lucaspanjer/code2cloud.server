package com.tasktop.c2c.server.scm.web.ui.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface ScmResources extends ClientBundle {

	@Source("css/scm.css")
	ScmCssResources style();

	public static ScmResources get = GWT.create(ScmResources.class);

}