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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.GenericProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeIncrementalInstrumentation;
import org.aim.api.instrumentation.ProbeVariable;

import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;

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
	public MultiSnippet getSnippet(final Class<? extends AbstractEnclosingProbe> probeType) throws InstrumentationException {
		if (snippets.containsKey(probeType)) {

			return snippets.get(probeType);
		} else {
			final MultiSnippet snippet = createSnippet(probeType);
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

	private MultiSnippet createSnippet(final Class<?> probeClass) throws InstrumentationException {

		final MultiSnippet snippet = new MultiSnippet();

		setInjectionCode(probeClass, snippet);
		for (final Field field : probeClass.getFields()) {
			if (field.isAnnotationPresent(ProbeVariable.class)) {
				snippet.getVariables().put(field.getName(), field.getType());
			}
		}

		return snippet;
	}

	private void setInjectionCode(final Class<?> probeClass, final MultiSnippet snippet) throws InstrumentationException {
		try {
			final DecompilerSettings dcSettings = new DecompilerSettings();
			dcSettings.setForceExplicitImports(true);
			final PlainTextOutput pto = new PlainTextOutput();
			Decompiler.decompile(probeClass.getName(), pto, dcSettings);

			final InputStream sis = new ByteArrayInputStream(pto.toString().getBytes("UTF-8"));
			final CompilationUnit cu = JavaParser.parse(sis);
			final TypeDeclaration type = cu.getTypes().get(0);

			final String packageName = cu.getPackage().getName().toString();
			final Map<String, String> imports = getImports(cu);
			final String className = type.getName();
			final Set<String> nonProbeMethods = getAllNonProbeMethods(probeClass);

			final List<BodyDeclaration> members = type.getMembers();
			for (final BodyDeclaration member : members) {
				final boolean beforeMethod = hasAnnotation(member, ProbeBeforePart.class.getSimpleName());
				final boolean afterMethod = hasAnnotation(member, ProbeAfterPart.class.getSimpleName());
				final boolean incrementalInstrumentation = hasAnnotation(member,
						ProbeIncrementalInstrumentation.class.getSimpleName());
				if (member instanceof MethodDeclaration && (beforeMethod || afterMethod || incrementalInstrumentation)) {
					final MethodDeclaration method = (MethodDeclaration) member;

					final String body = correctCode(imports, className, packageName, method, nonProbeMethods);
					final Set<String> methodNameRequirements = getMethodNameRequirements(method);

					if (beforeMethod) {
						snippet.getBeforePart().put(methodNameRequirements, body);
					} else if (afterMethod) {
						snippet.getAfterPart().put(methodNameRequirements, body);
					} else if (incrementalInstrumentation) {
						snippet.setIncrementalPart(body);
					}
				}
			}
		} catch (final Exception e) {
			throw new InstrumentationException(
					"Invalid Instrumentation Probe: Error in parsing instrumentation probe.", e);
		}
	}

	private String correctCode(final Map<String, String> imports, final String className, final String packageName,
			final MethodDeclaration method, final Set<String> nonProbeMethods) {
		final String thisFullClassName = packageName + "." + className;
		String body = method.getBody().toString();
		// remove enclosing braces
		final int fi = method.getBody().toString().indexOf("{");
		final int li = method.getBody().toString().lastIndexOf("}");
		body = body.substring(fi + 1, li);
		// replace all occurrences of "this." in front of a method (non-probe
		// variables / methods)
		body = body.replaceAll("this\\.(?![(_" + className + "_)(_" + GenericProbe.class.getSimpleName() + "_)(__)])",
				thisFullClassName + ".");
		// remove remaining occurrences of "this."
		body = body.replaceAll("this\\.", "");

		// correct calls to non probe methods
		for (final String npm : nonProbeMethods) {
			body = body.replaceAll("(\\s" + npm + "\\()", thisFullClassName + "." + "$1");
		}

		// complements package prefix to all classes
		for (final String classToReplace : imports.keySet()) {
			body = body.replaceAll("([^\\.\\w])" + classToReplace, "$1" + imports.get(classToReplace));
		}
		body = body.replace(AbstractEnclosingProbe.RETURN_OBJECT_PLACE_HOLDER, "$_");
		body = body.replaceAll("(" + AbstractEnclosingProbe.PARAMETER_PLACE_HOLDER + "\\[)(.+?)(\\])", "\\$$2");

		return body;
	}

	private Set<String> getAllNonProbeMethods(final Class<?> probeClass) {

		final Set<String> result = new HashSet<>();

		for (final Method method : probeClass.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(ProbeBeforePart.class) && !method.isAnnotationPresent(ProbeAfterPart.class)) {
				result.add(method.getName());
			}
		}

		for (final Method method : probeClass.getMethods()) {
			if (!method.isAnnotationPresent(ProbeBeforePart.class) && !method.isAnnotationPresent(ProbeAfterPart.class)) {
				result.add(method.getName());
			}
		}

		return result;
	}

	private Map<String, String> getImports(final CompilationUnit cu) {
		final Map<String, String> imports = new HashMap<>();
		for (final ImportDeclaration imp : cu.getImports()) {
			final String value = imp.getName().toString();
			final int li = value.lastIndexOf(".");
			final String key = value.substring(li + 1);

			imports.put(key, value);
		}
		return imports;
	}

	private boolean hasAnnotation(final BodyDeclaration member, final String annotation) {
		if (member.getAnnotations() == null) {
			return false;
		}
		for (final AnnotationExpr annExpr : member.getAnnotations()) {
			if (annExpr.getName().toString().contains(annotation)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getMethodNameRequirements(final BodyDeclaration member) {

		if (member.getAnnotations() == null) {
			return Collections.EMPTY_SET;
		}
		final HashSet<String> result = new HashSet<>();
		for (final AnnotationExpr annExpr : member.getAnnotations()) {
			for (final Node node : annExpr.getChildrenNodes()) {
				if (node instanceof MemberValuePair) {
					final MemberValuePair mvp = (MemberValuePair) node;
					if (mvp.getName().equals(METHOD_NAME_REQUIREMENT_KEY)) {
						String strValue = mvp.getValue().toString();
						if (strValue.startsWith("{") && strValue.endsWith("}")) {
							strValue = strValue.substring(1, strValue.length() - 1);
						}
						final String[] values = strValue.split(",");
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
