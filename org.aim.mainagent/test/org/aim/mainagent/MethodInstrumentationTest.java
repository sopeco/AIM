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
package org.aim.mainagent;

import java.lang.reflect.Modifier;
import java.util.Properties;

import junit.framework.Assert;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.artifacts.probes.NanoResponsetimeProbe;
import org.aim.artifacts.probes.ResponsetimeProbe;
import org.aim.artifacts.records.NanoResponseTimeRecord;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.description.InstrumentationDescription;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.mainagent.instrumentor.JInstrumentation;
import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lpe.common.config.GlobalConfiguration;
import org.lpe.common.extension.ExtensionRegistry;
import org.test.sut.ClassA;
import org.test.sut.ClassB;
import org.test.sut.ClassC;
import org.test.sut.ClassD;
import org.test.sut.ClassE;
import org.test.sut.ClassF;
import org.test.sut.ClassG;
import org.test.sut.ClassH;

public class MethodInstrumentationTest {

	public static void enableMeasurement() throws MeasurementException {
		AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		dataSource.enable();
	}

	public static void disableMeasurement() throws MeasurementException {
		AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		dataSource.disable();
	}

	public static MeasurementData getData() throws MeasurementException {
		AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		return dataSource.read();
	}

	@BeforeClass
	public static void prepareCollector() {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		ClassA a = new ClassA();
		ClassB b = new ClassB();
		ClassC c = new ClassC();
		ClassD d = new ClassD();
		ClassE e = new ClassE();
		ClassF f = new ClassF();
		ClassG g = new ClassG();
		AbstractDataSource dataSource = CollectorFactory.createDataSource(MemoryDataSource.class.getName(), null);
		AbstractDataSource.setDefaultDataSource(dataSource);
		
		Properties globalProperties = new Properties();
		String currentDir = System.getProperty("user.dir");
		globalProperties.setProperty(ExtensionRegistry.APP_ROOT_DIR_PROPERTY_KEY, currentDir);
		globalProperties.setProperty(ExtensionRegistry.PLUGINS_FOLDER_PROPERTY_KEY, "plugins");
		GlobalConfiguration.initialize(globalProperties);
	}

	@After
	public void revertInstrumentation() throws InstrumentationException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();
	}

	@Test
	public void testMethodScopeInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone()
				.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.getRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testMethodScopeInstrumentationWithByteArrayParameter() throws InstrumentationException,
			MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder.newMethodScopeEntity(ClassH.class.getName() + "*")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassH h = new ClassH();
		h.test(null);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.getRecords(ResponseTimeRecord.class).size());

	}

	@Test
	public void testConstructorScopeInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder.newConstructorScopeEntity(ClassA.class.getName())
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().newConstructorScopeEntity(ClassC.class.getName())
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		ClassC c = new ClassC();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(1, data.getRecords(NanoResponseTimeRecord.class).size());
	}

	@Test
	public void testAPIScopeInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder.newAPIScopeEntity(TestAPIScope.class.getName())
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassC c = new ClassC();
		c.methodC1();
		c.ifMethodA();
		c.ifMethodB();
		ClassD d = new ClassD();
		d.ifMethodA();
		d.ifMethodB();
		ClassE e = new ClassE();
		e.ifMethodA();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(4, data.getRecords(ResponseTimeRecord.class).size());
	}

	@Test
	public void testFullTraceScopeInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder.newTraceScopeEntity()
				.setMethodSubScope(ClassF.class.getName() + ".methodF1()").addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);

		ClassF f = new ClassF();

		f.methodF1();

		enableMeasurement();
		f = new ClassF();
		f.methodF1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(6, data.getRecords(ResponseTimeRecord.class).size());

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		f = new ClassF();

		f.methodF1();

		enableMeasurement();
		f = new ClassF();
		f.methodF1();
		disableMeasurement();
		data = getData();
		Assert.assertTrue(data.getRecords().isEmpty());
	}

	@Test
	public void testIntersectingFullTraceInstrumentationAtOnce() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));

		ClassF f = new ClassF();
		ClassG g = new ClassG();
		g.methodG1();
		f.methodF1();

		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder.newTraceScopeEntity()
				.setMethodSubScope(ClassG.class.getName() + ".methodG1()").addProbe(NanoResponsetimeProbe.MODEL_PROBE)
				.entityDone().newTraceScopeEntity().setMethodSubScope(ClassF.class.getName() + ".methodF1()")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);

		f.methodF1();
		g.methodG1();
		enableMeasurement();
		f.methodF1();
		g.methodG1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(9, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(7, data.getRecords(NanoResponseTimeRecord.class).size());

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		enableMeasurement();
		f.methodF1();
		g.methodG1();
		disableMeasurement();
		data = getData();
		Assert.assertTrue(data.getRecords().isEmpty());
	}

	@Test
	public void testIntersectingFullTraceInstrumentationStepwise() throws InstrumentationException,
			MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder.newTraceScopeEntity()
				.setMethodSubScope(ClassF.class.getName() + ".methodF1()").addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);

		ClassF f = new ClassF();
		ClassG g = new ClassG();
		g.methodG1();
		f.methodF1();

		enableMeasurement();
		f.methodF1();
		g.methodG1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(9, data.getRecords(ResponseTimeRecord.class).size());
		idBuilder = new InstrumentationDescriptionBuilder();
		descr = idBuilder.newTraceScopeEntity().setMethodSubScope(ClassG.class.getName() + ".methodG1()")
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);

		f.methodF1();
		g.methodG1();
		enableMeasurement();
		f.methodF1();
		g.methodG1();
		disableMeasurement();
		data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(9, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(7, data.getRecords(NanoResponseTimeRecord.class).size());

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		enableMeasurement();
		f.methodF1();
		g.methodG1();
		disableMeasurement();
		data = getData();
		Assert.assertTrue(data.getRecords().isEmpty());
	}

	@Test
	public void testFullTraceWithMethodScopeInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder.newMethodScopeEntity(ClassD.class.getName() + ".ifMethodB()")
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassF f = new ClassF();

		f.methodF1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.getRecords(NanoResponseTimeRecord.class).size());

		idBuilder = new InstrumentationDescriptionBuilder();
		descr = idBuilder.newTraceScopeEntity().setMethodSubScope(ClassF.class.getName() + ".methodF1()")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);

		f = new ClassF();

		f.methodF1();

		enableMeasurement();
		f = new ClassF();
		f.methodF1();
		disableMeasurement();
		data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(6, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(1, data.getRecords(NanoResponseTimeRecord.class).size());
	}

	@Test
	public void testUndoInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone()
				.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.getRecords(NanoResponseTimeRecord.class).size());

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		enableMeasurement();
		a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		disableMeasurement();
		data = getData();
		Assert.assertTrue(data.getRecords().isEmpty());
	}

	@Test
	public void testGlobalExclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newGlobalRestriction()
				.excludePackage(ClassA.class.getName() + "")
				.restrictionDone()
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone()
				.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.getRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testGlobalInclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newGlobalRestriction()
				.includePackage(ClassA.class.getName() + "")
				.restrictionDone()
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone()
				.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.getRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testGlobalModifierInclusion() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newGlobalRestriction()
				.includeModifier(Modifier.PRIVATE)
				.restrictionDone()
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone()
				.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(2, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(2, data.getRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testLocalExclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE)
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE)
				.newLocalRestriction()

				.excludePackage(ClassA.class.getName() + "")
				.restrictionDone()
				.addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone()
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(6, data.getRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testLocalInclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE)
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE)
				.newLocalRestriction()

				.includePackage(ClassA.class.getName() + "")
				.restrictionDone()
				.addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone()
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(6, data.getRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testLocalModifierInclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE)
				.newLocalRestriction()
				.includeModifier(Modifier.PUBLIC)
				.restrictionDone()
				
				.entityDone()
				
				
				
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)").newLocalRestriction()
				.includeModifier(Modifier.PRIVATE).restrictionDone().addProbe(NanoResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(4, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(2, data.getRecords(NanoResponseTimeRecord.class).size());

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		idBuilder = new InstrumentationDescriptionBuilder();
		descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE)
				.newLocalRestriction()

				.includeModifier(Modifier.PUBLIC)
				.includeModifier(Modifier.SYNCHRONIZED)
				.restrictionDone()
				.addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone()
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)",
						ClassB.class.getName() + ".methodB1()",
						ClassB.class.getName() + ".methodB2(java.lang.Integer)",
						ClassB.class.getName() + ".methodB3(java.lang.Integer)").newLocalRestriction()
				.includeModifier(Modifier.PRIVATE).restrictionDone().addProbe(NanoResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.getRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(2, data.getRecords(NanoResponseTimeRecord.class).size());
	}

}
