package com.neuronrobotics.sdk.common;

public interface ISynchronousDatagramListener {
	
	/**
	 * On sync inconimg packet
	 *
	 * @param data the data
	 */
	public BowlerDatagram onSyncReceive(BowlerDatagram data);
}
