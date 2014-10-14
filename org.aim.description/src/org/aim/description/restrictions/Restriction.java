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
package org.aim.description.restrictions;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.lpe.common.util.LpeStringUtils;

/**
 * This class represents a restriction to a given scope.
 * 
 * @author Henning Schulz
 * 
 */
public class Restriction {

	private static final int HASH_PRIME = 31;

	private final Set<String> packageIncludes;
	private final Set<String> packageExcludes;

	private final Set<Integer> modifierIncludes;
	private final Set<Integer> modifierExcludes;

	private double granularity = 1.0;

	/**
	 * Constructor. Sets all sets to empty ones.
	 */
	@JsonCreator
	public Restriction() {
		this.packageIncludes = new HashSet<>();
		this.packageExcludes = new HashSet<>();
		this.modifierExcludes = new HashSet<>();
		this.modifierIncludes = new HashSet<>();
	}

	/**
	 * Includes the given package.
	 * 
	 * @param packageName
	 *            package to be included
	 */
	public void addPackageInclude(String packageName) {
		packageIncludes.add(packageName);
	}

	/**
	 * @return the packageIncludes
	 */
	public Set<String> getPackageIncludes() {
		return packageIncludes;
	}

	/**
	 * Excludes the given package.
	 * 
	 * @param packageName
	 *            package to be excluded
	 */
	public void addPackageExclude(String packageName) {
		packageExcludes.add(packageName);
	}

	/**
	 * @return the packageExcludes
	 */
	public Set<String> getPackageExcludes() {
		return packageExcludes;
	}

	/**
	 * Includes all methods having the given modifier.
	 * 
	 * @param modifier
	 *            modifier of the methods to be included
	 */
	public void addModifierInclude(int modifier) {
		modifierIncludes.add(modifier);
	}

	/**
	 * @return the modifierIncludes
	 */
	public Set<Integer> getModifierIncludes() {
		return modifierIncludes;
	}

	/**
	 * Excludes all methods having the given modifier.
	 * 
	 * @param modifier
	 *            modifier of the methods to be excluded
	 */
	public void addModifierExclude(int modifier) {
		modifierExcludes.add(modifier);
	}

	/**
	 * @return the modifierExcludes
	 */
	public Set<Integer> getModifierExcludes() {
		return modifierExcludes;
	}

	/**
	 * Sets the granularity. Note is has to be between 0 to 1.
	 * 
	 * @param granularity
	 *            granularity to be set
	 */
	public void setGranularity(double granularity) {
		if (granularity > 1 || granularity < 0) {
			throw new IllegalArgumentException("The granularity has to be between 0 to 1!");
		}

		this.granularity = granularity;
	}

	/**
	 * @return the granularity
	 */
	public double getGranularity() {
		return granularity;
	}

	/**
	 * Checks whether given entity is excluded from instrumentation.
	 * 
	 * @param entityName
	 *            full qualified name of the entity (class, package, interface,
	 *            etc. ) to check
	 * @return true, if entity shell be excluded from instrumentation
	 */
	@JsonIgnore
	public boolean isExcluded(String entityName) {

		if (getPackageIncludes().isEmpty()) {
			for (String excl : getPackageExcludes()) {
				if (LpeStringUtils.patternMatches(entityName, excl)) {
					return true;
				}
			}
			return false;
		} else {
			boolean found = false;
			for (String incl : getPackageIncludes()) {
				if (LpeStringUtils.patternMatches(entityName, incl)) {
					found = true;
					break;
				}
			}

			if (!found) {
				return true;
			}

			for (String excl : getPackageExcludes()) {
				if (LpeStringUtils.patternMatches(entityName, excl)) {
					return true;
				}
			}
			return false;

		}
	}

	/**
	 * Checks whether restriction is empty.
	 * 
	 * @return true if no restrictions have been defined
	 */
	@JsonIgnore
	public boolean isEmpty() {
		return packageExcludes.isEmpty() && packageIncludes.isEmpty() && modifierExcludes.isEmpty()
				&& modifierIncludes.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		for (String include : packageIncludes) {
			builder.append("+");
			builder.append(include);
			builder.append(", ");
			trailingComma = true;
		}

		for (int mod : modifierIncludes) {
			builder.append("+\"");
			builder.append(Modifier.toString(mod));
			builder.append("\" methods, ");
			trailingComma = true;
		}

		for (String exclude : packageExcludes) {
			builder.append("-");
			builder.append(exclude);
			builder.append(", ");
			trailingComma = true;
		}

		for (int mod : modifierExcludes) {
			builder.append("-\"");
			builder.append(Modifier.toString(mod));
			builder.append("\" methods, ");
			trailingComma = true;
		}

		if (trailingComma) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

	/**
	 * Returns all included modifiers as one integer (the modifiers linked with
	 * OR).
	 * 
	 * @return all included modifiers
	 */
	@JsonIgnore
	public int getModifiersOrLinked() {
		int thisModifiers = 0;
		for (int includedModifier : getModifierIncludes()) {
			thisModifiers |= includedModifier;
		}

		for (int excludedModifier : getModifierExcludes()) {
			thisModifiers &= ~excludedModifier;
		}

		return thisModifiers;
	}

	/**
	 * Checks whether the given set of modifiers is excluded. THis method checks
	 * whether the passed set of modifiers covers all modifiers included in the
	 * restrictions.
	 * 
	 * @param modifiers
	 *            binary representation of a set of modifiers
	 * @return true, if given set is excluded by the restrictions
	 */
	public boolean modifierSetExcluded(int modifiers) {
		if (!hasModifierRestrictions()) {
			return false;
		}

		int thisModifiers = getModifiersOrLinked();

		return (thisModifiers & modifiers) != thisModifiers;
	}

	/**
	 * Checks whether this restriction object has modifier restrictions.
	 * 
	 * @return true, if any modifier restrictions have been defined
	 */
	public boolean hasModifierRestrictions() {
		return !getModifierIncludes().isEmpty() || !getModifierExcludes().isEmpty();
	}

	/**
	 * Returns the merge of this restriction and the specified one. The
	 * granularity is not overwritten.
	 * 
	 * @param other
	 *            Restriction to be merged with
	 * @return merge of this restriction and {@code other}
	 */
	public Restriction mergeWith(Restriction other) {
		Restriction result = new Restriction();
		result.getModifierExcludes().addAll(this.getModifierExcludes());
		result.getModifierExcludes().addAll(other.getModifierExcludes());
		result.getModifierIncludes().addAll(this.getModifierIncludes());
		result.getModifierIncludes().addAll(other.getModifierIncludes());
		result.getPackageExcludes().addAll(this.getPackageExcludes());
		result.getPackageExcludes().addAll(other.getPackageExcludes());
		result.getPackageIncludes().addAll(this.getPackageIncludes());
		result.getPackageIncludes().addAll(other.getPackageIncludes());
		result.setGranularity(this.getGranularity());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		Restriction other = (Restriction) obj;

		return this.getPackageExcludes().equals(other.getPackageExcludes())
				&& this.getPackageIncludes().equals(other.getPackageIncludes())
				&& this.getModifiersOrLinked() == other.getModifiersOrLinked();
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * HASH_PRIME + getModifiersOrLinked();
		hash = hash * HASH_PRIME + getPackageExcludes().hashCode();
		hash = hash * HASH_PRIME + getPackageIncludes().hashCode();
		return hash;
	}

}
