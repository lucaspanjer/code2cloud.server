package com.tasktop.c2c.server.profile.service.provider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jgit.JGitText;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.URIish;

public class ProxyPreAuthTransportHttp extends TransportHttp {

	public static final TransportProtocol PROTO_PRE_AUTH_HTTP = new TransportProtocol() {
		private final String[] schemeNames = { "http", "https" }; //$NON-NLS-1$ //$NON-NLS-2$

		private final Set<String> schemeSet = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays
				.asList(schemeNames)));

		public String getName() {
			return JGitText.get().transportProtoHTTP;
		}

		public Set<String> getSchemes() {
			return schemeSet;
		}

		public Set<URIishField> getRequiredFields() {
			return Collections.unmodifiableSet(EnumSet.of(URIishField.HOST, URIishField.PATH));
		}

		public Set<URIishField> getOptionalFields() {
			return Collections.unmodifiableSet(EnumSet.of(URIishField.USER, URIishField.PASS, URIishField.PORT));
		}

		public int getDefaultPort() {
			return 80;
		}

		public Transport open(URIish uri, Repository local, String remoteName) throws NotSupportedException {
			return new ProxyPreAuthTransportHttp(local, uri);
		}
	};

	ProxyPreAuthTransportHttp(Repository local, URIish uri) throws NotSupportedException {
		super(local, uri);
	}

	@Override
	protected HttpURLConnection httpOpen(String method, URL u) throws IOException {
		HttpURLConnection connection = super.httpOpen(method, u);
		InternalJgitProvider.getInstance().addHeaders(connection);
		return connection;
	}

}
