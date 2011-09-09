package com.neuronrobotics.addons.driving;

public abstract class AbstractDrivingRobot {
	protected IRangeSensor range=null;
	protected ILineSensor line=null;
	
	public void setRangeSensor(IRangeSensor range) {
		this.range=range;
	}
	public void setLineSensor(ILineSensor line) {
		this.line=line;
	}
	
	
	
	/**
	 * Driving kinematics should be implemented in here
	 * Before driving, a reset for each drive wheel should be called
	 * @param cm how many centimeters should be driven
	 * @param seconds how many seconds it should take
	 */
	public abstract void DriveStraight(double cm,double seconds);
	/**
	 * Driving kinematics should be implemented in here
	 * Before driving, a reset for each drive wheel should be called
	 * NOTE This should obey the right-hand rule.
	 * @param cmRadius radius of curve (centimeters)
	 * @param degrees degrees of the arch to sweep through
	 * @param seconds how many seconds it should take
	 */
	public abstract void DriveArc(double cmRadius,double degrees,double seconds);
}
