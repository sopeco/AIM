package org.aim.ui.interfaces;

/**
 * Interface to listen to connection state changes.
 * 
 * @author Marius Oehler
 *
 */
public interface ConnectionStateListener {

	/**
	 * Will be invoked when the client is connected.
	 */
	void onConnection();

	/**
	 * Will be invoked when the client is disconnected.
	 */
	void onDisconnection();

}
