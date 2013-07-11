package com.neuronrobotics.sdk.common;

public interface ISynchronousDatagramListener {
	
	/**
	 * On sync inconimg packet
	 *
	 * @param data the data
	 */
	public void onSyncReceive(BowlerDatagram data);
}
