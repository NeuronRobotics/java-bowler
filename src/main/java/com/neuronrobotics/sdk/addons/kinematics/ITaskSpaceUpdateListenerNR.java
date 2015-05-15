package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public interface ITaskSpaceUpdateListenerNR {
	/**
	 * The position update event
	 * This is called when the kinematics model has a new set of pose data
	 * @param source The AbstractKinematics object that the update was called from
	 * @param pose the current pose transform
	 */
	public void onTaskSpaceUpdate(AbstractKinematicsNR source,TransformNR pose);
	
	/**
	 * The position update event
	 * This is called when the kinematics model has a new set of target data
	 * @param source The AbstractKinematics object that the update was called from
	 * @param pose target pose transform
	 */
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,TransformNR pose);
	

}
