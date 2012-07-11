/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.cloud.domain;

import java.util.List;

public final class PoolStatus {
	private List<ServiceType> supportedServices;
	private int totalNodes;
	private int freeNodes;
	private int fullNodes;
	private List<ServiceHost> nodes;
	private int maxServicesPerHost;
	private double capacity;

	public int getTotalNodes() {
		return totalNodes;
	}

	public void setTotalNodes(int totalNodes) {
		this.totalNodes = totalNodes;
	}

	public int getFreeNodes() {
		return freeNodes;
	}

	public void setFreeNodes(int freeNodes) {
		this.freeNodes = freeNodes;
	}

	public int getFullNodes() {
		return fullNodes;
	}

	public void setFullNodes(int fullNodes) {
		this.fullNodes = fullNodes;
	}

	public List<ServiceType> getSupportedServices() {
		return supportedServices;
	}

	public void setSupportedServices(List<ServiceType> supportedServices) {
		this.supportedServices = supportedServices;
	}

	/**
	 * @return the nodes
	 */
	public List<ServiceHost> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public void setNodes(List<ServiceHost> nodes) {
		this.nodes = nodes;
	}

	public int getMaxServicesPerHost() {
		return maxServicesPerHost;
	}

	public void setMaxServicesPerHost(int maxCapacity) {
		this.maxServicesPerHost = maxCapacity;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

}