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
package org.aim.artifacts.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.probes.utils.MemoryMXBeanCache;
import org.aim.artifacts.records.MemoryFootprintRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;

/**
 * This probe collects information on memory footprints.
 * 
 * @author Alexander Wert
 * 
 */
public class MemoryFootprintProbe extends AbstractEnclosingProbe {

	public static final MeasurementProbe<MethodsEnclosingScope> MODEL_PROBE = new MeasurementProbe<>(
			MemoryFootprintProbe.class.getName());

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public MemoryFootprintProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public Long _MemoryFootprintProbe_stopTime;

	@ProbeVariable
	public MemoryFootprintRecord _MemoryFootprintProbe_record;

	@ProbeVariable
	public Runtime _MemoryFootprintProbe_runtime;

	@ProbeVariable
	public Long _MemoryFootprintProbe_beforeMemory;

	@ProbeVariable
	public Long _MemoryFootprintProbe_beforeEden;

	@ProbeVariable
	public Long _MemoryFootprintProbe_beforeSurvivor;

	@ProbeVariable
	public Long _MemoryFootprintProbe_beforeTenured;

	@ProbeVariable
	public Long _MemoryFootprintProbe_afterMemory;

	@ProbeVariable
	public Long _MemoryFootprintProbe_afterEden;

	@ProbeVariable
	public Long _MemoryFootprintProbe_afterSurvivor;

	@ProbeVariable
	public Long _MemoryFootprintProbe_afterTenured;

	/**
	 * Before part.
	 */
	@ProbeBeforePart
	public void beforePart() {
		_MemoryFootprintProbe_runtime = java.lang.Runtime.getRuntime();

		_MemoryFootprintProbe_beforeMemory = _MemoryFootprintProbe_runtime.totalMemory()
				- _MemoryFootprintProbe_runtime.freeMemory();

		_MemoryFootprintProbe_beforeEden = MemoryMXBeanCache.getInstance().getEdenBean().getUsage().getUsed();
		_MemoryFootprintProbe_beforeSurvivor = MemoryMXBeanCache.getInstance().getSurvivorBean().getUsage().getUsed();
		_MemoryFootprintProbe_beforeTenured = MemoryMXBeanCache.getInstance().getOldBean().getUsage().getUsed();
	}
	/**
	 * After part.
	 */
	@ProbeAfterPart
	public void afterPart() {
		_MemoryFootprintProbe_stopTime = System.currentTimeMillis();

		_MemoryFootprintProbe_afterMemory = _MemoryFootprintProbe_runtime.totalMemory()
				- _MemoryFootprintProbe_runtime.freeMemory();

		_MemoryFootprintProbe_afterEden = MemoryMXBeanCache.getInstance().getEdenBean().getUsage().getUsed();
		_MemoryFootprintProbe_afterSurvivor = MemoryMXBeanCache.getInstance().getSurvivorBean().getUsage().getUsed();
		_MemoryFootprintProbe_afterTenured = MemoryMXBeanCache.getInstance().getOldBean().getUsage().getUsed();

		_MemoryFootprintProbe_record = new MemoryFootprintRecord();
		_MemoryFootprintProbe_record.setCallId(_GenericProbe_callId);
		_MemoryFootprintProbe_record.setOperation(__methodSignature);
		_MemoryFootprintProbe_record.setEndTimestamp(_MemoryFootprintProbe_stopTime);
		_MemoryFootprintProbe_record.setTimeStamp(_GenericProbe_startTime);
		_MemoryFootprintProbe_record.setMemoryUsedBefore(_MemoryFootprintProbe_beforeMemory);
		_MemoryFootprintProbe_record.setEdenSpaceUsedBefore(_MemoryFootprintProbe_beforeEden);
		_MemoryFootprintProbe_record.setSurvivorSpaceUsedBefore(_MemoryFootprintProbe_beforeSurvivor);
		_MemoryFootprintProbe_record.setTenuredSpaceUsedBefore(_MemoryFootprintProbe_beforeTenured);
		_MemoryFootprintProbe_record.setMemoryUsedAfter(_MemoryFootprintProbe_afterMemory);
		_MemoryFootprintProbe_record.setEdenSpaceUsedAfter(_MemoryFootprintProbe_afterEden);
		_MemoryFootprintProbe_record.setSurvivorSpaceUsedAfter(_MemoryFootprintProbe_afterSurvivor);
		_MemoryFootprintProbe_record.setTenuredSpaceUsedAfter(_MemoryFootprintProbe_afterTenured);
		_GenericProbe_collector.newRecord(_MemoryFootprintProbe_record);
	}

}
