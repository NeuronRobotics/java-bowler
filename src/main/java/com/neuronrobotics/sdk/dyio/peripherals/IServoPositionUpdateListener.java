package com.neuronrobotics.sdk.dyio.peripherals;


// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IServoPositionUpdate events.
 * The class that is interested in processing a IServoPositionUpdate
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIServoPositionUpdateListener  method. When
 * the IServoPositionUpdate event occurs, that object's appropriate
 * method is invoked.
 *

 */
public interface IServoPositionUpdateListener {
	
	/**
	 * On servo position update.
	 *
	 * @param srv the source of the event
	 * @param position the position to set to
	 * @param time th time in seconds
	 */
	public void onServoPositionUpdate(ServoChannel srv, int position, double time);
}
