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

import java.util.Set;

import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
import org.aim.description.restrictions.Restriction;

/**
 * A scope analyzer represents a certain scope and decides for a given class
 * whether and if yes, which methods of this class need to be instrumented.
 * 
 * @author Alexander Wert
 * 
 */
public interface IScopeAnalyzer {

	/**
	 * Decides for a given class whether and if yes, which methods of this class
	 * need to be instrumented.
	 * 
	 * @param clazz
	 *            class to analyse
	 * @param scopeEntities
	 *            set where to add new scope entities
	 */
	void visitClass(Class<?> clazz, Set<FlatScopeEntity> scopeEntities);

	/**
	 * Sets instrumentation restriction.
	 * 
	 * @param restriction
	 *            restriction instance
	 */
	void setRestriction(Restriction restriction);

	/**
	 * 
	 * @return id of the scope
	 */
	Long getScopeId();

	/**
	 * 
	 * @param scopeId
	 *            scope id to set
	 */
	void setScopeId(Long scopeId);

}
