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
package com.tasktop.c2c.server.services.web.scm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.ChunkedInputStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.web.HttpRequestHandler;

import com.tasktop.c2c.server.common.service.Security;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.io.FlushingChunkedOutputStream;
import com.tasktop.c2c.server.common.service.io.MultiplexingOutputStream;
import com.tasktop.c2c.server.common.service.io.PacketType;

/**
 * A handler for Git requests initiated via SSH at the ALM hub.
 * 
 * @author David Green (Tasktop Technologies Inc.)
 */
public class GitHandler implements HttpRequestHandler {

	private enum GitCommand {
		RECEIVE_PACK("git-receive-pack", Role.User), UPLOAD_PACK("git-upload-pack", Role.Observer, Role.Community,
				Role.User);

		private final String commandName;
		private final String[] roles;

		private GitCommand(String commandName, String... roles) {
			this.commandName = commandName;
			this.roles = roles;
		}

		public static final GitCommand fromCommandName(String commandName) {
			for (GitCommand command : values()) {
				if (command.getCommandName().equals(commandName)) {
					return command;
				}
			}
			return null;
		}

		public String getCommandName() {
			return commandName;
		}

		public String[] getRoles() {
			return roles;
		}
	}

	private static final String MIME_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";

	private static final Pattern GIT_COMMAND_PATTERN = Pattern.compile("/(git-upload-pack|git-receive-pack)/(.*)");

	private static Boolean chunkedIOContainerSupported;

	private Logger log = LoggerFactory.getLogger(GitHandler.class.getName());

	private int bufferSize = 1024 * 16;
	private long timeoutInMillis = 1000L * 60L * 60L * 2L;

	private RepositoryResolver<HttpServletRequest> repositoryResolver;

	@SuppressWarnings("serial")
	private static class ErrorResponseException extends Exception {
		private ErrorResponseException(String message) {
			super(message);
		}
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		final boolean containerSupportsChunkedIO = computeContainerSupportsChunkedIO();

		String pathInfo = request.getPathInfo();
		log.info("Git request: " + request.getMethod() + " " + request.getRequestURI() + " " + pathInfo);

		Repository repository = null;
		try {
			// only work on Git requests
			Matcher matcher = pathInfo == null ? null : GIT_COMMAND_PATTERN.matcher(pathInfo);
			if (matcher == null || !matcher.matches()) {
				log.info("Unexpected path: " + pathInfo);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			String requestCommand = matcher.group(1);
			String requestPath = matcher.group(2);

			// sanity check on path, disallow path separator components
			if (requestPath == null || requestPath.contains("/") || requestPath.contains("..")) {
				badPathResponse();
			}

			repository = repositoryResolver.open(request, requestPath);

			InputStream requestInput = request.getInputStream();
			if (!containerSupportsChunkedIO) {
				requestInput = new ChunkedInputStream(requestInput);
			}

			MultiplexingOutputStream mox = createMultiplexingOutputStream(response, containerSupportsChunkedIO);
			// indicate that we're ok to handle the request
			// note that following this there will be a two-way communication with the process
			// that might still encounter errors. That's ok.
			startOkResponse(response, containerSupportsChunkedIO);

			// identify the git command
			GitCommand command = GitCommand.fromCommandName(requestCommand);
			if (command != null) {
				// permissions check
				if (!Security.hasOneOfRoles(command.getRoles())) {
					log.info("Access denied to " + Security.getCurrentUser() + " for " + command.getCommandName()
							+ " on " + TenancyContextHolder.getContext().getTenant() + " " + requestPath);
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				switch (command) {
				case RECEIVE_PACK:
					ReceivePack rp = new ReceivePack(repository);
					rp.receive(requestInput, mox.stream(PacketType.STDOUT), mox.stream(PacketType.STDERR));
					break;
				case UPLOAD_PACK:
					UploadPack up = new UploadPack(repository);
					up.upload(requestInput, mox.stream(PacketType.STDOUT), mox.stream(PacketType.STDERR));
					break;
				default:
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}

			// at this stage we're done with IO
			// send the exit value and closing chunk
			try {
				int exitValue = 0;

				if (exitValue != 0) {
					log.info("Exit value: " + exitValue);
				}
				mox.writeExitCode(exitValue);
				mox.close();
			} catch (IOException e) {
				// ignore
				log.debug("Cannot complete writing exit state", e);
			}

			// clear interrupt status
			Thread.interrupted();

		} catch (ErrorResponseException e) {
			createGitErrorResponse(response, containerSupportsChunkedIO, e.getMessage());
		} catch (ServiceNotAuthorizedException e) {
			createGitErrorResponse(response, containerSupportsChunkedIO, e.getMessage());
		} catch (ServiceNotEnabledException e) {
			createGitErrorResponse(response, containerSupportsChunkedIO, e.getMessage());
		} finally {
			log.info("Git request complete");
			if (repository != null) {
				repository.close();
			}
		}
	}

	private void badPathResponse() throws ErrorResponseException {
		throw new ErrorResponseException("Path does not appear to be a git repository");
	}

	private void createGitErrorResponse(HttpServletResponse response, boolean containerSupportsChunkedIO, String message)
			throws IOException {
		startOkResponse(response, containerSupportsChunkedIO);
		MultiplexingOutputStream mox = createMultiplexingOutputStream(response, containerSupportsChunkedIO);

		OutputStream errorStream = mox.stream(PacketType.STDERR);
		errorStream.write(message.getBytes());
		if (!message.endsWith("\n")) {
			errorStream.write('\n');
		}
		errorStream.flush();

		mox.writeExitCode(1);
		mox.close();
	}

	private MultiplexingOutputStream createMultiplexingOutputStream(HttpServletResponse response,
			final boolean containerSupportsChunkedIO) throws IOException {
		MultiplexingOutputStream mox;
		OutputStream outputStream = response.getOutputStream();

		if (!containerSupportsChunkedIO) {
			outputStream = new FlushingChunkedOutputStream(outputStream);
		}
		mox = new MultiplexingOutputStream(outputStream);
		return mox;
	}

	private void startOkResponse(HttpServletResponse response, final boolean containerSupportsChunkedIO) {
		response.setStatus(HttpServletResponse.SC_OK);
		addNoCacheHeaders(response);
		response.setHeader("Content-Type", MIME_TYPE_APPLICATION_OCTET_STREAM);
		if (!containerSupportsChunkedIO) {
			response.setHeader("Transfer-Encoding", "chunked");
		}
	}

	private static boolean computeContainerSupportsChunkedIO() {
		synchronized (GitHandler.class) {
			if (chunkedIOContainerSupported == null) {
				boolean supported = true;
				for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
					if (stackTrace.getClassName().contains("winstone")) {
						supported = false;
						break;
					}
				}
				chunkedIOContainerSupported = supported;
			}
		}
		return chunkedIOContainerSupported;
	}

	private void addNoCacheHeaders(HttpServletResponse response) {
		// expire in the past
		response.addHeader("Expires", "Fri, 01 Jan 2000 00:00:00 GMT");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Cache-Control", "no-cache, max-age=0, must-revalidate");
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public long getTimeoutInMillis() {
		return timeoutInMillis;
	}

	public void setTimeoutInMillis(long timeoutInMillis) {
		this.timeoutInMillis = timeoutInMillis;
	}

	/**
	 * @param repositoryResolver
	 *            the repositoryResolver to set
	 */
	public void setRepositoryResolver(RepositoryResolver<HttpServletRequest> repositoryResolver) {
		this.repositoryResolver = repositoryResolver;
	}

}
