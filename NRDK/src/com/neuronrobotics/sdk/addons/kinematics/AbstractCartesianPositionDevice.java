package com.neuronrobotics.sdk.addons.kinematics;

public abstract class AbstractCartesianPositionDevice {
	
	
	public abstract void initialize();
	public abstract void flush(double time);
}
