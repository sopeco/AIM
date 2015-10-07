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

import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.description.restriction.Restriction;

/**
 * Builder for a {@link Restriction}.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class RestrictionBuilder<ParentBuilderType extends AbstractRestrictableBuilder> {

	private final ParentBuilderType parentBuilder;
	private final Set<String> includePackages = new HashSet<>();
	private final Set<String> excludePackages = new HashSet<>();
	private final Set<Integer> includeModifiers = new HashSet<>();
	private final Set<Integer> excludeModifiers = new HashSet<>();
	
	private double granularity = 1.0;

	/**
	 * Constructor.
	 * 
	 * @param parentBuilder
	 *            builder which called this constructor.
	 * @param restriction 
	 */
	public RestrictionBuilder(final ParentBuilderType parentBuilder) {
		super();
		this.parentBuilder = parentBuilder;
	}

	/**
	 * Includes the specified package.
	 * 
	 * @param packageName
	 *            package to be included
	 * @return this builder
	 */
	public RestrictionBuilder<ParentBuilderType> includePackage(final String packageName) {
		includePackages.add(packageName);
		return this;
	}

	/**
	 * Excludes the specified package.
	 * 
	 * @param packageName
	 *            package to be excluded
	 * @return this builder
	 */
	public RestrictionBuilder<ParentBuilderType> excludePackage(final String packageName) {
		excludePackages.add(packageName);
		return this;
	}

	/**
	 * Includes all methods with the specified modifier.
	 * 
	 * @param modifier
	 *            modifier of the methods to be included
	 * @return this builder
	 */
	public RestrictionBuilder<ParentBuilderType> includeModifier(final int modifier) {
		includeModifiers.add(modifier);
		return this;
	}

	/**
	 * Excludes all methods with the specified modifier.
	 * 
	 * @param modifier
	 *            modifier of the methods to be excluded
	 * @return this builder
	 */
	public RestrictionBuilder<ParentBuilderType> excludeModifier(final int modifier) {
		excludeModifiers.add(modifier);
		return this;
	}
	
	/**
	 * Sets the granularity of this {@code Restriction}. Note is has
	 * to be between 0 to 1.
	 * 
	 * @param granularity
	 *            granularity to be set
	 * @return this builder
	 */
	public RestrictionBuilder<ParentBuilderType> setGranularity(final double granularity) {
		this.granularity = granularity;
		return this;
	}

	/**
	 * Finishes building of this restriction and returns to the parent builder.
	 * 
	 * @return the parent builder
	 */
	public ParentBuilderType restrictionDone() {
		parentBuilder.setRestriction(
			 new Restriction(this.includePackages, this.excludePackages, this.includeModifiers, this.excludeModifiers, this.granularity));
		return parentBuilder;
	}

	static <RBT extends AbstractRestrictableBuilder> RestrictionBuilder<RBT> fromRestriction(
			final AbstractRestrictableBuilder parent, final Restriction prototype) {
		final RestrictionBuilder<RBT> result = new RestrictionBuilder<RBT>((RBT) parent);
		result.excludeModifiers.addAll(prototype.getModifierExcludes());
		result.includeModifiers.addAll(prototype.getModifierIncludes());
		result.excludePackages.addAll(prototype.getPackageExcludes());
		result.includePackages.addAll(prototype.getPackageIncludes());
		result.granularity = prototype.getGranularity();
		return result;
	}

}
