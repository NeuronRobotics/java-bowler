package com.neuronrobotics.sdk.pid;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IPIDEvent events.
 * The class that is interested in processing a IPIDEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIPIDEventListener  method. When
 * the IPIDEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PIDEvent
 * @see  PIDLimitEvent
 */
public interface IPIDEventListener {
	
	/**
	 * On pid event.
	 *
	 * @param e the e
	 */
	public void onPIDEvent(PIDEvent e);
	
	/**
	 * On pid limit event.
	 *
	 * @param e the e
	 */
	public void onPIDLimitEvent(PIDLimitEvent e);
	
	/**
	 * On pid reset.
	 *
	 * @param group the group
	 * @param currentValue the current value
	 */
	public void onPIDReset(int group,int currentValue);
}
