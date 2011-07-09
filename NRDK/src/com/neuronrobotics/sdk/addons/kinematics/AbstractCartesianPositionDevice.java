package com.neuronrobotics.sdk.addons.kinematics;

public abstract class AbstractCartesianPositionDevice {
	
	
	public abstract void initialize();
	public abstract void flush(double time);
	public int ConvertUnitsToSteps(double units) {
		// TODO Auto-generated method stub
		return 0;
	}
	public abstract void SetXAxisPosition(int steps);
	public abstract void SetYAxisPosition(int steps);
	public abstract void SetZAxisPosition(int steps);
	
}
