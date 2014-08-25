package org.aim.ui.dummy;

import org.aim.artifacts.probes.ResponsetimeProbe;
import org.aim.description.builder.InstrumentationDescriptionBuilder;


public final class DummyUtil {
	private DummyUtil() {
		
		InstrumentationDescriptionBuilder b = new InstrumentationDescriptionBuilder();
		
		b.newMethodScopeEntity("*").addProbe(ResponsetimeProbe.MODEL_PROBE);
		
	}

}
