package com.neuronrobotics.sdk.common;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ithreadedTimout events.
 * The class that is interested in processing a ithreadedTimout
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIthreadedTimoutListener  method. When
 * the ithreadedTimout event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IthreadedTimoutListener {
	
	/**
	 * On timeout.
	 *
	 * @param message the message
	 */
	public void onTimeout(String message);
}
