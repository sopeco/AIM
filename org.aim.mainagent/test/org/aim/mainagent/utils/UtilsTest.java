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
package org.aim.mainagent.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import junit.framework.Assert;

import org.junit.Test;
import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.ResponseTimeRecord;

public class UtilsTest {
	@Test
	public void testIsNormalClass() {

		class MyLocalClass {

		}

		Assert.assertTrue(Utils.isNormalClass(this.getClass()));
		Assert.assertFalse(Utils.isNormalClass(Runnable.class));
		Assert.assertFalse(Utils.isNormalClass(int[].class));
		Assert.assertFalse(Utils.isNormalClass(Object[].class));
		Assert.assertFalse(Utils.isNormalClass(int.class));
		Assert.assertFalse(Utils.isNormalClass(EmbeddedClass.class));
		Assert.assertFalse(Utils.isNormalClass(MyLocalClass.class));
		Assert.assertFalse(Utils.isNormalClass(ProbeVariable.class));
	}

	@Test
	public void testIsBootstrapClass() throws ClassNotFoundException {
		Assert.assertTrue(Utils.isBootstrapClass(Object.class));
		Assert.assertTrue(Utils.isBootstrapClass(String.class));
		Assert.assertFalse(Utils.isBootstrapClass(this.getClass()));
		Class<?> clazz = Class.forName(ResponseTimeRecord.class.getName());
		Assert.assertFalse(Utils.isBootstrapClass(clazz));
	}

	@Test
	public void testIsInterface() {

		class MyLocalClass {

		}

		Assert.assertTrue(Utils.isInterface(Runnable.class));
		Assert.assertFalse(Utils.isInterface(this.getClass()));
		Assert.assertFalse(Utils.isInterface(int[].class));
		Assert.assertFalse(Utils.isInterface(Object[].class));
		Assert.assertFalse(Utils.isInterface(int.class));
		Assert.assertFalse(Utils.isInterface(EmbeddedClass.class));
		Assert.assertFalse(Utils.isInterface(MyLocalClass.class));
		Assert.assertFalse(Utils.isInterface(ProbeVariable.class));
	}

	@Test
	public void testGetCtBehaviourUtils() throws NotFoundException,
			InstrumentationException {

		ClassPool cPool = new ClassPool(true);
		CtClass ctClass = cPool.get(DummyClass.class.getName());

		Assert.assertNull(Utils.getCtBehaviour(ctClass, "wrongName()"));

		Assert.assertNotNull(Utils.getCtBehaviour(ctClass, "DummyClass()"));
		Assert.assertTrue(Utils.getCtBehaviour(ctClass, "DummyClass()") instanceof CtConstructor);

		Assert.assertNotNull(Utils.getCtBehaviour(ctClass,
				"DummyClass(java.lang.Integer)"));
		Assert.assertTrue(Utils.getCtBehaviour(ctClass,
				"DummyClass(java.lang.Integer)") instanceof CtConstructor);

		Assert.assertNotNull(Utils
				.getCtBehaviour(ctClass, "DummyClass(double)"));
		Assert.assertTrue(Utils.getCtBehaviour(ctClass, "DummyClass(double)") instanceof CtConstructor);

		Assert.assertNotNull(Utils
				.getCtBehaviour(ctClass,
						"org.lpe.common.instrumentation.adaptive.utils.DummyClass.methodA()"));
		Assert.assertTrue(Utils.getCtBehaviour(ctClass, "methodA()") instanceof CtMethod);
		Assert.assertFalse(Modifier.isStatic(Utils.getCtBehaviour(ctClass,
				"methodA()").getModifiers()));

		Assert.assertNotNull(Utils.getCtBehaviour(ctClass,
				"methodB(java.lang.Integer)"));
		Assert.assertTrue(Utils.getCtBehaviour(ctClass,
				"methodB(java.lang.Integer)") instanceof CtMethod);

		Assert.assertNotNull(Utils.getCtBehaviour(ctClass, "staticMethod()"));
		Assert.assertTrue(Utils.getCtBehaviour(ctClass, "staticMethod()") instanceof CtMethod);
		Assert.assertTrue(Modifier.isStatic(Utils.getCtBehaviour(ctClass,
				"staticMethod()").getModifiers()));

	}

	@Test(expected = InstrumentationException.class)
	public void testGetCtBehaviourWithExceptionUtils()
			throws NotFoundException, InstrumentationException {
		ClassPool cPool = new ClassPool(true);
		CtClass ctClass = cPool.get(DummyClass.class.getName());

		Utils.getCtBehaviour(ctClass, "wrongName");
	}

	@Test
	public void testInsertMethodLocalVariables()
			throws InstrumentationException, NotFoundException {
		ClassPool cPool = new ClassPool(true);
		CtClass ctClass = cPool.get(DummyClass.class.getName());
		CtBehavior ctMethod = Utils.getCtBehaviour(ctClass,
				"methodB(java.lang.Integer)");
		CtBehavior ctConstructor = Utils.getCtBehaviour(ctClass,
				"DummyClass(java.lang.Integer)");
		Map<String, Class<?>> variables = new HashMap<>();
		variables.put("v1", int.class);
		variables.put("v2", Integer.class);
		variables.put("v3", ResponseTimeRecord.class);
		int numVarsBefore = ctMethod.getMethodInfo().getCodeAttribute()
				.getMaxLocals();
		Utils.insertMethodLocalVariables(ctMethod, variables);

		Assert.assertEquals(numVarsBefore + variables.size(), ctMethod
				.getMethodInfo().getCodeAttribute().getMaxLocals());

		numVarsBefore = ctConstructor.getMethodInfo().getCodeAttribute()
				.getMaxLocals();
		Utils.insertMethodLocalVariables(ctConstructor, variables);

		Assert.assertEquals(numVarsBefore + variables.size(), ctConstructor
				.getMethodInfo().getCodeAttribute().getMaxLocals());
	}

	@Test
	public void testGetParameterTypes() throws ClassNotFoundException {

		Method[] ms = DummyClassB.class.getMethods();

		for (Method m : ms) {
			Utils.getParameterTypes(m.toString());
		}
	}

	private class EmbeddedClass {

	}
}
