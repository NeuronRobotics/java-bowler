package com.neuronrobotics.sdk.common;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ISynchronousDatagram events.
 * The class that is interested in processing a ISynchronousDatagram
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addISynchronousDatagramListener  method. When
 * the ISynchronousDatagram event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface ISynchronousDatagramListener {
	
	/**
	 * On sync inconimg packet.
	 *
	 * @param data the data
	 * @return the bowler datagram
	 */
	public BowlerDatagram onSyncReceive(BowlerDatagram data);
}
