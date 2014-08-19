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
import org.aim.mainagent.builder.ProbeBuilder;
import org.aim.mainagent.builder.Snippet;

public class ProbeBuilderTest {
	public static final String TEST_METHOD_SIGNATURE = "testSignature(Integer,Object)";

	public static final String GENERIC_SNIPPET_CONTROL_SEQUENCE = "_GenericProbe_collector = "
			+ AbstractDataSource.class.getName() + ".getDefaultDataSource();";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_1 = "System.out.println(\"BeforeControlSequence\");";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_2 = "_DummyProbe_record.setOperation(\""
			+ TEST_METHOD_SIGNATURE + "\");";
	public static final String DUMMY_SNIPPET_CONTROL_SEQUENCE_3 = "System.out.println(\"AfetControlSequence\");";
	public static final String ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_1 = "System.out.println(\"AnotherBeforeControlSequence\");";
	public static final String ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_2 = "_AnotherDummyProbe_record = new "
			+ ResponseTimeRecord.class.getName() + "()";
	public static final String ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_3 = "System.out.println(\"AnotherAfterControlSequence\");";
	public static final int GENERIC_SNIPPET_NUM_VARIABLES = 3;
	public static final int DUMMY_SNIPPET_NUM_VARIABLES = 3;
	public static final int ANOTHER_DUMMY_SNIPPET_NUM_VARIABLES = 2;

	@Test
	public void testProbeBuilder() throws InstrumentationException {
		ProbeBuilder pBuilder = new ProbeBuilder(TEST_METHOD_SIGNATURE);
		pBuilder.inject(DummyProbe.class);
		Snippet snippet_1 = pBuilder.build();

		Assert.assertTrue(snippet_1.getBeforePart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertTrue(snippet_1.getBeforePart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertTrue(snippet_1.getBeforePart().contains(GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertTrue(snippet_1.getAfterPart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		Assert.assertEquals(1, numOccurences(snippet_1.getBeforePart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertEquals(1, numOccurences(snippet_1.getBeforePart(), GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertEquals(1, numOccurences(snippet_1.getAfterPart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		Assert.assertEquals(snippet_1.getVariables().size(), DUMMY_SNIPPET_NUM_VARIABLES
				+ GENERIC_SNIPPET_NUM_VARIABLES);
		Assert.assertEquals(snippet_1.getVariables().get("_DummyProbe_testVariable"), int.class);
		Assert.assertEquals(snippet_1.getVariables().get("_DummyProbe_record"), ResponseTimeRecord.class);
		Assert.assertEquals(snippet_1.getVariables().get("_DummyProbe_NanoResponseTimeRecordVariable"),
				NanoResponseTimeRecord.class);
		Assert.assertEquals(snippet_1.getVariables().get("_GenericProbe_startTime"), long.class);
		Assert.assertEquals(snippet_1.getVariables().get("_GenericProbe_callId"), long.class);
		Assert.assertEquals(snippet_1.getVariables().get("_GenericProbe_collector"), IDataCollector.class);

		Snippet snippet_2 = pBuilder.build();

		Assert.assertEquals(1, numOccurences(snippet_2.getBeforePart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertEquals(1, numOccurences(snippet_2.getBeforePart(), GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertEquals(1, numOccurences(snippet_2.getAfterPart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		pBuilder.inject(DummyProbe.class);
		Snippet snippet_3 = pBuilder.build();

		Assert.assertTrue(snippet_3.getBeforePart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertTrue(snippet_3.getBeforePart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertTrue(snippet_3.getBeforePart().contains(GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertTrue(snippet_3.getAfterPart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		Assert.assertEquals(1, numOccurences(snippet_3.getBeforePart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertEquals(1, numOccurences(snippet_3.getBeforePart(), GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertEquals(1, numOccurences(snippet_3.getAfterPart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		Assert.assertEquals(snippet_3.getVariables().size(), DUMMY_SNIPPET_NUM_VARIABLES
				+ GENERIC_SNIPPET_NUM_VARIABLES);
		Assert.assertEquals(snippet_3.getVariables().get("_DummyProbe_testVariable"), int.class);
		Assert.assertEquals(snippet_3.getVariables().get("_DummyProbe_record"), ResponseTimeRecord.class);
		Assert.assertEquals(snippet_3.getVariables().get("_DummyProbe_NanoResponseTimeRecordVariable"),
				NanoResponseTimeRecord.class);
		Assert.assertEquals(snippet_3.getVariables().get("_GenericProbe_startTime"), long.class);
		Assert.assertEquals(snippet_3.getVariables().get("_GenericProbe_callId"), long.class);
		Assert.assertEquals(snippet_3.getVariables().get("_GenericProbe_collector"), IDataCollector.class);

		pBuilder.inject(AnotherDummyProbe.class);
		Snippet snippet_4 = pBuilder.build();
		Assert.assertTrue(snippet_4.getBeforePart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertTrue(snippet_4.getBeforePart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertTrue(snippet_4.getBeforePart().contains(GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertTrue(snippet_4.getAfterPart().contains(DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		Assert.assertTrue(snippet_4.getBeforePart().contains(ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertTrue(snippet_4.getBeforePart().contains(ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertTrue(snippet_4.getAfterPart().contains(ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		Assert.assertEquals(2, numOccurences(snippet_4.getBeforePart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertEquals(1, numOccurences(snippet_4.getBeforePart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertEquals(1, numOccurences(snippet_4.getBeforePart(), GENERIC_SNIPPET_CONTROL_SEQUENCE));
		Assert.assertEquals(1, numOccurences(snippet_4.getAfterPart(), DUMMY_SNIPPET_CONTROL_SEQUENCE_3));
		Assert.assertEquals(1, numOccurences(snippet_4.getBeforePart(), ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_1));
		Assert.assertEquals(1, numOccurences(snippet_4.getBeforePart(), ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_2));
		Assert.assertEquals(1, numOccurences(snippet_4.getAfterPart(), ANOTHER_DUMMY_SNIPPET_CONTROL_SEQUENCE_3));

		Assert.assertEquals(snippet_4.getVariables().size(), DUMMY_SNIPPET_NUM_VARIABLES
				+ ANOTHER_DUMMY_SNIPPET_NUM_VARIABLES + GENERIC_SNIPPET_NUM_VARIABLES);
		Assert.assertEquals(snippet_4.getVariables().get("_DummyProbe_testVariable"), int.class);
		Assert.assertEquals(snippet_4.getVariables().get("_DummyProbe_record"), ResponseTimeRecord.class);
		Assert.assertEquals(snippet_4.getVariables().get("_DummyProbe_NanoResponseTimeRecordVariable"),
				NanoResponseTimeRecord.class);
		Assert.assertEquals(snippet_4.getVariables().get("_GenericProbe_startTime"), long.class);
		Assert.assertEquals(snippet_4.getVariables().get("_GenericProbe_callId"), long.class);
		Assert.assertEquals(snippet_4.getVariables().get("_GenericProbe_collector"), IDataCollector.class);
		Assert.assertEquals(snippet_4.getVariables().get("_AnotherDummyProbe_testVariable"), int.class);
		Assert.assertEquals(snippet_4.getVariables().get("_AnotherDummyProbe_record"), ResponseTimeRecord.class);

	}

	private int numOccurences(String target, String subject) {
		int counter = 0;
		String regex = subject.replace(".", "\\.");
		regex = regex.replace("(", "\\(");
		regex = regex.replace(")", "\\)");

		while (target.contains(subject)) {
			counter++;
			target = target.replaceFirst(regex, "");
		}
		return counter;
	}
}
