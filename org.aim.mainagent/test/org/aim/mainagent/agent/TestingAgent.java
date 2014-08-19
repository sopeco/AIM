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
package org.aim.mainagent.agent;

import java.lang.instrument.Instrumentation;

import org.aim.mainagent.instrumentor.JInstrumentation;

public class TestingAgent {
	/**
	 * Main method for the agent. Initializes the agent. This method is called,
	 * when the agent is started with a java application as argument.
	 * 
	 * @param agentArgs
	 *            arguments for the agent. Valid arguments: port=<PORT> (port
	 *            where to bind the agent REST service), prefix=<PREFIX> (prefix
	 *            of the agent REST service), collector=<COLLECTORTYPE> (type of
	 *            the data collector (default: file)), any <KEY>=<VALUE> pair
	 *            which is passed as a Property entry
	 * @param inst
	 *            Java instrumentation instance
	 */
	public static void premain(String agentArgs, Instrumentation inst) {
		agentmain(agentArgs, inst);
	}

	/**
	 * Main method for the agent. Initializes the agent. This method is called,
	 * when the agent is loaded into a JVM at runtime.
	 * 
	 * @param agentArgs
	 *            arguments for the agent. Valid arguments: port=<PORT> (port
	 *            where to bind the agent REST service), prefix=<PREFIX> (prefix
	 *            of the agent REST service), collector=<COLLECTORTYPE> (type of
	 *            the data collector (default: file)), logging=<TRUE>/<FALSE>
	 *            (enable/disable logging), any <KEY>=<VALUE> pair which is
	 *            passed as a Property entry
	 * @param inst
	 *            Java instrumentation instance
	 */
	public static void agentmain(String agentArgs, Instrumentation inst) {
		try {
			if (!inst.isRedefineClassesSupported()) {
				throw new IllegalStateException(
						"Redefining classes not supported, InstrumentationAgent cannot work properly!");
			}
			JInstrumentation.getInstance().setjInstrumentation(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
