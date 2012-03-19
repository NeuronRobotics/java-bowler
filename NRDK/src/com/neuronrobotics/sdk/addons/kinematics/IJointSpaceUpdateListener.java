package com.neuronrobotics.sdk.addons.kinematics;

//import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public interface IJointSpaceUpdateListener {
	/**
	 * The JointSpace update event
	 * This is called when the kinematics model has a new set of pose data
	 * @param source The AbstractKinematics object that the update was called from
	 * @param joints the current joint space values mm,radians 
	 */
	public void onJointSpaceUpdate(AbstractKinematics source,double [] joints);
	
	/**
	 * The JointSpace update event
	 * This is called when the kinematics model has a new set of pose data
	 * @param source The AbstractKinematics object that the update was called from
	 * @param joints the current joint space values mm,radians 
	 */
	public void onJointSpaceTargetUpdate(AbstractKinematics source,double [] joints);
	
	/**
	 * The JointSpace limit
	 * This is called when the kinematics model has a new set of pose data
	 * @param source The AbstractKinematics object that the update was called from
	 * @param joints the current joint space values mm,radians 
	 */
	public void onJointSpaceLimit(AbstractKinematics source, int axis,JointLimit event);
}
