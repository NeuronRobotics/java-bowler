package com.neuronrobotics.sdk.common;

public interface IConnectionEventListener {
	/**
	 * Called on the event of a connection object disconnect
	 */
	public void onDisconnect();
	/**
	 * called on the event of a connection object connect
	 */
	public void onConnect();
}
