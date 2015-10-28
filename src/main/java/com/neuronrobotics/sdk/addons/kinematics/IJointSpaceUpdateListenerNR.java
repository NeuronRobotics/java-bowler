package com.neuronrobotics.sdk.addons.kinematics;

// TODO: Auto-generated Javadoc
//import com.neuronrobotics.sdk.pid.PIDLimitEvent;

/**
 * The Interface IJointSpaceUpdateListenerNR.
 */
public interface IJointSpaceUpdateListenerNR {
	
	/**
	 * The JointSpace update event
	 * This is called when the kinematics model has a new set of pose data.
	 *
	 * @param source The AbstractKinematics object that the update was called from
	 * @param joints the current joint space values mm,radians
	 */
	public void onJointSpaceUpdate(AbstractKinematicsNR source,double [] joints);
	
	/**
	 * The JointSpace update event
	 * This is called when the kinematics model has a new set of pose data.
	 *
	 * @param source The AbstractKinematics object that the update was called from
	 * @param joints the current joint space values mm,radians
	 */
	public void onJointSpaceTargetUpdate(AbstractKinematicsNR source,double [] joints);
	
	/**
	 * The JointSpace limit
	 * This is called when the kinematics model has a new set of pose data.
	 *
	 * @param source The AbstractKinematics object that the update was called from
	 * @param axis the axis
	 * @param event the event
	 */
	public void onJointSpaceLimit(AbstractKinematicsNR source, int axis,JointLimit event);
}
