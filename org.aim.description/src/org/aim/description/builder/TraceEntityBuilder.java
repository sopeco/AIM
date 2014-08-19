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
package org.aim.description.builder;

import org.aim.description.scopes.APIScope;
import org.aim.description.scopes.ConstructorScope;
import org.aim.description.scopes.CustomScope;
import org.aim.description.scopes.MethodScope;
import org.aim.description.scopes.TraceScope;

/**
 * Builder of an {@link org.aim.description.InstrumentationEntity} with a
 * {@link TraceScope}. It is only responsible for setting the sub-scope and
 * delegates all further buildings to the {@link InstrumentationEntityBuilder}.
 * 
 * @author Henning Schulz
 * 
 */
public class TraceEntityBuilder {
	private long scopeId;
	private final InstrumentationDescriptionBuilder parentBuilder;

	/**
	 * Constructor.
	 * 
	 * @param parentBuilder
	 *            builder which called this constructor
	 */
	public TraceEntityBuilder(InstrumentationDescriptionBuilder parentBuilder) {
		this(parentBuilder, System.nanoTime());
	}

	/**
	 * Constructor.
	 * 
	 * @param parentBuilder
	 *            parent builder
	 * @param id
	 *            scope id
	 */
	public TraceEntityBuilder(InstrumentationDescriptionBuilder parentBuilder, long id) {
		this.parentBuilder = parentBuilder;
		scopeId = id;
	}

	/**
	 * Sets a method scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param patterns
	 *            method patterns of the sub-scope
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setMethodSubScope(String... patterns) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new MethodScope(patterns), scopeId), parentBuilder);
	}

	/**
	 * Sets a constructor scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param classes
	 *            classes to be considered in the sub-scope
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setConstructorSubScope(String... classes) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new ConstructorScope(classes), scopeId), parentBuilder);
	}

	/**
	 * Sets an API scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param apiName
	 *            name of the API
	 * 
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setAPISubScope(String apiName) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new APIScope(apiName), scopeId), parentBuilder);
	}

	/**
	 * Sets an custom scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param scopeName
	 *            name of the scope
	 * 
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setCustomSubScope(String scopeName) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new CustomScope(scopeName), scopeId), parentBuilder);
	}

}
