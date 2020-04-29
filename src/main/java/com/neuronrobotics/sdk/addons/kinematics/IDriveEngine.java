package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * The Interface IDriveEngine.
 */
public interface IDriveEngine {

	/**
	 * Driving kinematics should be implemented in here
	 *
	 * 
	 * This method should not block You will get that called every 0.1 to 0.01 seconds 
	 * by the jog widget with a small displacement transform. If the last command 
	 * finishes before a new one comes in, reset the driving device. 
	 * if a new command comes in then keep moving. Assume that the most important 
	 * thing here is time synchronicity.you may get it called with a large transform, 
	 * larger than you can take in one step,a nd you may get a transform with a step size so 
	 * small it would never move. You will need to warp and stretch the transform coming in 
	 * to make sure there are an integer number of steps, with a t least some minimum step length.
	 * 
	 * Be sure to have any threads you create timeout and die, don't wait for disconnect, as you 
	 * are developing that will be a pain in the ass
	 * 
	 * Essentially, this command defines a velocity (transform/second)and you need to maintain
	 * that velocity, descretized into steps, and stop as soon as the last velocity term times out
	 * also, do not assume it will ever be pure rotation nor pure translation, assume all 
	 * commands are a combo of both.
	 *
	 * @param source the source
	 * @param newPose the new pose that should be achived.
	 * @param seconds how many seconds it should take
	 */
	public abstract void DriveArc(MobileBase source,TransformNR newPose,double seconds);

	
	/**
	 * Tells the robot to start driving at a speed without any endpoint. 
	 * The encoding will track the progress.
	 *
	 * @param source the source
	 * @param cmPerSecond the cm per second
	 */
	public default  void DriveVelocityStraight(MobileBase source,double cmPerSecond) {
		DriveArc(source,new TransformNR(cmPerSecond,0,0,new RotationNR()),1);
	}
	
	/**
	 * Tells the robot to start driving at a speed without any endpoint. 
	 * The encoding will track the progress.
	 * The radius is how much turn arch is needed
	 *
	 * @param source the source
	 * @param degreesPerSecond is now much orientation will change over time
	 * @param cmRadius is the radius of the turn. 0 is turn on center, infinity is driving straight
	 */
	public default  void DriveVelocityArc(MobileBase source,double degreesPerSecond, double cmRadius) {
		DriveArc(source,new TransformNR(0,cmRadius,0,new RotationNR(0, degreesPerSecond, 0)),1);
	}
}
