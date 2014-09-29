package com.neuronrobotics.sdk.dyio.peripherals;


public interface IServoPositionUpdateListener {
	/**
	 * 
	 * @param srv the source of the event
	 * @param position the position to set to 
	 * @param time th time in seconds
	 */
	public void onServoPositionUpdate(ServoChannel srv, int position, double time);
}
