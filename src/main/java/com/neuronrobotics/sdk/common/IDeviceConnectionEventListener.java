package com.neuronrobotics.sdk.common;

public interface IDeviceConnectionEventListener {
	/**
	 * Called on the event of a connection object disconnect
	 */
	public void onDisconnect(BowlerAbstractDevice source);
	/**
	 * called on the event of a connection object connect
	 */
	public void onConnect(BowlerAbstractDevice source);
}
