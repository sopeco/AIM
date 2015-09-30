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
package org.aim.aiminterface.description.restriction;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.lpe.common.util.LpeStringUtils;

/**
 * This class represents a restriction to a given scope.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public final class Restriction {

	public static final Restriction EMPTY_RESTRICTION = new Restriction(
			Collections.<String> emptySet(), Collections.<String> emptySet(), Collections.<Integer> emptySet(), Collections.<Integer> emptySet(), 1.0d);
	
	private final Set<String> packageIncludes;
	private final Set<String> packageExcludes;

	private final Set<Integer> modifierIncludes;
	private final Set<Integer> modifierExcludes;

	private final double granularity;

	@JsonCreator
	public Restriction(
			@JsonProperty("packageIncludes") final Set<String> packageIncludes, 
			@JsonProperty("packageExcludes") final Set<String> packageExcludes, 
			@JsonProperty("modifierIncludes") final Set<Integer> modifierIncludes,
			@JsonProperty("modifierExcludes") final Set<Integer> modifierExcludes, 
			@JsonProperty("granularity") final double granularity) {
		super();
		this.packageIncludes = new HashSet<>(packageIncludes);
		this.packageExcludes = new HashSet<String>(packageExcludes);
		this.modifierIncludes = new HashSet<Integer>(modifierIncludes);
		this.modifierExcludes = new HashSet<Integer>(modifierExcludes);
		this.granularity = granularity;
	}

	/**
	 * @return the packageIncludes
	 */
	public Set<String> getPackageIncludes() {
		return Collections.unmodifiableSet(packageIncludes);
	}

	/**
	 * @return the packageExcludes
	 */
	public Set<String> getPackageExcludes() {
		return Collections.unmodifiableSet(packageExcludes);
	}

	/**
	 * @return the modifierIncludes
	 */
	public Set<Integer> getModifierIncludes() {
		return Collections.unmodifiableSet(modifierIncludes);
	}

	/**
	 * @return the modifierExcludes
	 */
	public Set<Integer> getModifierExcludes() {
		return Collections.unmodifiableSet(modifierExcludes);
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
	public boolean isExcluded(final String entityName) {

		if (getPackageIncludes().isEmpty()) {
			for (final String excl : getPackageExcludes()) {
				if (LpeStringUtils.patternMatches(entityName, excl)) {
					return true;
				}
			}
			return false;
		} else {
			boolean found = false;
			for (final String incl : getPackageIncludes()) {
				if (LpeStringUtils.patternMatches(entityName, incl)) {
					found = true;
					break;
				}
			}

			if (!found) {
				return true;
			}

			for (final String excl : getPackageExcludes()) {
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

	/**
	 * Returns all included modifiers as one integer (the modifiers linked with
	 * OR).
	 * 
	 * @return all included modifiers
	 */
	@JsonIgnore
	public int getModifiersOrLinked() {
		int thisModifiers = 0;
		for (final int includedModifier : getModifierIncludes()) {
			thisModifiers |= includedModifier;
		}

		for (final int excludedModifier : getModifierExcludes()) {
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
	public boolean modifierSetExcluded(final int modifiers) {
		if (!hasModifierRestrictions()) {
			return false;
		}

		final int thisModifiers = getModifiersOrLinked();

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
	public Restriction mergeWith(final Restriction other) {
		final Set<String> packageIncludes = new HashSet<String>(this.packageIncludes);
		packageIncludes.addAll(other.packageIncludes);
		final Set<String> packageExcludes = new HashSet<String>(this.packageExcludes);
		packageIncludes.addAll(other.packageExcludes);
		final Set<Integer> modifierIncludes = new HashSet<Integer>(this.modifierIncludes);
		modifierIncludes.addAll(other.modifierIncludes);
		final Set<Integer> modifierExcludes = new HashSet<Integer>(this.modifierExcludes);
		modifierExcludes.addAll(other.modifierExcludes);

		return new Restriction(packageIncludes, packageExcludes, modifierIncludes, modifierExcludes, this.granularity);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;
	
		for (final String include : packageIncludes) {
			builder.append("+");
			builder.append(include);
			builder.append(", ");
			trailingComma = true;
		}
	
		for (final int mod : modifierIncludes) {
			builder.append("+\"");
			builder.append(Modifier.toString(mod));
			builder.append("\" methods, ");
			trailingComma = true;
		}
	
		for (final String exclude : packageExcludes) {
			builder.append("-");
			builder.append(exclude);
			builder.append(", ");
			trailingComma = true;
		}
	
		for (final int mod : modifierExcludes) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(granularity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((modifierExcludes == null) ? 0 : modifierExcludes.hashCode());
		result = prime * result + ((modifierIncludes == null) ? 0 : modifierIncludes.hashCode());
		result = prime * result + ((packageExcludes == null) ? 0 : packageExcludes.hashCode());
		result = prime * result + ((packageIncludes == null) ? 0 : packageIncludes.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Restriction other = (Restriction) obj;
		if (Double.doubleToLongBits(granularity) != Double.doubleToLongBits(other.granularity)) {
			return false;
		}
		if (modifierExcludes == null) {
			if (other.modifierExcludes != null) {
				return false;
			}
		} else if (!modifierExcludes.equals(other.modifierExcludes)) {
			return false;
		}
		if (modifierIncludes == null) {
			if (other.modifierIncludes != null) {
				return false;
			}
		} else if (!modifierIncludes.equals(other.modifierIncludes)) {
			return false;
		}
		if (packageExcludes == null) {
			if (other.packageExcludes != null) {
				return false;
			}
		} else if (!packageExcludes.equals(other.packageExcludes)) {
			return false;
		}
		if (packageIncludes == null) {
			if (other.packageIncludes != null) {
				return false;
			}
		} else if (!packageIncludes.equals(other.packageIncludes)) {
			return false;
		}
		return true;
	}

}
