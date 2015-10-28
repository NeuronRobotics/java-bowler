package com.neuronrobotics.addons.driving;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IRobotDriveEvent events.
 * The class that is interested in processing a IRobotDriveEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIRobotDriveEventListener  method. When
 * the IRobotDriveEvent event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IRobotDriveEventListener {
	
	/**
	 * On drive event.
	 *
	 * @param source the source
	 * @param x the x
	 * @param y the y
	 * @param orentation the orentation
	 */
	public void onDriveEvent(AbstractRobotDrive source,double x, double y, double orentation);
}
