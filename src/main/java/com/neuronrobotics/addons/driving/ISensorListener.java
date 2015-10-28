package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ISensor events.
 * The class that is interested in processing a ISensor
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addISensorListener  method. When
 * the ISensor event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface ISensorListener {
	
	/**
	 * On range sensor event.
	 *
	 * @param source the source
	 * @param data the data
	 * @param timeStamp the time stamp
	 */
	public void onRangeSensorEvent(AbstractSensor source,ArrayList<DataPoint> data,long timeStamp);
	
	/**
	 * On line sensor event.
	 *
	 * @param source the source
	 * @param left the left
	 * @param middle the middle
	 * @param right the right
	 * @param timeStamp the time stamp
	 */
	public void onLineSensorEvent(AbstractSensor source,Integer left,Integer  middle,Integer  right,long timeStamp);
}
