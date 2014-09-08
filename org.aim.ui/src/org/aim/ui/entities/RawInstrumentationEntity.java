package org.aim.ui.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entity")
public class RawInstrumentationEntity {

//	@XmlElement(name = "scope")
	private String scope;
//	@XmlElement(name = "isTraceScope")
	private boolean isTraceScope;
//	@XmlElement(name = "scopeSettings")
	private String[] scopeSettings;

//	@XmlElement(name = "probes")
	private String[] probes;

//	@XmlElement(name = "includedPackages")
	private String[] incPackages;
//	@XmlElement(name = "excludedPackages")
	private String[] excPackages;

//	@XmlElement(name = "includedModifiers")
	private int[] incModifiers;
//	@XmlElement(name = "excludedModifiers")
	private int[] excModifiers;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public boolean isTraceScope() {
		return isTraceScope;
	}

	public void setTraceScope(boolean isTraceScope) {
		this.isTraceScope = isTraceScope;
	}

	public String[] getScopeSettings() {
		return scopeSettings;
	}

	public void setScopeSettings(String[] scopeSettings) {
		this.scopeSettings = scopeSettings;
	}

	public String[] getProbes() {
		return probes;
	}

	public void setProbes(String[] probes) {
		this.probes = probes;
	}

	public String[] getIncPackages() {
		return incPackages;
	}

	public void setIncPackages(String[] incPackages) {
		this.incPackages = incPackages;
	}

	public String[] getExcPackages() {
		return excPackages;
	}

	public void setExcPackages(String[] excPackages) {
		this.excPackages = excPackages;
	}

	public int[] getIncModifiers() {
		return incModifiers;
	}

	public void setIncModifiers(int[] incModifiers) {
		this.incModifiers = incModifiers;
	}

	public int[] getExcModifiers() {
		return excModifiers;
	}

	public void setExcModifiers(int[] excModifiers) {
		this.excModifiers = excModifiers;
	}

}
