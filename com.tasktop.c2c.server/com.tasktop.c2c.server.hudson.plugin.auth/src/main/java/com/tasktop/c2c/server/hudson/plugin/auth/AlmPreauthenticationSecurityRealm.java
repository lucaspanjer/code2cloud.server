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
package com.tasktop.c2c.server.hudson.plugin.auth;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.security.GroupDetails;
import hudson.security.UserMayOrMayNotExistException;
import hudson.security.SecurityRealm;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;

import org.eclipse.hudson.security.team.Team;
import org.eclipse.hudson.security.team.TeamAwareSecurityRealm;
import org.eclipse.hudson.security.team.TeamManager;
import org.kohsuke.stapler.DataBoundConstructor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tasktop.c2c.server.common.service.web.TenancyUtil;

public class AlmPreauthenticationSecurityRealm extends TeamAwareSecurityRealm implements UserDetailsService,
		AuthenticationManager {

	@DataBoundConstructor
	public AlmPreauthenticationSecurityRealm() {
	}

	@Override
	public SecurityComponents createSecurityComponents() {
		AuthenticationManager manager = (AuthenticationManager) this;
		UserDetailsService userDetails = (UserDetailsService) this;
		return new SecurityComponents(manager, userDetails);
	}

	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		return auth;
	}

	@Override
	public boolean canLogOut() {
		return false;
	}

	@Override
	public boolean allowsSignup() {
		return false;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		// does not check the underlying user datastore.
		throw new UserMayOrMayNotExistException(username);
	}

	@Override
	public GroupDetails loadGroupByGroupname(final String groupname) throws UsernameNotFoundException,
			DataAccessException {
		return new GroupDetails() {

			@Override
			public String getName() {
				return groupname;
			}
		};
	}

	@Override
	public Filter createFilter(FilterConfig filterConfig) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath:applicationContext-security.xml" }, false) {
		};
		context.setClassLoader(AlmPreauthenticationSecurityRealm.class.getClassLoader());

		context.refresh();
		Filter securityFilter = (Filter) context.getBean("springSecurityFilterChain");
		return securityFilter;
	}

	@Override
	public Team GetCurrentUserTeam() {
		// Specify which project (and corresponding Hudson team) this pre authenticated user belongs to
		String dcsProjectName = TenancyUtil.getCurrentTenantProjectIdentifer();
		if (dcsProjectName == null) {
			return null;
		}
		TeamManager teamManager = Hudson.getInstance().getTeamManager();
		try {
			return teamManager.findTeam(dcsProjectName);
		} catch (TeamManager.TeamNotFoundException ex) {
			// The team does not exist. Just create and return it
			try {
				return teamManager.createTeam(dcsProjectName);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static final class DescriptorImpl extends Descriptor<SecurityRealm> {

		@Override
		public String getDisplayName() {
			return "Code2Cloud Authentication";
		}
	}

	@Extension
	public static DescriptorImpl install() {
		return new DescriptorImpl();
	}

}
