package com.neuronrobotics.sdk.common;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IConnectionEvent events.
 * The class that is interested in processing a IConnectionEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIConnectionEventListener  method. When
 * the IConnectionEvent event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IConnectionEventListener {
	
	/**
	 * Called on the event of a connection object disconnect.
	 *
	 * @param source the source
	 */
	public void onDisconnect(BowlerAbstractConnection source);
	
	/**
	 * called on the event of a connection object connect.
	 *
	 * @param source the source
	 */
	public void onConnect(BowlerAbstractConnection source);
}
