package com.neuronrobotics.sdk.addons.kinematics;

public abstract class AbstractCartesianPositionDevice {
	
	/*Kevin should we include private AbstractLinks for each
	 * axis in this interface and just type cast them (polymorphism)
	 * to the subclass that is required by that particular device?
	 * 
	 * private AbstractLink X = null;
	 * private AbstractLink y = null;
	 * private AbstractLink z = null;
	 * 
	 * public AbstractCartesianPositionDevice(AbstractLink x2,AbstractLink y2, AbstractLink z2){
	 		X=x2;
	 		Y=y2;
	 		Z=z2;
	 	}
	 * 
	 * the super command would need to be called in all subclasses and all subclasses would have to type
	 * cast the AbstractLink objects to the required subclass of AbstractLink.
	 * 
	 * just a idea I would run by you
	 * 
	 * */
	
	
	/**inititalize is used for seting the AbstractCartesianDevice to its default configuration
	 * */
	public abstract void initialize();
	
	/** flush is for executing the DyIO's flush command
	 * @param time the feedrate for the printer task*/
	public abstract void flush(double time);
	public int ConvertUnitsToSteps(double units) {
		// TODO Auto-generated method stub
		return 0;
	}
	/** SetXAxisPosition takes the location that the X axis will move to,
	 *  typically the steps that the motor must move the use of this
	 * function requires a DyIO flush to be called.
	 *  @param steps the location to where the counter output channel
	 *  		must actuate the motor to*/
	public abstract void SetXAxisPosition(int steps);
	
	/** SetYAxisPosition takes the location that the Y axis will move to,
	 * typically the steps that the motor must move, the use of this
	 * function requires a DyIO flush to be called.
	 * @param steps the location to where the counter output channel
	 *  		must actuate the motor to*/
	public abstract void SetYAxisPosition(int steps);
	
	/** SetYAxisPosition takes the location that the Z axis will move to,
	 * typically the steps that the motor must move the use of this
	 * function requires a DyIO flush to be called.
	 * @param steps the location to where the counter output channel
	 *  		must actuate the motor to*/
	public abstract void SetZAxisPosition(int steps);
	
	/**SetAllAxisPosition tells the AbstractCartesianDevice to actuate all Axes
	 * simultaneously
	 * @param xSteps the location of which you want the X axis to move to
	 * @param ySteps the location of which you want the Y axis to move to
	 * @param zSteps the location of which you want the Z axis to move to
	 * */
	public void SetAllAxisPosition(int xSteps,int ySteps,int zSteps){
		// calls the unimplemented methods for actuating each axis
		SetXAxisPosition(xSteps);
		SetYAxisPosition(ySteps);
		SetZAxisPosition(zSteps);
	}
	
	/** ReCalibrateAbstractCartesianDevice is used for recalibrating the 
	 * device at any point during the set of commands it may be executing 
	 * this method allows for calibration post the initialize routine.*/
	public abstract void ReCalibrateAbstractCartesianDevice();
	
}
