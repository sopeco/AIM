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

import org.aim.description.restrictions.Restriction;

/**
 * Common interface of builders building restrictable elements.
 * 
 * @author Henning Schulz
 * 
 */
public abstract class AbstractRestrictableBuilder {

	/**
	 * Restricts this object by the given {@link Restriction}.
	 * 
	 * @param restriction
	 *            restriction to be applied
	 */
	protected abstract void setRestriction(Restriction restriction);

}
