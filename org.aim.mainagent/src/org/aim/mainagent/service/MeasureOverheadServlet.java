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
package org.aim.mainagent.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.aim.api.instrumentation.entities.OverheadData;
import org.aim.api.instrumentation.entities.OverheadRecord;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.overhead.OverheadEstimator;

/**
 * Measures probe overhead for given probe.
 * 
 * @author Alexander Wert
 * 
 */
public class MeasureOverheadServlet implements Service {

	@Override
	public void doService(Request req, Response resp) throws Exception {

		BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
		String json = "";
		if (br != null) {
			json = br.readLine();
		}

		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);

		String probeClassName = json;
		OverheadData oData = new OverheadData();

		List<OverheadRecord> records = OverheadEstimator.measureOverhead(probeClassName);
		oData.setoRecords(records);
		resp.setContentType("application/json");
		mapper.writeValue(resp.getOutputStream(), oData);
	}

}
