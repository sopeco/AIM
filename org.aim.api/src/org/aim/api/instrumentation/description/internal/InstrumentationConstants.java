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
package org.aim.api.instrumentation.description.internal;

/**
 * Contains constants for the instrumentation implementation.
 * 
 * @author Alexander Wert
 * 
 */
public final class InstrumentationConstants {
	private InstrumentationConstants() {
	}

	public static final String JAVA_PACKAGE = "java.*";
	public static final String JAVAX_PACKAGE = "javax.*";
	public static final String JAVASSIST_PACKAGE = "javassist.*";
	public static final String LPE_COMMON_PACKAGE = "org.lpe.common.*";
	public static final String AIM_PACKAGE = "org.aim.*";
}
