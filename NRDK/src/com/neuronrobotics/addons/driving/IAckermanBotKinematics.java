package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDEvent;

public interface IAckermanBotKinematics {

	/**
	 * Sets up a drive session. Goes some distance and stops.
	 * @param cm the distance traviled in Cm
	 * @param seconds the time that that transversal should take
	 * @return A Drive data object containing the encoder tick values of the difference from 0 to the target values
	 */
	public AckermanBotDriveData DriveStraight(double cm, double seconds);
	/**
	 * Sets up a drive session. Goes some distance and stops. This drives on an arch
	 * @param cmRadius the radius of curvature of the path
	 * @param degrees the number of degrees swept out by the robot
	 * @param seconds the time that that transversal should take
	 * @return A Drive data object containing the encoder tick values of the difference from 0 to the target values
	 */
	public AckermanBotDriveData DriveArc(double cmRadius, double degrees, double seconds);
	
	/**
	 * Sets up a velocity drive session. 
	 * @param cmPerSecond the velocity the robot should travil
	 * @return Velocity data in encoder ticks
	 */
	public AckermanBotVelocityData DriveVelocityStraight(double cmPerSecond) ;
	/**
	 * Sets up a velocity drive session. This drives over a curved path
	 * @param degreesPerSecond the velocity of the robot around the curved path
	 * @param cmRadius the radius of curvature
	 * @return Velocity data in encoder ticks
	 */
	public AckermanBotVelocityData DriveVelocityArc(double degreesPerSecond, double cmRadius) ;
	/**
	 * The delta robots current location in cartesian space
	 * @param e the most recent encoder packet 
	 * @param steerAngle the current angle of the steering wheel
	 * @return 	the Deta of the cartesian position. This is the offset that resulted from the encoder event. 
	 * 			If the robot did not move based on this encoder packet, the position of 0,0,0 should be 
	 * 			returned.
	 */
	public RobotLocationData onPIDEvent(PIDEvent e, double steerAngle) ;
	/**
	 * Get the maximum ticks per second of the robot
	 * @return
	 */
	public double getMaxTicksPerSeconds();
	/**
	 * Reset the Left encoder to a value
	 * @param currentValue
	 */
	public void onPIDReset( int currentValue);

}
