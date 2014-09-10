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

	private int[] excModifiers;
	private String[] excPackages;
	private int[] incModifiers;
	private String[] incPackages;
	private boolean isTraceScope;
	private String[] probes;
	private String scope;
	private String[] scopeSettings;

	/**
	 * @return the excModifiers
	 */
	public int[] getExcModifiers() {
		return excModifiers;
	}

	/**
	 * @return the excPackages
	 */
	public String[] getExcPackages() {
		return excPackages;
	}

	/**
	 * @return the incModifiers
	 */
	public int[] getIncModifiers() {
		return incModifiers;
	}

	/**
	 * @return the incPackages
	 */
	public String[] getIncPackages() {
		return incPackages;
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
	 * @param excModifiers
	 *            the excModifiers to set
	 */
	public void setExcModifiers(int[] excModifiers) {
		this.excModifiers = excModifiers;
	}

	/**
	 * @param excPackages
	 *            the excPackages to set
	 */
	public void setExcPackages(String[] excPackages) {
		this.excPackages = excPackages;
	}

	/**
	 * @param incModifiers
	 *            the incModifiers to set
	 */
	public void setIncModifiers(int[] incModifiers) {
		this.incModifiers = incModifiers;
	}

	/**
	 * @param incPackages
	 *            the incPackages to set
	 */
	public void setIncPackages(String[] incPackages) {
		this.incPackages = incPackages;
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
