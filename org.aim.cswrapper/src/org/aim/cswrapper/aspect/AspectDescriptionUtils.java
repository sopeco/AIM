package org.aim.cswrapper.aspect;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aim.artifacts.events.probes.MonitorWaitingTimeProbe;
import org.aim.artifacts.probes.ResponsetimeProbe;
import org.aim.artifacts.probes.SQLQueryProbe;
import org.aim.artifacts.scopes.EntryPointScope;
import org.aim.artifacts.scopes.JDBCScope;
import org.aim.description.InstrumentationEntity;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.APIScope;
import org.aim.description.scopes.MethodScope;
import org.aim.description.scopes.Scope;
import org.aim.description.scopes.SynchronizedScope;

public class AspectDescriptionUtils {

	public static List<AspectDescription> convert(InstrumentationEntity<?> entity) {
		List<AspectDescription> retList = new ArrayList<>();

		boolean responseSQL = false;
		boolean containsRP = false;
		boolean containsSP = false;
		for (MeasurementProbe<?> probe : entity.getProbes()) {
			if (probe.equals(ResponsetimeProbe.MODEL_PROBE)) {
				if (containsSP){
					responseSQL = true;
				}else {
					containsRP = true;
				}
			}
			
			if (probe.equals(SQLQueryProbe.MODEL_PROBE)) {
				if (containsRP){
					responseSQL = true;
				} else {
					containsSP = true;
				}
			}
		}

		if (responseSQL) {
			MeasurementProbe<?> probe = entity.getProbes().iterator().next();
			addProbe(probe, entity, retList, "ResponseTimeSQLQueryAspect");
		} else {
			for (MeasurementProbe<?> probe : entity.getProbes()) {
				addProbe(probe, entity, retList, null);
			}
		}

		return retList;
	}

	private static void addProbe(MeasurementProbe<?> probe, InstrumentationEntity<?> entity,
			List<AspectDescription> retList, String aspectName) {
		AspectDescription aspect = new AspectDescription();
		Restriction restriction = entity.getLocalRestriction();

		// Aspect class
		if (aspectName == null)
			aspect.setAspectClass(propeToAspect(entity.getScope(), probe));
		else
			aspect.setAspectClass(aspectName);

		// Modifiers
		setModifiers(aspect, restriction.getModifierIncludes());

		// Packages
		if (!restriction.getPackageIncludes().isEmpty()) {
			StringBuilder incPackages = new StringBuilder("regex:");
			Iterator<String> iterator = restriction.getPackageIncludes().iterator();
			while (iterator.hasNext()) {
				incPackages.append(iterator.next());
				if (iterator.hasNext()) {
					incPackages.append("|");
				}
			}
			aspect.setTargetTypes(incPackages.toString());
		}

		// Translate scope into the aspect description
		if (entity.getScope() instanceof SynchronizedScope) {
			// SynchronizedScope
			aspect.setOnSynchronized(true);
		} else if (entity.getScope() instanceof APIScope) {
			// APIScope
			APIScope apiScope = (APIScope) entity.getScope();

			// EntryPointScope
			if (apiScope.getApiName().equals(EntryPointScope.class.getName())) {
				aspect.setSuperClass("System.Web.Mvc.Controller");
				aspect.setSuperClassAssembly("System.Web.Mvc");
			}

			// JDBCScope
			if (apiScope.getApiName().equals(JDBCScope.class.getName())) {
				//aspect.setTargetAssemblies("EntityFramework");
				aspect.setTargetTypes("Nop.Data.NopObjectContext");
				aspect.setTargetMembers("regex:.*Sql.*");
			}
		} else if (entity.getScope() instanceof MethodScope) {
			// TODO only 1 method is supported at the moment!
			// MethodScope
			// Assembly:Class.Path:Method
			// Class.Path:Method # Local Assembly
			MethodScope methodScopehodScope = (MethodScope) entity.getScope();

			for (String methodPattern : methodScopehodScope.getMethods()) {
				String split[] = methodPattern.split(":");

				if (split.length == 2) {
					aspect.setTargetTypes(split[0]);
					aspect.setTargetMembers(split[1]);
				} else if (split.length == 3) {
					aspect.setTargetAssemblies(split[0]);
					aspect.setTargetTypes(split[1]);
					aspect.setTargetMembers(split[2]);
				}
			}
		}

		retList.add(aspect);

		/*
		 * Excluding
		 */
		if (!restriction.getModifierExcludes().isEmpty() || !restriction.getPackageExcludes().isEmpty()) {
			AspectDescription aspectExclude = new AspectDescription();

			// Aspect class
			aspectExclude.setAspectClass(propeToAspect(entity.getScope(), probe));

			// Modifiers
			setModifiers(aspectExclude, restriction.getModifierExcludes());

			// Packages
			if (!restriction.getPackageExcludes().isEmpty()) {
				StringBuilder excPackages = new StringBuilder("regex:");
				Iterator<String> iterator = restriction.getPackageExcludes().iterator();
				while (iterator.hasNext()) {
					excPackages.append(iterator.next());
					if (iterator.hasNext()) {
						excPackages.append("|");
					}
				}
				aspectExclude.setTargetTypes(excPackages.toString());
			}

			aspectExclude.setAttributeExclude(true);

			aspect.setPriority(1);
			aspectExclude.setPriority(2);

			// Adding
			retList.add(aspectExclude);
		}
	}

	/**
	 * Sets the given modifiers in the targetAspect.
	 * 
	 * @param targetAspect
	 *            aspect that receives the given modifiers
	 * @param modifiers
	 *            modifiers that are applied to the aspect
	 */
	private static void setModifiers(AspectDescription targetAspect, Set<Integer> modifiers) {
		for (Integer mod : modifiers) {
			if (Modifier.isAbstract(mod)) {
				targetAspect.setOnAbstract(true);
			}
			if (Modifier.isPrivate(mod)) {
				targetAspect.setOnPrivate(true);
			}
			if (Modifier.isProtected(mod)) {
				targetAspect.setOnProtected(true);
			}
			if (Modifier.isPublic(mod)) {
				targetAspect.setOnPublic(true);
			}
			if (Modifier.isStatic(mod)) {
				targetAspect.setOnStatic(true);
			}
		}
	}

	/**
	 * Returns a String which represents the Class name of an aspect related to
	 * the given probe and scope.
	 * 
	 * @param scope
	 * @param probe
	 * @return Name of the aspect class
	 */
	private static String propeToAspect(Scope scope, MeasurementProbe<?> probe) {
		if (probe.equals(ResponsetimeProbe.MODEL_PROBE) || ResponsetimeProbe.class.isAssignableFrom(probe.getClass())) {
			return "ResponseTimeAspect";
		}
		if (scope instanceof SynchronizedScope && probe.equals(MonitorWaitingTimeProbe.MODEL_PROBE)) {
			return "ResponseTimeAspect";
		}
		if (probe.equals(SQLQueryProbe.MODEL_PROBE)) {
			return "SQLQueryAspect";
		}
		throw new UnsupportedPropeException(probe);
	}

}
