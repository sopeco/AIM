package org.aim.ui.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aim.artifacts.instrumentation.InstrumentationClient;
import org.aim.ui.interfaces.ConnectionStateListener;
import org.aim.ui.view.MainView;
import org.aim.ui.view.MainView.ClientSettingsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

	private static ClientManager SINGLETON;

	public static ClientManager SINGLETON() {
		if (SINGLETON == null) {
			SINGLETON = new ClientManager();
		}
		return SINGLETON;
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

	public void addConnectionStateListener(ConnectionStateListener listener) {
		csListener.add(listener);
	}

	public void connect() {
		MainView.SINGLETON().addLogMessage("Connecting..");

		MainView.SINGLETON().setClientSettingsState(ClientSettingsState.CONNECTING);

		final String host = MainView.SINGLETON().getInputHost().getSelectedItem().toString();
		final String port = MainView.SINGLETON().getInputPort().getText();

		// validate input
		if (host.isEmpty()) {
			MainView.SINGLETON().addLogMessage("The host is not specified");
			MainView.SINGLETON().setClientSettingsState(ClientSettingsState.DEFAULT);
		} else if (port.isEmpty()) {
			MainView.SINGLETON().addLogMessage("The port is not specified");
			MainView.SINGLETON().setClientSettingsState(ClientSettingsState.DEFAULT);
		} else if (!port.matches("^\\d+")) {
			MainView.SINGLETON().addLogMessage("'" + port + "' is not a valid port value..");
			MainView.SINGLETON().setClientSettingsState(ClientSettingsState.DEFAULT);
		} else {
			LOGGER.debug("Connecting to {}:{}", host, port);

			if (!InstrumentationClient.testConnection(host, port)) {
				MainView.SINGLETON().addLogMessage("Can't establish connection to " + host + ":" + port + "");
				LOGGER.debug("Can't establish connection to {}:{}", host, port);

				MainView.SINGLETON().setClientSettingsState(ClientSettingsState.DEFAULT);
			} else {
				MainView.SINGLETON().addLogMessage("Connection establish to " + host + ":" + port + "");
				LOGGER.debug("Client connected to {}:{}", host, port);
				client = new InstrumentationClient(host, port);

				MainView.SINGLETON().setClientSettingsState(ClientSettingsState.CONNECTED);

				connected();
			}
		}
	}

	/**
	 *
	 */
	private void disconnect() {
		LOGGER.debug("Disconnecting..");
		MainView.SINGLETON().setClientSettingsState(ClientSettingsState.DEFAULT);
		MainView.SINGLETON().addLogMessage("Connection reversed");
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

	private void connected() {
		for (ConnectionStateListener l : csListener) {
			l.onConnection();
		}
	}

	public List<String> getProbes() {
		List<String> probes = client.getSupportedExtensions().getEnclosingProbeExtensions();
		return probes;
	}

	public List<String> getScopes() {
		List<String> scopeExtensions = new ArrayList<String>();
		scopeExtensions.addAll(client.getSupportedExtensions().getApiScopeExtensions());
		scopeExtensions.addAll(client.getSupportedExtensions().getCustomScopeExtensions());
		return scopeExtensions;
	}

	public boolean isConnected() {
		return client != null;
	}

	public void removeConnectionStateListener(ConnectionStateListener listener) {
		csListener.remove(listener);
	}
}
