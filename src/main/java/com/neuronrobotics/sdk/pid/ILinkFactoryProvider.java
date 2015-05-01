package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public interface ILinkFactoryProvider {
	
	LinkConfiguration requestLinkConfiguration(int index);
	
	/**
	 * This calculates the target pose 
	 * @param taskSpaceTransform
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public double[] setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds);
	
	/**
	 * This takes a reading of the robots position and converts it to a joint space vector
	 * This vector is converted to task space and returned 
	 * @return taskSpaceVector in mm,radians [x,y,z,rotx,rotY,rotZ]
	 */
	public TransformNR getCurrentTaskSpaceTransform();
	
	
	/**
	 * This calculates the target pose 
	 * @param JointSpaceVector the target joint space vector
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	public TransformNR setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds);
	
	/**
	 * Sets an individual target joint position 
	 * @param axis the joint index to set
	 * @param value the value to set it to
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @throws Exception If there is a workspace error
	 */

	public void setDesiredJointAxisValue(int axis, double value, double seconds) ;


}
