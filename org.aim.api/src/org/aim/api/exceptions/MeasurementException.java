/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.api.exceptions;

/**
 * The {@link MeasurementException} is thrown if something goes wrong during
 * configuration, initialization or execution of measurements.
 * 
 * @author Alexander Wert
 * 
 */
public class MeasurementException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 623832223169238394L;

	/**
	 * Constructor.
	 */
	public MeasurementException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            error message
	 */
	public MeasurementException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            error cause
	 */
	public MeasurementException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            error message
	 * @param cuase
	 *            error cause
	 */
	public MeasurementException(String message, Throwable cuase) {
		super(message, cuase);
	}
}
