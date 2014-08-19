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
package org.aim.mainagent.probes.builder;

import junit.framework.Assert;

import org.junit.Test;
import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.IDataCollector;
import org.aim.artifacts.records.NanoResponseTimeRecord;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.mainagent.TraceInstrumentor;
import org.aim.mainagent.builder.MultiSnippet;
import org.aim.mainagent.builder.SnippetProvider;
import org.aim.mainagent.probes.IncrementalInstrumentationProbe;

public class SnippetProviderTest {

	public static final String GENERIC_SNIPPET_CONTROL_SEQUENCE = "_GenericProbe_collector = "
			+ AbstractDataSource.class.getName() + ".getDefaultDataSource();";

	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_1 = "System.out.println(\"BeforeControlSequence\");";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_2 = "_DummyProbe_record = new "
			+ ResponseTimeRecord.class.getName() + "();";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_3 = "_DummyProbe_NanoResponseTimeRecordVariable = new "
			+ NanoResponseTimeRecord.class.getName() + "();";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_4 = "_DummyProbe_record.setOperation(__methodSignature);";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_5 = "_DummyProbe_NanoResponseTimeRecordVariable.setTimeStamp(_GenericProbe_startTime);";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_6 = "System.out.println(\"AfetControlSequence\");";

	public static final String INVALID_DUMMY_SNIPPET_CONTROL_SEQUENCE_1 = "System.out.println(testVariable);";
	public static final String INVALID_DUMMY_SNIPPET_CONTROL_SEQUENCE_2 = "System.out.println(_InvalidDummyProbe_testVariable_2);";

	public static final int GENERIC_SNIPPET_NUM_VARIABLES = 3;
	public static final int DUMMY_SNIPPET_NUM_VARIABLES = 3;
	public static final int INVALID_DUMMY_SNIPPET_NUM_VARIABLES = 2;

	@Test
	public void testGenericSnippet() throws InstrumentationException {
		MultiSnippet snippet_1 = SnippetProvider.getInstance().getGenericSnippet();
		MultiSnippet snippet_2 = SnippetProvider.getInstance().getGenericSnippet();
		Assert.assertEquals(snippet_1, snippet_2);
		Assert.assertTrue(snippet_1.getBeforePart("test").contains(GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertTrue(snippet_1.getAfterPart("test").isEmpty());
		Assert.assertEquals(snippet_1.getVariables().size(), GENERIC_SNIPPET_NUM_VARIABLES);
		Assert.assertEquals(snippet_1.getVariables().get("_GenericProbe_startTime"), long.class);
		Assert.assertEquals(snippet_1.getVariables().get("_GenericProbe_callId"), long.class);
		Assert.assertEquals(snippet_1.getVariables().get("_GenericProbe_collector"), IDataCollector.class);

	}

	@Test
	public void testDummySnippet() throws InstrumentationException {
		MultiSnippet snippet = SnippetProvider.getInstance().getSnippet(DummyProbe.class);
		Assert.assertTrue(snippet.getBeforePart("test").contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertTrue(snippet.getBeforePart("test").contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertTrue(snippet.getBeforePart("test").contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_3));
		Assert.assertTrue(snippet.getBeforePart("test").contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_4));
		Assert.assertTrue(snippet.getBeforePart("test").contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_5));
		Assert.assertTrue(snippet.getAfterPart("test").contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_6));

		Assert.assertEquals(snippet.getVariables().size(), DUMMY_SNIPPET_NUM_VARIABLES);
		Assert.assertEquals(snippet.getVariables().get("_DummyProbe_testVariable"), int.class);
		Assert.assertEquals(snippet.getVariables().get("_DummyProbe_record"), ResponseTimeRecord.class);
		Assert.assertEquals(snippet.getVariables().get("_DummyProbe_NanoResponseTimeRecordVariable"),
				NanoResponseTimeRecord.class);
	}

	@Test
	public void testConditionalSnippet() throws InstrumentationException {
		MultiSnippet snippet = SnippetProvider.getInstance().getSnippet(DummyProbe2.class);
		Assert.assertTrue(snippet.getBeforePart("mymethodReqName").contains("BeforeControlSequence"));
		Assert.assertFalse(snippet.getBeforePart("mymethodReqName").contains("AnotherControlSequence"));
		Assert.assertTrue(snippet.getBeforePart("myotherMethodName").contains("AnotherControlSequence"));
		Assert.assertFalse(snippet.getBeforePart("myotherMethodName").contains("BeforeControlSequence"));
		Assert.assertTrue(snippet.getBeforePart("invalidMethodName").isEmpty());

		Assert.assertTrue(snippet.getAfterPart("mymethodReqName").contains("AfterControlSequence"));
		Assert.assertFalse(snippet.getAfterPart("mymethodReqName").contains("AfterSecondControlSequence"));
		Assert.assertTrue(snippet.getAfterPart("myotherMethodName").contains("AfterSecondControlSequence"));
		Assert.assertFalse(snippet.getAfterPart("myotherMethodName").contains("AfterControlSequence"));
		Assert.assertTrue(snippet.getAfterPart("invalidMethodName").isEmpty());

	}

	@Test
	public void testInvalidProbe() throws InstrumentationException {
		MultiSnippet snippet = SnippetProvider.getInstance().getSnippet(InvalidDummyProbe.class);
		Assert.assertFalse(snippet.getBeforePart("test").contains(INVALID_DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertTrue(snippet.getAfterPart("test").contains(INVALID_DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertFalse(snippet.getVariables().size() == INVALID_DUMMY_SNIPPET_NUM_VARIABLES);
		Assert.assertEquals(snippet.getVariables().get("testVariable"), String.class);
		Assert.assertFalse(snippet.getVariables().containsKey("_DummyProbe_record"));

	}

	@Test
	public void testIncrementalSnippet() throws InstrumentationException {
		MultiSnippet snippet = SnippetProvider.getInstance().getSnippet(IncrementalInstrumentationProbe.class);

		Assert.assertTrue(snippet.getBeforePart("test").isEmpty());
		Assert.assertTrue(snippet.getAfterPart("test").isEmpty());
		Assert.assertFalse(snippet.getIncrementalPart().isEmpty());
		Assert.assertEquals(0, snippet.getVariables().size());
		Assert.assertTrue(snippet.getIncrementalPart().contains(
				TraceInstrumentor.class.getName() + ".getInstance().instrumentIncrementally"));
		Assert.assertTrue(snippet.getIncrementalPart().contains("__clazz.getClass().getName()"));
		Assert.assertTrue(snippet.getIncrementalPart().contains("__methodSignature"));
		Assert.assertTrue(snippet.getIncrementalPart().contains("__instDescription"));

	}
}
