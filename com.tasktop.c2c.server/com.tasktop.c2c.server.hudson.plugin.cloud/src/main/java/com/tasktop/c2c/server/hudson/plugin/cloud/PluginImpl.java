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
package com.tasktop.c2c.server.hudson.plugin.cloud;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Node;

import java.io.IOException;

@Extension
public class PluginImpl extends Plugin implements Describable<PluginImpl> {

	@Override
	public void start() throws Exception {
		load();
		removeSlaves();
	}

	@Override
	public void stop() throws Exception {
		removeSlaves();
		super.stop();
	}

	private void removeSlaves() throws IOException {
		for (Node slave : Hudson.getInstance().getNodes()) {
			if (slave instanceof C2CSlave) {
				C2CSlave c2cSlave = (C2CSlave) slave;
				System.out.println("Terminating slave: " + slave.toString());
				try {
					c2cSlave.terminate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) Hudson.getInstance().getDescriptorOrDie(getClass());
	}

	public static PluginImpl get() {
		return Hudson.getInstance().getPlugin(PluginImpl.class);
	}

	@Extension
	public static final class DescriptorImpl extends Descriptor<PluginImpl> {
		@Override
		public String getDisplayName() {
			return "Code2Cloud Dynamic Build Pool Plugin";
		}
	}
}
