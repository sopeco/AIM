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
package org.aim.api.measurement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link RecordValue} annotation is used to mark members of record classes
 * as record parameters.
 * 
 * @author Alexander Wert
 * 
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RecordValue {
	/**
	 * Specifies whether the underlying parameter is a metric type or not. A
	 * metric type corresponds to a observation parameter.
	 * 
	 */
	boolean metric() default false;

	/**
	 * Specifies the name of the parameter.
	 * 
	 */
	String name();

	/**
	 * Specifies whether parameter is a timestamp. Annotation required if
	 * timestamp needs to be aligned over distributed nodes.
	 * 
	 */
	boolean isTimestamp() default false;
}
