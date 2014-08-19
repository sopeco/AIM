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

import org.aim.description.InstrumentationEntity;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * {@link JsonSerializer} to serialize {@link InstrumentationEntity
 * InstrumentationEntities}.
 * 
 * @author Henning Schulz
 * 
 */
public class InstrumentationEntitySerializer extends JsonSerializer<InstrumentationEntity<?>> {

	@Override
	public void serialize(InstrumentationEntity<?> entity, JsonGenerator generator, SerializerProvider provider)
			throws IOException {
		generator.writeStartObject();
		generator.writeObjectField("localRestriction", entity.getLocalRestriction());
		generator.writeObjectField("scope", entity.getScope());
		generator.writeObjectField("scopeClass", entity.getScope().getClass());
		generator.writeObjectField("probesAsArray", entity.getProbes().toArray());
		generator.writeEndObject();
	}

}
