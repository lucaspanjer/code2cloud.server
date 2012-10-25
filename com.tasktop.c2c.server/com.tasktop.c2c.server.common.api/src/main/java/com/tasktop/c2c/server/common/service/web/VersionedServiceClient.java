package com.tasktop.c2c.server.common.service.web;

public interface VersionedServiceClient {

	/**
	 * Returns the version set in the Service interface.
	 * 
	 * @return
	 */
	public String getClientVersion();

	/**
	 * This makes a call to the ServiceController to get its version.
	 * 
	 * @return
	 */
	public String getServiceVersion();
}
