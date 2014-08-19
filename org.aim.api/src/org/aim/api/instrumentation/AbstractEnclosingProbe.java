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
package org.aim.api.instrumentation;

import org.aim.api.measurement.collector.IDataCollector;
import org.lpe.common.extension.AbstractExtensionArtifact;
import org.lpe.common.extension.IExtension;

/**
 * Rules for coding a probe:
 * 
 * 1. all probes must inherit the AbstractProbe
 * 
 * 2. use _<CLASSNAME>_ as prefix for all ProbeVariables
 * 
 * 3. all ProbeVariables and ProbeMethods have to be public
 * 
 * 4. use the variable __methodSignature as a placeholder for the method
 * signature
 * 
 * 5. use the variable __parameter[<NUMBER>] as a placeholder for the signature
 * parameter at position <NUMBER>
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractEnclosingProbe extends AbstractExtensionArtifact {

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public AbstractEnclosingProbe(IExtension<?> provider) {
		super(provider);
	}

	public static final String J_INSTRUMENTATION_KEY = "jinstrumentation";
	public static final String METHOD_SIGNATURE_PLACE_HOLDER = "__methodSignature";

	public static final String PARAMETER_PLACE_HOLDER = "__parameter";
	public static final String RETURN_OBJECT_PLACE_HOLDER = "__returnObject";
	/**
	 * Placeholder for the method signature.
	 */
	public String __methodSignature = METHOD_SIGNATURE_PLACE_HOLDER;

	public Object[] __parameter;

	public Object __returnObject;

	/**
	 * timestamp of the record.
	 */
	public long _GenericProbe_startTime;
	/**
	 * call id.
	 */
	public long _GenericProbe_callId;
	/**
	 * common data collector.
	 */
	public IDataCollector _GenericProbe_collector;

}
