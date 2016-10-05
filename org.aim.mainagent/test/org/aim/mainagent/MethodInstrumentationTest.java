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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.measurements.AbstractRecord;
import org.aim.aiminterface.entities.measurements.MeasurementData;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.artifacts.probes.NanoResponsetimeProbe;
import org.aim.artifacts.probes.ResponsetimeProbe;
import org.aim.artifacts.records.NanoResponseTimeRecord;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.mainagent.instrumentation.JInstrumentation;
import org.codehaus.jackson.map.ObjectMapper;
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
import org.test.sut.ClassI;
import org.test.sut.ClassK;

import junit.framework.Assert;

public class MethodInstrumentationTest {

	public static void enableMeasurement() throws MeasurementException {
		final AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		dataSource.enable();
	}

	public static void disableMeasurement() throws MeasurementException {
		final AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		dataSource.disable();
	}

	public static MeasurementData getData() throws MeasurementException {
		final AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		return dataSource.read();
	}

	@BeforeClass
	public static void prepareCollector() {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final ClassA a = new ClassA();
		final ClassB b = new ClassB();
		final ClassC c = new ClassC();
		final ClassD d = new ClassD();
		final ClassE e = new ClassE();
		final ClassF f = new ClassF();
		final ClassG g = new ClassG();
		final ClassK k = new ClassK();
		final AbstractDataSource dataSource = CollectorFactory.createDataSource(MemoryDataSource.class.getName(), null);
		AbstractDataSource.setDefaultDataSource(dataSource);
		
		final Properties globalProperties = new Properties();
		final String currentDir = System.getProperty("user.dir");
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
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone()
				.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		final ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.selectRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testLargeScaleRTMeasurements() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()",
						ClassA.class.getName() + ".methodA2(java.lang.Integer)",
						ClassA.class.getName() + ".methodA3(java.lang.Integer)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone()
				.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		final ClassA a = new ClassA();
		for (int i=0; i < 200000; i++) {
			a.methodA1();
		}
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(200001, data.selectRecords(ResponseTimeRecord.class).size());

	}
	
	@Test
	public void testMethodScopeInstrumentationWithByteArrayParameter() throws InstrumentationException,
			MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder.newMethodScopeEntity(ClassH.class.getName() + "*")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		final ClassH h = new ClassH();
		h.test(null);
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.selectRecords(ResponseTimeRecord.class).size());

	}

	@Test
	public void testConstructorScopeInstrumentation() throws InstrumentationException, MeasurementException, Exception {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder.newConstructorScopeEntity(ClassA.class.getName())
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().newConstructorScopeEntity(ClassC.class.getName())
				.addProbe(NanoResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		final ClassA a = new ClassA();
		final ClassC c = new ClassC();
		disableMeasurement();
		final MeasurementData data = getMeasurementData(AdaptiveInstrumentationFacade.getInstance().getMeasurementData());
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(1, data.selectRecords(NanoResponseTimeRecord.class).size());
	}
	
	private MeasurementData getMeasurementData(final List<String> jsondata) throws MeasurementException {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final LinkedList<AbstractRecord> records = new LinkedList<>();
			for (final String jsonRecord : jsondata) {
				records.add(mapper.readValue(jsonRecord, AbstractRecord.class));
			}
			return new MeasurementData(records);
		} catch (final IOException e) {
			throw new MeasurementException(e);
		}		
	}

	@Test
	public void testAPIScopeInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder.newAPIScopeEntity(TestAPIScope.class.getName())
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		final ClassC c = new ClassC();
		c.methodC1();
		c.ifMethodA();
		c.ifMethodB();
		final ClassD d = new ClassD();
		d.ifMethodA();
		d.ifMethodB();
		final ClassE e = new ClassE();
		e.ifMethodA();
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(4, data.selectRecords(ResponseTimeRecord.class).size());
	}

	@Test
	public void testFullTraceScopeInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassF.class.getName() + ".methodF1()").enableTrace().addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();
		
		ClassF f = new ClassF();
		AdaptiveInstrumentationFacade.getInstance().instrument(descr);

		

		f.methodF1();

		enableMeasurement();
		f = new ClassF();
		f.methodF1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(6, data.selectRecords(ResponseTimeRecord.class).size());

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
	public void testFullTraceScopeWithStaticMethodsInstrumentation2() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder.
				newMethodScopeEntity(ClassI.class.getName() + ".methodI1()").enableTrace().addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();

		

		ClassI i = new ClassI();

		ClassI.methodI2();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		
		enableMeasurement();
		i = new ClassI();
		i.methodI1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(2, data.selectRecords(ResponseTimeRecord.class).size());

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		
		enableMeasurement();
		i.methodI1();
		disableMeasurement();
		data = getData();
		Assert.assertTrue(data.getRecords().isEmpty());
	}

	
	@Test
	public void testFullTraceScopeWithStaticMethodsInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder.
				newMethodScopeEntity(ClassI.class.getName() + ".methodI1()").enableTrace().addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();

		

		ClassI i = new ClassI();

		i.methodI1();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		
		enableMeasurement();
		i = new ClassI();
		i.methodI1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(2, data.selectRecords(ResponseTimeRecord.class).size());

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		
		enableMeasurement();
		i.methodI1();
		disableMeasurement();
		data = getData();
		Assert.assertTrue(data.getRecords().isEmpty());
	}

	@Test
	public void testIntersectingFullTraceInstrumentationAtOnce() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));

		final ClassF f = new ClassF();
		final ClassG g = new ClassG();
		g.methodG1();
		f.methodF1();

		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassG.class.getName() + ".methodG1()").enableTrace().addProbe(NanoResponsetimeProbe.MODEL_PROBE)
				.entityDone().newMethodScopeEntity(ClassF.class.getName() + ".methodF1()").enableTrace()
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
		Assert.assertEquals(9, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(7, data.selectRecords(NanoResponseTimeRecord.class).size());

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
		InstrumentationDescription descr = idBuilder.
				newMethodScopeEntity(ClassF.class.getName() + ".methodF1()").enableTrace().addProbe(ResponsetimeProbe.MODEL_PROBE)
				.entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);

		final ClassF f = new ClassF();
		final ClassG g = new ClassG();
		g.methodG1();
		f.methodF1();

		enableMeasurement();
		f.methodF1();
		g.methodG1();
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(9, data.selectRecords(ResponseTimeRecord.class).size());
		idBuilder = new InstrumentationDescriptionBuilder();
		descr = idBuilder.newMethodScopeEntity(ClassG.class.getName() + ".methodG1()").enableTrace()
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
		Assert.assertEquals(9, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(7, data.selectRecords(NanoResponseTimeRecord.class).size());

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
		Assert.assertEquals(1, data.selectRecords(NanoResponseTimeRecord.class).size());

		idBuilder = new InstrumentationDescriptionBuilder();
		descr = idBuilder.newMethodScopeEntity(ClassF.class.getName() + ".methodF1()").enableTrace()
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
		Assert.assertEquals(6, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(1, data.selectRecords(NanoResponseTimeRecord.class).size());
	}

	@Test
	public void testUndoInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
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
		Assert.assertEquals(3, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.selectRecords(NanoResponseTimeRecord.class).size());

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
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
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
		final ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		final ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.selectRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testGlobalInclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
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
		final ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		final ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(3, data.selectRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testGlobalModifierInclusion() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
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
		final ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		final ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(2, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(2, data.selectRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testLocalExclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
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
		final ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		final ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(6, data.selectRecords(NanoResponseTimeRecord.class).size());

	}

	@Test
	public void testLocalInclusions() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
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
		final ClassA a = new ClassA();
		a.methodA1();
		a.methodA2(1);
		final ClassB b = new ClassB();
		b.methodB1();
		b.methodB2(1);
		disableMeasurement();
		final MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(3, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(6, data.selectRecords(NanoResponseTimeRecord.class).size());

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
		Assert.assertEquals(4, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(2, data.selectRecords(NanoResponseTimeRecord.class).size());

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
		Assert.assertEquals(1, data.selectRecords(ResponseTimeRecord.class).size());
		Assert.assertEquals(2, data.selectRecords(NanoResponseTimeRecord.class).size());
	}
	
	@Test
	public void testReturnMethodInstrumentation() throws InstrumentationException, MeasurementException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder
				.newMethodScopeEntity(ClassK.class.getName() + ".returnMethod(boolean)")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		final ClassK k = new ClassK();
		k.returnMethod(false);
		disableMeasurement();
		MeasurementData data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.selectRecords(ResponseTimeRecord.class).size());
		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();
		k.returnMethod(true);
		disableMeasurement();
		data = getData();
		Assert.assertFalse(data.getRecords().isEmpty());
		Assert.assertEquals(1, data.selectRecords(ResponseTimeRecord.class).size());
		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();
	}

}
