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
package com.tasktop.c2c.server.common.service.domain.criteria;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * This is used to store a relative date (such as "2 days ago" or "30 minutes from now") and convert this to a fixed
 * time as needed.
 * 
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
@SuppressWarnings("serial")
public class RelativeDate implements Serializable {

	public static final String KEY_TIME_VALUE = "timeValue";
	public static final String KEY_TIME_UNIT = "timeUnit";

	public static final String PREFIX = "currentTimeOffset";

	/**
	 * Since this is often embedded in another class during serialization, it may be serialized as a Map instead. This
	 * convenience method is used to tell us whether the Map is likely to be a deserialized RelativeDate.
	 * 
	 * @param maybeRelativeDate
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isDeserializedRelativeDate(Map maybeRelativeDate) {
		return (maybeRelativeDate != null && maybeRelativeDate.keySet().size() == 2
				&& maybeRelativeDate.containsKey("timeValue") && maybeRelativeDate.containsKey("timeUnit"));
	}

	public static Date calculateTime(int timeValue, TimeUnit timeUnit) {
		long queryTime = System.currentTimeMillis();
		switch (timeUnit) {
		case MINUTES:
			queryTime += (timeValue * 60 * 1000);
			break;
		case HOURS:
			queryTime += (timeValue * 60 * 60 * 1000);
			break;
		case DAYS:
			queryTime += (timeValue * 24 * 60 * 60 * 1000);
			break;
		}
		return new Date(queryTime);
	}

	public static RelativeDate parseValue(String value) {
		int timeValueBegin = PREFIX.length();
		int timeValueEnd = timeValueBegin;
		for (int i = PREFIX.length(); value.charAt(i) == '-' || Character.isDigit(value.charAt(i)); i++) {
			timeValueEnd++;
		}
		if (timeValueBegin >= timeValueEnd) {
			throw new IllegalStateException("No time value was found in '" + value + "'.");
		}
		String maybeTimeValue = value.substring(timeValueBegin, timeValueEnd);
		int timeValue = 0;
		try {
			timeValue = Integer.parseInt(maybeTimeValue);
		} catch (NumberFormatException e) {
			throw new IllegalStateException("The time value '" + maybeTimeValue + "' must be a number.");
		}
		String maybeTimeUnit = value.substring(timeValueEnd);
		TimeUnit timeUnit = TimeUnit.valueOf(maybeTimeUnit.toUpperCase());
		if (timeUnit == null) {
			throw new IllegalStateException("The time unit value '" + maybeTimeUnit + "' is invalid.");
		}

		return new RelativeDate(timeValue, timeUnit);
	}

	private TimeUnit timeUnit;
	private int timeValue;

	public RelativeDate() {
		// no-arg constructor for serialization
	}

	public RelativeDate(int timeValue, TimeUnit timeUnit) {
		super();
		this.timeUnit = timeUnit;
		this.timeValue = timeValue;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(int timeValue) {
		this.timeValue = timeValue;
	}

	public String toString() {
		return new StringBuilder(PREFIX).append(timeValue).append(timeUnit.name().toLowerCase()).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((timeUnit == null) ? 0 : timeUnit.hashCode());
		result = prime * result + timeValue;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelativeDate other = (RelativeDate) obj;
		if (!timeUnit.name().equals(other.timeUnit.name()))
			return false;
		if (timeValue != other.timeValue)
			return false;
		return true;
	}

}
