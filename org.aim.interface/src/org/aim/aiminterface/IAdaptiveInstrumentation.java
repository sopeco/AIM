package org.aim.aiminterface;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.measurements.MeasurementData;
import org.aim.aiminterface.entities.results.FlatInstrumentationState;
import org.aim.aiminterface.entities.results.OverheadData;
import org.aim.aiminterface.entities.results.SupportedExtensions;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;

public interface IAdaptiveInstrumentation {

	/**
	 * Instruments the code according to the passed
	 * {@link InstrumentationDescription}.
	 * 
	 * @param description
	 *            describes where and how to instrument the application code
	 * @throws InstrumentationException
	 *             thrown if exception occur during instrumentation
	 */
	void instrument(InstrumentationDescription description) throws InstrumentationException;

	/**
	 * Retrieves the current instrumentation state.
	 * 
	 * @return a flat representation of the internal instrumentation state.
	 */
	FlatInstrumentationState getInstrumentationState();

	/**
	 * Reverts all previous instrumentation steps and resets the application
	 * code to the original state.
	 * 
	 * false
	 * 
	 * @throws InstrumentationException
	 *             thrown if exception occur during uninstrumentation
	 */
	void uninstrument() throws InstrumentationException;

	/**
	 * Retrieves all supported extensions for the extension points of the
	 * instrumentation description.
	 * 
	 * @return an object wrapping all supported extensions
	 */
	SupportedExtensions getSupportedExtensions();

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
	 * Retrieves the state whether monitoring is enabled.
	 * 
	 * @return boolean whether monitoring is enabled
	 */
	boolean isMonitoringEnabled();

	/**
	 * 
	 * @return collected measurement data
	 * @throws MeasurementException
	 *             thrown if data cannot be retrieved
	 */
	MeasurementData getMeasurementData() throws MeasurementException;

	/**
	 * Measures the overhead of the given probe type.
	 * 
	 * @param probeType
	 *            type of the probe
	 * @return overhead information.
	 */
	OverheadData measureProbeOverhead(String probeType);

	/**
	 * 
	 * @return the current local timestamp of the specific controller
	 */
	long getCurrentTime();

	/**
	 * 
	 * @return true if connecting to service possible
	 */
	boolean testConnection();

}