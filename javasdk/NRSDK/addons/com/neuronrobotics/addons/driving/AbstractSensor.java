package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

public abstract class AbstractSensor {
	
	private ArrayList<ISensorListener> sensorListeners = new ArrayList<ISensorListener>();

	protected AbstractSensor() {
		
	}
	/**
	 * Add an IDriveListener that will be contacted with an   on
	 * each incoming data event.
	 * 
	 * @param l
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
	 * 
	 */
	public void fireRangeSensorEvent(ArrayList<DataPoint> data,long timeStamp) {
		for(ISensorListener l : sensorListeners) {
			l.onRangeSensorEvent(this,data,timeStamp);
		}
	}

	/**
	 * Contact all of the sensorListeners with the given event.
	 * 
	 * 
	 */
	public void fireLineSensorEvent(Integer left,Integer middle,Integer right,long timeStamp) {
		for(ISensorListener l : sensorListeners) {
			l.onLineSensorEvent(this,left,middle,right,timeStamp);
		}
	}
	public abstract void StartSweep(double start, double stop, double increment);

}
