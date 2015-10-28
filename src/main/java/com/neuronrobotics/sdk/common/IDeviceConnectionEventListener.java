package com.neuronrobotics.sdk.common;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IDeviceConnectionEvent events.
 * The class that is interested in processing a IDeviceConnectionEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIDeviceConnectionEventListener  method. When
 * the IDeviceConnectionEvent event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IDeviceConnectionEventListener {
	
	/**
	 * Called on the event of a connection object disconnect.
	 *
	 * @param source the source
	 */
	public void onDisconnect(BowlerAbstractDevice source);
	
	/**
	 * called on the event of a connection object connect.
	 *
	 * @param source the source
	 */
	public void onConnect(BowlerAbstractDevice source);
}
