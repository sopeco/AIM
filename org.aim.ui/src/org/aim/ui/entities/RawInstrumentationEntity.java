package org.aim.ui.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * Abstract representation of an
 * {@link org.aim.description.InstrumentationEntity}.
 * 
 * @author Marius Oehler
 *
 */
@XmlRootElement(name = "entity")
public class RawInstrumentationEntity {

	private int[] excludedModifiers;
	private String[] excludedPackages;
	private int[] includedModifiers;
	private String[] includedPackages;
	private boolean isTraceScope;
	private String[] probes;
	private String scope;
	private String[] scopeSettings;

	/**
	 * @return the excludedModifiers
	 */
	public int[] getExcludedModifiers() {
		return excludedModifiers;
	}

	/**
	 * @return the excludedPackages
	 */
	public String[] getExcludedPackages() {
		return excludedPackages;
	}

	/**
	 * @return the includedModifiers
	 */
	public int[] getIncludedModifiers() {
		return includedModifiers;
	}

	/**
	 * @return the includedPackages
	 */
	public String[] getIncludedPackages() {
		return includedPackages;
	}

	/**
	 * @return the probes
	 */
	public String[] getProbes() {
		return probes;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @return the scopeSettings
	 */
	public String[] getScopeSettings() {
		return scopeSettings;
	}

	/**
	 * @return the isTraceScope
	 */
	public boolean isTraceScope() {
		return isTraceScope;
	}

	/**
	 * @param excludedModifiers
	 *            the excludedModifiers to set
	 */
	public void setExcludedModifiers(int[] excludedModifiers) {
		this.excludedModifiers = excludedModifiers;
	}

	/**
	 * @param excludedPackages
	 *            the excludedPackages to set
	 */
	public void setExcludedPackages(String[] excludedPackages) {
		this.excludedPackages = excludedPackages;
	}

	/**
	 * @param includedModifiers
	 *            the includedModifiers to set
	 */
	public void setIncludedModifiers(int[] includedModifiers) {
		this.includedModifiers = includedModifiers;
	}

	/**
	 * @param includedPackages
	 *            the includedPackages to set
	 */
	public void setIncludedPackages(String[] includedPackages) {
		this.includedPackages = includedPackages;
	}

	/**
	 * @param probes
	 *            the probes to set
	 */
	public void setProbes(String[] probes) {
		this.probes = probes;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @param scopeSettings
	 *            the scopeSettings to set
	 */
	public void setScopeSettings(String[] scopeSettings) {
		this.scopeSettings = scopeSettings;
	}

	/**
	 * @param isTraceScope
	 *            the isTraceScope to set
	 */
	public void setTraceScope(boolean isTraceScope) {
		this.isTraceScope = isTraceScope;
	}

}
