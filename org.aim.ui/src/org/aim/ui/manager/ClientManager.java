package org.aim.ui.manager;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.dataset.Parameter;
import org.aim.api.measurement.utils.RecordCSVWriter;
import org.aim.artifacts.instrumentation.InstrumentationClient;
import org.aim.description.InstrumentationDescription;
import org.aim.ui.Main;
import org.aim.ui.interfaces.ConnectionStateListener;
import org.aim.ui.view.MainView;
import org.aim.ui.view.MainView.ClientSettingsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Manages the connection to the SUT.
 * 
 * @author Marius Oehler
 *
 */
public final class ClientManager {

	private static ClientManager instance;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

	/**
	 * Returns an instance of this class. This object is the only instance
	 * (singleton) of this class.
	 * 
	 * @return instance of {@link ClientManager}
	 */
	public static ClientManager instance() {
		if (instance == null) {
			instance = new ClientManager();
		}
		return instance;
	}

	private InstrumentationClient client;
	private Collection<ConnectionStateListener> csListener;

	private ClientManager() {
		csListener = new ArrayList<ConnectionStateListener>();
	}

	/**
	 * Will be invoked when the UI's connect/disconnect button is pressed.
	 */
	public void actionButton() {
		if (client == null) {
			connect();
		} else {
			disconnect();
		}
	}

	/**
	 * Adds another {@link ConnectionStateListener}.
	 * 
	 * @param listener
	 *            - listener to register
	 */
	public void addConnectionStateListener(ConnectionStateListener listener) {
		csListener.add(listener);
	}

	/**
	 * Provoke the connection buildup to the specified controller.
	 */
	public void connect() {
		MainView.instance().addLogMessage("Connecting..");

		MainView.instance().setClientSettingsState(ClientSettingsState.CONNECTING);

		final String host = MainView.instance().getInputHost().getSelectedItem().toString();
		final String port = MainView.instance().getInputPort().getText();

		// validate input
		if (host.isEmpty()) {
			MainView.instance().addLogMessage("The host is not specified");
			MainView.instance().setClientSettingsState(ClientSettingsState.DEFAULT);
		} else if (port.isEmpty()) {
			MainView.instance().addLogMessage("The port is not specified");
			MainView.instance().setClientSettingsState(ClientSettingsState.DEFAULT);
		} else if (!port.matches("^\\d+")) {
			MainView.instance().addLogMessage("'" + port + "' is not a valid port value..");
			MainView.instance().setClientSettingsState(ClientSettingsState.DEFAULT);
		} else {
			LOGGER.debug("Connecting to {}:{}", host, port);

			if (!InstrumentationClient.testConnection(host, port)) {
				MainView.instance().addLogMessage("Can't establish connection to " + host + ":" + port + "");
				LOGGER.debug("Can't establish connection to {}:{}", host, port);

				MainView.instance().setClientSettingsState(ClientSettingsState.DEFAULT);
			} else {
				MainView.instance().addLogMessage("Connection establish to " + host + ":" + port + "");
				LOGGER.debug("Client connected to {}:{}", host, port);
				client = new InstrumentationClient(host, port);

				MainView.instance().setClientSettingsState(ClientSettingsState.CONNECTED);

				connected();
			}
		}
	}

	private void connected() {
		for (ConnectionStateListener l : csListener) {
			l.onConnection();
		}
	}

	/**
	 * Disconnect the client from the agent.
	 */
	private void disconnect() {
		LOGGER.debug("Disconnecting..");
		MainView.instance().setClientSettingsState(ClientSettingsState.DEFAULT);
		MainView.instance().addLogMessage("Connection reversed");
		if (client != null) {
			client = null;
		}
		disconnected();
	}

	private void disconnected() {
		for (ConnectionStateListener l : csListener) {
			l.onDisconnection();
		}
	}

	/**
	 * Downloads the agent's dataset into the specified directory.
	 * 
	 * @param targetDirectory
	 *            target directory of the file
	 */
	public void downloadDataset(final File targetDirectory) {

		try {
			final PipedOutputStream outPipe = new PipedOutputStream();
			PipedInputStream inPipe = new PipedInputStream(outPipe);

			Main.getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					try {
						client.pipeToOutputStream(outPipe);
					} catch (MeasurementException e) {
						e.printStackTrace();
					}
				}
			});
			RecordCSVWriter.getInstance().pipeDataToDatasetFiles(inPipe, targetDirectory.getAbsolutePath(),
					new HashSet<Parameter>());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Returns the a list of api scopes (classes) which are supported by the
	 * connected agent.
	 * 
	 * @return api scopes supported by the connected agent
	 */
	public List<String> getApiScopes() {
		return client.getSupportedExtensions().getApiScopeExtensions();
	}

	/**
	 * Returns the a list of custom scopes (classes) which are supported by the
	 * connected agent.
	 * 
	 * @return custom scopes supported by the connected agent
	 */
	public List<String> getCustomScopes() {
		return client.getSupportedExtensions().getCustomScopeExtensions();
	}

	/**
	 * Returns whether monitoring is enabled.
	 * 
	 * @return monitoring state
	 */
	public boolean isMonitoringEnabled() {
		return client.isMonitoringEnabled();
	}

	/**
	 * Returns the a list of probes (classes) which are supported by the
	 * connected agent.
	 * 
	 * @return probes supported by the connected agent
	 */
	public List<String> getProbes() {
		List<String> probes = new ArrayList<String>(client.getSupportedExtensions().getProbeExtensions());
		return probes;
	}

	/**
	 * Returns the a list of probes (classes) and the corresponding scopes which
	 * are supported by the connected agent.
	 * 
	 * @return probes supported by the connected agent
	 */
	public Map<String, Set<String>> getProbeMapping() {
		Map<String, Set<String>> probeExtensionsMapping = client.getSupportedExtensions().getProbeExtensionsMapping();
		return probeExtensionsMapping;
	}

	/**
	 * Returns the a list of sampler (classes) which are supported by the
	 * connected agent.
	 * 
	 * @return sampler supported by the connected agent
	 */
	public List<String> getSampler() {
		return client.getSupportedExtensions().getSamplerExtensions();
	}

	/**
	 * Returns the a list of scopes (classes) which are supported by the
	 * connected agent.
	 * 
	 * @return scopes supported by the connected agent
	 */
	public List<String> getScopes() {
		List<String> scopeExtensions = new ArrayList<String>();
		scopeExtensions.addAll(client.getSupportedExtensions().getApiScopeExtensions());
		scopeExtensions.addAll(client.getSupportedExtensions().getCustomScopeExtensions());
		return scopeExtensions;
	}

	/**
	 * Instrument the target system with the given
	 * {@link InstrumentationDescription}.
	 * 
	 * @param instrumentationDescription
	 *            - description to instrument
	 */
	public void instrument(InstrumentationDescription instrumentationDescription) {
		try {
			client.instrument(instrumentationDescription);
		} catch (InstrumentationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns whether a connection to an agent is established.
	 * 
	 * @return true if a connection is established
	 */
	public boolean isConnected() {
		return client != null;
	}

	/**
	 * Remove the given {@link ConnectionStateListener}.
	 * 
	 * @param listener
	 *            - listener to remove
	 */
	public void removeConnectionStateListener(ConnectionStateListener listener) {
		csListener.remove(listener);
	}

	/**
	 * Enables the monitoring.
	 */
	public void startMonitoring() {
		try {
			client.enableMonitoring();
		} catch (MeasurementException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stops the monitoring.
	 */
	public void stopMonitoring() {
		try {
			client.disableMonitoring();
		} catch (MeasurementException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Uninstrument the target system.
	 */
	public void uninstrument() {
		try {
			client.uninstrument();
		} catch (InstrumentationException e) {
			throw new RuntimeException(e);
		}
	}
}
