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
package org.aim.description.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationEntity;
import org.aim.aiminterface.description.measurementprobe.MeasurementProbe;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.description.scope.Scope;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * {@link JsonSerializer} to deserialize {@link InstrumentationEntity
 * InstrumentationEntities}.
 * 
 * @author Henning Schulz
 * 
 */
public class InstrumentationEntityDeserializer extends JsonDeserializer<InstrumentationEntity> {

	@SuppressWarnings({ "unchecked" })
	@Override
	public InstrumentationEntity deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectCodec oc = parser.getCodec();
		final JsonNode node = oc.readTree(parser);

		final Class<? extends Scope> scopeClass = mapper.treeToValue(node.get("scopeClass"), Class.class);
		final Scope scope = mapper.treeToValue(node.get("scope"), scopeClass);
		final InstrumentationEntity entity = new InstrumentationEntity(scope);

		final Restriction restriction = mapper.treeToValue(node.get("localRestriction"), Restriction.class);
		entity.setLocalRestriction(restriction);

		final MeasurementProbe[] probesAsArray = mapper.treeToValue(node.get("probesAsArray"), MeasurementProbe[].class);
		final Set<MeasurementProbe> probes = new HashSet<>();
		for (final MeasurementProbe mp : probesAsArray) {
			probes.add(mp);
		}
		entity.getProbes().addAll(probes);

		return entity;
	}

}
