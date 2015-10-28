package com.neuronrobotics.test.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.GenericKinematicsModelNR;
import com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.JointLimit;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericKinematicsTest.
 */
public class GenericKinematicsTest implements IJointSpaceUpdateListenerNR, ITaskSpaceUpdateListenerNR, IRegistrationListenerNR {
	
	/**
	 * Instantiates a new generic kinematics test.
	 */
	private GenericKinematicsTest(){
		//Create the kinematics object
		AbstractKinematicsNR kin = new GenericKinematicsModelNR();
		//Get the current pose of the robot in mm
		TransformNR current = kin.getCurrentTaskSpaceTransform();
		//Get the current joint angles in engineering units
		double [] currentJoints  = kin.getCurrentJointSpaceVector();
		
		kin.addJointSpaceListener(this);
		kin.addPoseUpdateListener(this);
		kin.addRegistrationListener(this);
		
		TransformNR newTarget = new TransformNR(current.getX() + 5,
				current.getY()-3,
				current.getZ()+.5,
				current.getRotation());
		
		try {
			kin.setDesiredTaskSpaceTransform(newTarget, 2.5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new GenericKinematicsTest();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, double[])
	 */
	@Override
	public void onJointSpaceUpdate(AbstractKinematicsNR source, double[] joints) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceTargetUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, double[])
	 */
	@Override
	public void onJointSpaceTargetUpdate(AbstractKinematicsNR source,
			double[] joints) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IJointSpaceUpdateListenerNR#onJointSpaceLimit(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, int, com.neuronrobotics.sdk.addons.kinematics.JointLimit)
	 */
	@Override
	public void onJointSpaceLimit(AbstractKinematicsNR source, int axis,
			JointLimit event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR#onBaseToFiducialUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onBaseToFiducialUpdate(AbstractKinematicsNR source,
			TransformNR regestration) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR#onFiducialToGlobalUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,TransformNR regestration) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTargetTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source,
			TransformNR pose) {
		// TODO Auto-generated method stub
		
	}

}
