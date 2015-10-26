package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public interface IDriveEngine {

	/**
	 * Driving kinematics should be implemented in here
	 * Before driving, a reset for each drive wheel should be called
	 * NOTE This should obey the right-hand rule.
	 * @param newPose the new pose that should be achived. 
	 * @param seconds how many seconds it should take
	 */
	public abstract void DriveArc(MobileBase source,TransformNR newPose,double seconds);

	
	/**
	 * Tells the robot to start driving at a speed without any endpoint. 
	 * The encoding will track the progress.
	 * @param cmPerSecond
	 */
	public abstract void DriveVelocityStraight(MobileBase source,double cmPerSecond);
	/**
	 * Tells the robot to start driving at a speed without any endpoint. 
	 * The encoding will track the progress.
	 * The radius is how much turn arch is needed
	 * @param degreesPerSecond is now much orientation will change over time
	 * @param cmRadius is the radius of the turn. 0 is turn on center, infinity is driving straight
	 */
	public abstract void DriveVelocityArc(MobileBase source,double degreesPerSecond, double cmRadius);
}
