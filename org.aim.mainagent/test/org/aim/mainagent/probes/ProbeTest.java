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
package org.aim.mainagent.probes;

import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;

import org.junit.Before;
import org.junit.Test;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.mainagent.instrumentor.InstrumentorAccessor;
import org.aim.mainagent.utils.DummyClass;

public abstract class ProbeTest {

	public static final String INSTRUmENTED_METHOD = "org.lpe.common.instrumentation.adaptive.utils.DummyClass.methodA()";
	private static int counter = 0;
	public static int numInvokations = 1;

	@Before
	public synchronized void prepareTest() {
		numInvokations = getNumberOfMethodInvokations();
	}

	@Test
	public void testProbeInstrumentation() throws Exception {

		AbstractDataSource dataSource = CollectorFactory.createDataSource(MemoryDataSource.class.getName(), null);
		AbstractDataSource.setDefaultDataSource(dataSource);
		ClassPool cPool = new ClassPool(true);
		CtClass ctClass = cPool.get(DummyClass.class.getName());
		String methodSignature = INSTRUmENTED_METHOD;

		Set<Class<? extends AbstractEnclosingProbe>> probeTypes = new HashSet<>();
		probeTypes.add(getProbeType());

		InstrumentorAccessor.instrumentBehaviour(probeTypes, ctClass, methodSignature);

		ctClass.replaceClassName(DummyClass.class.getName(), DummyClass.class.getName() + "_" + (counter++));
		Class<?> dummyClass = ctClass.toClass();
		Object o = dummyClass.newInstance();

		checkPreCall(dataSource.read());

		dataSource.enable();
		String simpleMEthodName = INSTRUmENTED_METHOD.substring(INSTRUmENTED_METHOD.lastIndexOf('.') + 1,
				INSTRUmENTED_METHOD.indexOf('('));
		for (int i = 0; i < numInvokations; i++) {
			o.getClass().getMethod(simpleMEthodName).invoke(o);
		}

		dataSource.disable();
		checkPostCall(dataSource.read());
	}

	protected abstract Class<? extends AbstractEnclosingProbe> getProbeType();

	protected abstract void checkPreCall(MeasurementData mData);

	protected abstract void checkPostCall(MeasurementData mData);

	protected abstract int getNumberOfMethodInvokations();
}
