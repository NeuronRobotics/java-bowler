package com.neuronrobotics.sdk.common;


// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IDeviceAdded events.
 * The class that is interested in processing a IDeviceAdded
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIDeviceAddedListener  method. When
 * the IDeviceAdded event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface IDeviceAddedListener {
	
	/**
	 * On new device added.
	 *
	 * @param bad the bad
	 */
	public void onNewDeviceAdded(BowlerAbstractDevice bad);
	
	/**
	 * On device removed.
	 *
	 * @param bad the bad
	 */
	public void onDeviceRemoved(BowlerAbstractDevice bad);
}
