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
package com.tasktop.c2c.server.common.profile.web.client.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.BatchAction;
import net.customware.gwt.dispatch.shared.BatchAction.OnException;
import net.customware.gwt.dispatch.shared.BatchResult;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.shared.UserInfo;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetUserInfoAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetUserInfoResult;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;
import com.tasktop.c2c.server.common.web.client.view.CommonGinjector;
import com.tasktop.c2c.server.common.web.shared.KnowsErrorMessageAction;

/**
 * Base classes for places that first make RPCs to authorize, then make the rest of the RPCs.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractBatchFetchingPlace extends AbstractPlace {

	protected boolean requiresUserInfo = true;
	protected boolean readyToGo = false;
	private List<Action<?>> actions;
	private BatchResult results;
	private List<Integer> resultIndexToIgnoreExceptions = new ArrayList<Integer>();

	protected void setUserInfo(UserInfo ui) {
		ProfileGinjector.get.instance().getAppState().setCredentials(ui.getCredentials());
		ProfileGinjector.get.instance().getAppState().setHasPendingAgreements(ui.getHasPendingAgreements());
	}

	public final void go() {
		if (!readyToGo) {
			fetchPlaceData();
			return;
		}

		ProfileGinjector.get.instance().getPlaceController().goTo(this);
		ProfileGinjector.get.instance().getScheduler().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				if (displayOnArrival != null) {
					notifier.displayMessage(displayOnArrival);
				}
			}
		});

	}

	public boolean isReadyToGo() {
		return readyToGo;
	}

	@Override
	public void reset() {
		readyToGo = false;
	}

	/** Subclasses should call this from the handlBatchResults method after they are ready to go. */
	protected void onPlaceDataFetched() {
		readyToGo = true;
		go();
	}

	@SuppressWarnings("unchecked")
	protected <T extends Result> T getResult(Class<T> resultClass) {
		for (Result r : results) {
			if (r != null && r.getClass().getName().equals(resultClass.getName())) {
				return (T) r;
			}
		}
		return null;
	}

	/** Override to handle the batch results. Don't forget to call super. */
	protected void handleBatchResults() {

	}

	private void onResultsRecieved() {

		if (requiresUserInfo) {
			setUserInfo(getResult(GetUserInfoResult.class).get());
		}

		if (isNotAuthorized()) {
			onNotAuthorised();
			return;
		}

		if (!AuthenticationHelper.isAnonymous()
				&& !(this instanceof AgreementsPlace)
				&& (ProfileGinjector.get.instance().getAppState().hasPendingAgreements() == null || ProfileGinjector.get
						.instance().getAppState().hasPendingAgreements())) {
			AgreementsPlace.createPlace(this).go();
			return;
		}

		int i = 0;
		for (Throwable t : results.getExceptions()) {
			if (resultIndexToIgnoreExceptions.contains(i)) {
				continue;
			}
			DispatchException dispatchException = (DispatchException) t;
			if (dispatchException != null) {
				boolean shouldContinue = handleExceptionInResults(actions.get(i), dispatchException);

				if (!shouldContinue) {
					return;
				}
			}
			i++;
		}

		handleBatchResults();
	}

	/**
	 * Called after we have fetched the results. The userInfo will be populated. Default impl just looks for auth
	 * exceptions in the results or a disabled account
	 * 
	 * @return
	 */
	protected boolean isNotAuthorized() {
		return getException("InsufficientPermissionsException") != null
				|| getException("AccessDeniedException") != null
				|| (requiresUserInfo && AuthenticationHelper.isAccountDisabled());
	}

	/**
	 * Called if there is an exception in the results.
	 * 
	 * @param dispatchException
	 * @param action
	 * 
	 * @return true to continue with place. false if we should abort
	 */
	protected boolean handleExceptionInResults(Action<?> action, DispatchException dispatchException) {
		List<String> errorMessages = null;

		if (action instanceof KnowsErrorMessageAction) {
			String aMessage = ((KnowsErrorMessageAction) action).getErrorMessage(dispatchException);
			if (aMessage != null) {
				errorMessages = Collections.singletonList(aMessage);
			}
		}

		if (errorMessages == null && AsyncCallbackSupport.getErrorHandler() != null) {
			errorMessages = AsyncCallbackSupport.getErrorHandler().getErrorMessages(dispatchException);
		}

		if (errorMessages == null) {
			errorMessages = Collections.singletonList(super.commonProfileMessages.serverSideErrorOccured());
		}
		notifier.displayMessage(Message.createErrorMessage(StringUtils.concatenate(errorMessages)));
		return false;
	}

	protected final boolean handleExceptionInResults() {
		return false;
	}

	/** Get an exception with the given name. */
	protected final Throwable getException(String exceptionClassName) {
		int i = 0;
		for (Throwable t : results.getExceptions()) {
			if (resultIndexToIgnoreExceptions.contains(i++)) {
				continue;
			}
			DispatchException dispatchException = (DispatchException) t;
			if (dispatchException != null) {
				if (exceptionClassName == null
						|| (dispatchException.getCauseClassname() != null && dispatchException.getCauseClassname()
								.contains(exceptionClassName))) {
					return dispatchException;
				}
			}
		}
		return null;
	}

	private BatchAction createFetchAction() {
		actions = new ArrayList<Action<?>>();
		if (requiresUserInfo) {
			actions.add(new GetUserInfoAction());
		}
		addActions();
		return new BatchAction(OnException.CONTINUE, actions.toArray(new Action<?>[actions.size()]));
	}

	/** Override to add more actions. Don't forget to call super. */
	protected void addActions() {

	}

	protected final void addAction(Action<?> action) {
		actions.add(action);
	}

	protected final void addActionAndIgnoreFailure(Action<?> action) {
		resultIndexToIgnoreExceptions.add(actions.size());
		actions.add(action);
	}

	protected final void fetchPlaceData() {

		BatchAction fetchAction = createFetchAction();

		AsyncCallback<BatchResult> fetchCallback = new AsyncCallbackSupport<BatchResult>(
				AsyncCallbackSupport.LOADING_MESSSAGE) {

			@Override
			protected void success(BatchResult result) {
				results = result;
				onResultsRecieved();
			}

		};
		CommonGinjector.get.instance().getDispatchService().execute(fetchAction, fetchCallback);
	}

}
