package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractSensor.
 */
public abstract class AbstractSensor {
	
	/** The sensor listeners. */
	private ArrayList<ISensorListener> sensorListeners = new ArrayList<ISensorListener>();

	/**
	 * Instantiates a new abstract sensor.
	 */
	protected AbstractSensor() {
		
	}
	
	/**
	 * Add an IDriveListener that will be contacted with an   on
	 * each incoming data event.
	 *
	 * @param l the l
	 */
	public void addSensorListener(ISensorListener l) {
		if(sensorListeners.contains(l)) {
			return;
		}
		sensorListeners.add(l);
	}
	
	/**
	 * Contact all of the sensorListeners with the given event.
	 *
	 * @param data the data
	 * @param timeStamp the time stamp
	 */
	public void fireRangeSensorEvent(ArrayList<DataPoint> data,long timeStamp) {
		for(ISensorListener l : sensorListeners) {
			l.onRangeSensorEvent(this,data,timeStamp);
		}
	}

	/**
	 * Contact all of the sensorListeners with the given event.
	 *
	 * @param left the left
	 * @param middle the middle
	 * @param right the right
	 * @param timeStamp the time stamp
	 */
	public void fireLineSensorEvent(Integer left,Integer middle,Integer right,long timeStamp) {
		for(ISensorListener l : sensorListeners) {
			l.onLineSensorEvent(this,left,middle,right,timeStamp);
		}
	}
	
	/**
	 * Start sweep.
	 *
	 * @param start the start
	 * @param stop the stop
	 * @param increment the increment
	 */
	public abstract void StartSweep(double start, double stop, double increment);

}
