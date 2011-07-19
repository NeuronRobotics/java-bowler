package com.neuronrobotics.sdk.addons.kinematics;

public abstract class AbstractCartesianPositionDevice {
	
	/**initialize is used for changing any settings that need to be changed at the start
	 * of a print job
	 * */
	public abstract void initialize();
	
	/** flush is for executing the DyIO's flush command
	 * @param time the feedrate for the printer task*/
	public abstract void flush(double time);
	
	/** SetXAxisPosition takes the location that the X axis will move to,
	 *  typically the steps that the motor must move the use of this
	 * function requires a DyIO flush to be called.
	 *  @param steps the location to where the counter output channel
	 *  		must actuate the motor to*/
	public abstract void SetXAxisPosition(double unit, double time);
	
	/** SetYAxisPosition takes the location that the Y axis will move to,
	 * typically the steps that the motor must move, the use of this
	 * function requires a DyIO flush to be called.
	 * @param steps the location to where the counter output channel
	 *  		must actuate the motor to*/
	public abstract void SetYAxisPosition(double unit, double time);
	
	/** SetYAxisPosition takes the location that the Z axis will move to,
	 * typically the steps that the motor must move the use of this
	 * function requires a DyIO flush to be called.
	 * @param steps the location to where the counter output channel
	 *  		must actuate the motor to*/
	public abstract void SetZAxisPosition(double unit, double time);
	
	/**SetAllAxisPosition tells the AbstractCartesianDevice to actuate all Axes
	 * simultaneously
	 * @param xSteps the location of which you want the X axis to move to
	 * @param ySteps the location of which you want the Y axis to move to
	 * @param zSteps the location of which you want the Z axis to move to
	 * */
	public abstract void SetAllAxisPosition(double xSteps,double ySteps,double zSteps,double time);
	
	/**SetAllAxisToHome sets all cartesian axes to theit home position
	 * @param time accounts for the feedrate at which the axes are moved */
	public abstract void SetAllAxisToHome(double time);
	
	/**SetXToHome sets the X axis to its home position
	 * @param time accounts for the feedrate for which the axis is moved */
	public abstract void SetXToHome(double time);
	
	/**SetYToHome sets the Y axis to its home position
	 * @param time accounts for the feedrate for which the axis is moved */
	public abstract void SetYToHome(double time);
	
	/**SetZToHome sets the Z axis to its home position
	 * @param time accounts for the feeedrate for which the axis is moved */
	public abstract void SetZToHome(double time);	
}
