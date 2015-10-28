package com.neuronrobotics.sdk.util;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IProgressMonitor events.
 * The class that is interested in processing a IProgressMonitor
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIProgressMonitorListener  method. When
 * the IProgressMonitor event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IProgressMonitorListener {
	
	/**
	 * On update.
	 *
	 * @param value the value
	 */
	public void onUpdate(double value);
	
	/**
	 * On complete.
	 */
	public void onComplete();
}
