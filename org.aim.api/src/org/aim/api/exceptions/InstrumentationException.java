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
 * This exception is thrown if instrumentation fails at some point.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public InstrumentationException() {
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            error message
	 */
	public InstrumentationException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            cause of the error
	 */
	public InstrumentationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            error message
	 * @param cause
	 *            cause of the error
	 */
	public InstrumentationException(String message, Throwable cause) {
		super(message, cause);
	}

}
