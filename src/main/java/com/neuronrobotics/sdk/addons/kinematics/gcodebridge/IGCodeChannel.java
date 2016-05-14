package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

public interface IGCodeChannel {
	/**
	 * Return the gcode axis of this channel
	 * @return the axis
	 */
	public String getAxis();
	
	/**
	 * Set a value of the current position
	 * @param value of the current psition 
	 */
	public void setValue(double value) ;

}
