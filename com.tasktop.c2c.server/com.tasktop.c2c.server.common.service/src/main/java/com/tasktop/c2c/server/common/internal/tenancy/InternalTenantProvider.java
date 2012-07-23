package com.tasktop.c2c.server.common.internal.tenancy;

import org.springframework.tenancy.core.Tenant;
import org.springframework.tenancy.provider.DefaultTenant;
import org.springframework.tenancy.provider.TenantProvider;

public class InternalTenantProvider implements TenantProvider {

	@Override
	public Tenant findTenant(Object id) {
		if (id instanceof Tenant) {
			return (Tenant) id;
		}
		return new DefaultTenant(id, null);
	}

}
