package org.aim.mainagent;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.results.FlatInstrumentationState;
import org.aim.aiminterface.entities.results.SupportedExtensions;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;

public interface AdaptiveInstrumentationFacadeMXBean {

	/**
	 * Instruments the target application according to the passed
	 * instrumentation. Note, instrumentation is additive, thus, calling this
	 * method several times with different instrumentation descriptions results
	 * in a joined instrumentation.
	 * 
	 * @param instrumentationDescription
	 *            describes the instrumentation to be made
	 * @throws InstrumentationException
	 *             if instrumentation fails
	 */
	void instrument(InstrumentationDescription instrumentationDescription) throws InstrumentationException;

	/**
	 * Reverts all instrumentation steps.
	 * 
	 * @throws InstrumentationException
	 *             if instrumentation fails
	 */
	void undoInstrumentation();

	/**
	 * Retrieves the flat instrumentation state.
	 * 
	 * @return flat instrumentation state
	 */
	FlatInstrumentationState getInstrumentationState();

	/**
	 * retrieve supported extensions.
	 * 
	 * @return Object wrapping all extensions supported by this agent.
	 * @throws InstrumentationException
	 *             thrown if extensions cannot be retrieved
	 */
	SupportedExtensions getSupportedExtensions() throws InstrumentationException;
	
	/**
	 * Enables monitoring or measurement data collection.
	 * 
	 * @throws MeasurementException
	 *             thrown if monitoring fails
	 */
	void enableMonitoring() throws MeasurementException;

	/**
	 * Disables monitoring or measurement data collection.
	 * 
	 * @throws MeasurementException
	 *             thrown if monitoring fails
	 */
	void disableMonitoring() throws MeasurementException;
	
	/**
	 * 
	 * @return collected measurement data
	 * @throws MeasurementException
	 *             thrown if data cannot be retrieved
	 */
	byte[] getMeasurementData() throws MeasurementException;
}