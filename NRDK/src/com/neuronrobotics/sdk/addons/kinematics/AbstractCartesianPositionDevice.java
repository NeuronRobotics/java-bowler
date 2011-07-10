package com.neuronrobotics.sdk.addons.kinematics;

public abstract class AbstractCartesianPositionDevice {
	
	
	/**inititalize is used for seting the AbstractCartesianDevice to its default configuration
	 * */
	public abstract void initialize();
	/** flush is for executing the DyIO's flush command*/
	public abstract void flush(double time);
	public int ConvertUnitsToSteps(double units) {
		// TODO Auto-generated method stub
		return 0;
	}
	public abstract void SetXAxisPosition(int steps);
	public abstract void SetYAxisPosition(int steps);
	public abstract void SetZAxisPosition(int steps);
	
	/**SetAllAxisPosition tells the AbstractCartesianDevice to actuate all Axes
	 * simultaneously
	 * @param xSteps the amount of steps for the X axis
	 * @param ySteps the amount of steps for the Y axis
	 * @param zSteps the amount of steps for the Z axis
	 * */
	public void SetAllAxisPosition(int xSteps,int ySteps,int zSteps){
		SetXAxisPosition(xSteps);
		SetYAxisPosition(ySteps);
		SetZAxisPosition(zSteps);
	}
	
}
