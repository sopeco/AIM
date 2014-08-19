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
package org.aim.mainagent.builder;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeIncrementalInstrumentation;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.mainagent.probes.GenericProbe;
import org.apache.tools.ant.filters.StringInputStream;

import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;

/**
 * Provides single snippets for different probes.
 * 
 * @author Alexander Wert
 * 
 */
public final class SnippetProvider {
	private static SnippetProvider instance;

	public static final String METHOD_NAME_REQUIREMENT_KEY = "requiredMethodName";

	/**
	 * Returns singleton instance.
	 * 
	 * @return singleton
	 */
	public static SnippetProvider getInstance() {
		if (instance == null) {
			instance = new SnippetProvider();
		}
		return instance;
	}

	private MultiSnippet genericSnippet = null;
	private final Map<Class<? extends AbstractEnclosingProbe>, MultiSnippet> snippets;

	private SnippetProvider() {
		snippets = new HashMap<Class<? extends AbstractEnclosingProbe>, MultiSnippet>();

	}

	/**
	 * Retrieves snippet for the given probe type.
	 * 
	 * @param probeType
	 *            probe type for which a snippet shell be retrieved
	 * @return a snippet
	 * @throws InstrumentationException
	 *             if snippet cannot be retrieved
	 */
	public MultiSnippet getSnippet(Class<? extends AbstractEnclosingProbe> probeType) throws InstrumentationException {
		if (snippets.containsKey(probeType)) {

			return snippets.get(probeType);
		} else {
			MultiSnippet snippet = createSnippet(probeType);
			snippets.put(probeType, snippet);
			return snippet;
		}
	}

	/**
	 * Retrieves the generic probe snippet.
	 * 
	 * @return snippet for common code
	 * @throws InstrumentationException
	 *             if snippet cannot be retrieved
	 */
	public MultiSnippet getGenericSnippet() throws InstrumentationException {
		if (genericSnippet == null) {
			genericSnippet = createSnippet(GenericProbe.class);
		}
		return genericSnippet;
	}

	private MultiSnippet createSnippet(Class<?> probeClass) throws InstrumentationException {

		MultiSnippet snippet = new MultiSnippet();

		setInjectionCode(probeClass, snippet);
		for (Field field : probeClass.getFields()) {
			if (field.isAnnotationPresent(ProbeVariable.class)) {
				snippet.getVariables().put(field.getName(), field.getType());
			}
		}

		return snippet;
	}

	private void setInjectionCode(Class<?> probeClass, MultiSnippet snippet) throws InstrumentationException {
		try {
			DecompilerSettings dcSettings = new DecompilerSettings();
			dcSettings.setForceExplicitImports(true);
			PlainTextOutput pto = new PlainTextOutput();
			Decompiler.decompile(probeClass.getName(), pto, dcSettings);

			StringInputStream sis = new StringInputStream(pto.toString());
			CompilationUnit cu = JavaParser.parse(sis);
			TypeDeclaration type = cu.getTypes().get(0);

			String packageName = cu.getPackage().getName().toString();
			Map<String, String> imports = getImports(cu);
			String className = type.getName();
			Set<String> nonProbeMethods = getAllNonProbeMethods(probeClass);

			List<BodyDeclaration> members = type.getMembers();
			for (BodyDeclaration member : members) {
				boolean beforeMethod = hasAnnotation(member, ProbeBeforePart.class.getSimpleName());
				boolean afterMethod = hasAnnotation(member, ProbeAfterPart.class.getSimpleName());
				boolean incrementalInstrumentation = hasAnnotation(member,
						ProbeIncrementalInstrumentation.class.getSimpleName());
				if (member instanceof MethodDeclaration && (beforeMethod || afterMethod || incrementalInstrumentation)) {
					MethodDeclaration method = (MethodDeclaration) member;

					String body = correctCode(imports, className, packageName, method, nonProbeMethods);
					Set<String> methodNameRequirements = getMethodNameRequirements(method);

					if (beforeMethod) {
						snippet.getBeforePart().put(methodNameRequirements, body);
					} else if (afterMethod) {
						snippet.getAfterPart().put(methodNameRequirements, body);
					} else if (incrementalInstrumentation) {
						snippet.setIncrementalPart(body);
					}
				}
			}
		} catch (Exception e) {
			throw new InstrumentationException(
					"Invalid Instrumentation Probe: Error in parsing instrumentation probe.", e);
		}
	}

	private String correctCode(Map<String, String> imports, String className, String packageName,
			MethodDeclaration method, Set<String> nonProbeMethods) {
		String thisFullClassName = packageName + "." + className;
		String body = method.getBody().toString();
		// remove enclosing braces
		int fi = method.getBody().toString().indexOf("{");
		int li = method.getBody().toString().lastIndexOf("}");
		body = body.substring(fi + 1, li);
		// replace all occurrences of "this." in front of a method (non-probe
		// variables / methods)
		body = body.replaceAll("this\\.(?![(_" + className + "_)(_" + GenericProbe.class.getSimpleName() + "_)(__)])",
				thisFullClassName + ".");
		// remove remaining occurrences of "this."
		body = body.replaceAll("this\\.", "");

		// correct calls to non probe methods
		for (String npm : nonProbeMethods) {
			body = body.replaceAll("(\\s" + npm + "\\()", thisFullClassName + "." + "$1");
		}

		// complements package prefix to all classes
		for (String classToReplace : imports.keySet()) {
			body = body.replaceAll("([^\\.\\w])" + classToReplace, "$1" + imports.get(classToReplace));
		}
		body = body.replace(AbstractEnclosingProbe.RETURN_OBJECT_PLACE_HOLDER, "$_");
		body = body.replaceAll("(" + AbstractEnclosingProbe.PARAMETER_PLACE_HOLDER + "\\[)(.+?)(\\])", "\\$$2");

		return body;
	}

	private Set<String> getAllNonProbeMethods(Class<?> probeClass) {

		Set<String> result = new HashSet<>();

		for (Method method : probeClass.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(ProbeBeforePart.class) && !method.isAnnotationPresent(ProbeAfterPart.class)) {
				result.add(method.getName());
			}
		}

		for (Method method : probeClass.getMethods()) {
			if (!method.isAnnotationPresent(ProbeBeforePart.class) && !method.isAnnotationPresent(ProbeAfterPart.class)) {
				result.add(method.getName());
			}
		}

		return result;
	}

	private Map<String, String> getImports(CompilationUnit cu) {
		Map<String, String> imports = new HashMap<>();
		for (ImportDeclaration imp : cu.getImports()) {
			String value = imp.getName().toString();
			int li = value.lastIndexOf(".");
			String key = value.substring(li + 1);

			imports.put(key, value);
		}
		return imports;
	}

	private boolean hasAnnotation(BodyDeclaration member, String annotation) {
		if (member.getAnnotations() == null) {
			return false;
		}
		for (AnnotationExpr annExpr : member.getAnnotations()) {
			if (annExpr.getName().toString().contains(annotation)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getMethodNameRequirements(BodyDeclaration member) {

		if (member.getAnnotations() == null) {
			return Collections.EMPTY_SET;
		}
		HashSet<String> result = new HashSet<>();
		for (AnnotationExpr annExpr : member.getAnnotations()) {
			for (Node node : annExpr.getChildrenNodes()) {
				if (node instanceof MemberValuePair) {
					MemberValuePair mvp = (MemberValuePair) node;
					if (mvp.getName().equals(METHOD_NAME_REQUIREMENT_KEY)) {
						String strValue = mvp.getValue().toString();
						if (strValue.startsWith("{") && strValue.endsWith("}")) {
							strValue = strValue.substring(1, strValue.length() - 1);
						}
						String[] values = strValue.split(",");
						for (String value : values) {
							value = value.trim();
							result.add(value.substring(1, value.length() - 1));
						}
						return result;
					}

				}
			}
		}

		return Collections.EMPTY_SET;

	}

}
