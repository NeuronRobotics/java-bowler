package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public interface IRegistrationListenerNR {
	/**
	 * The fiducial to robot 0 
	 * This is called when the configuration of the robot is set up
	 * @param source The AbstractKinematics object that the update was called from
	 * @param regestration the current regestration
	 */
	public void onBaseToFiducialUpdate(AbstractKinematicsNR source,TransformNR regestration);
	
	/**
	 * The global to fiducial transform
	 * This is called when the robot is regestered in global coordinantes
	 * @param source The AbstractKinematics object that the update was called from
	 * @param regestration the current regestration in global space
	 */
	public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,TransformNR regestration);
}
