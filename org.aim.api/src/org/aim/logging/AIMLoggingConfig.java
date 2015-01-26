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
package org.aim.logging;

/**
 * Configuration of AIM Logging.
 * 
 * @author Alexander Wert
 * 
 */
public class AIMLoggingConfig {

	/**
	 * Logging type.
	 * 
	 * @author Alexander Wert
	 * 
	 */
	public enum LoggingType {
		STDOUT, FILE
	}

	private LoggingType loggingType = LoggingType.STDOUT;
	private String fileName = "aim.log";
	private int loggingLevel = LoggingLevel.ERROR;

	/**
	 * @return the loggingType
	 */
	public LoggingType getLoggingType() {
		return loggingType;
	}

	/**
	 * @param loggingType
	 *            the loggingType to set
	 */
	public void setLoggingType(LoggingType loggingType) {
		this.loggingType = loggingType;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the loggingLevel
	 */
	public int getLoggingLevel() {
		return loggingLevel;
	}

	/**
	 * @param loggingLevel
	 *            the loggingLevel to set
	 */
	public void setLoggingLevel(int loggingLevel) {
		this.loggingLevel = loggingLevel;
	}

}
