package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.ILinkFactoryProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class CartesianNamespacePidKinematics.
 */
public class CartesianNamespacePidKinematics extends AbstractKinematicsNR{
	
	/** The factory. */
	private LinkFactory factory;
	
	/** The connection. */
	private ILinkFactoryProvider connection;
	
	/**
	 * Instantiates a new cartesian namespace pid kinematics.
	 *
	 * @param device the device
	 * @param connection the connection
	 */
	public CartesianNamespacePidKinematics(GenericPIDDevice device,ILinkFactoryProvider connection){
		super();
		this.connection = connection;
		factory = new LinkFactory(connection,device);
		setDevice(factory,factory.getLinkConfigurations());
		
	}
	
	/**
	 * This calculates the target pose .
	 *
	 * @param taskSpaceTransform the task space transform
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	@Override
	public double[] setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) throws Exception{
		Log.info("Setting target pose: "+taskSpaceTransform);
		setCurrentPoseTarget(taskSpaceTransform);
		taskSpaceTransform = inverseOffset(taskSpaceTransform);
		
		double [] jointSpaceVect = connection.setDesiredTaskSpaceTransform(taskSpaceTransform, seconds);
		factory.setCachedTargets(jointSpaceVect);
		currentJointSpaceTarget = jointSpaceVect;
		fireTargetJointsUpdate(currentJointSpaceTarget,taskSpaceTransform );
		//setDesiredJointSpaceVector(jointSpaceVect,  seconds);
		
		return jointSpaceVect;
	}
	
	/**
	 * This takes a reading of the robots position and converts it to a joint space vector
	 * This vector is converted to task space and returned .
	 *
	 * @return taskSpaceVector in mm,radians [x,y,z,rotx,rotY,rotZ]
	 */
	@Override
	public TransformNR getCurrentTaskSpaceTransform() {
		//TransformNR fwd  = forwardKinematics(getCurrentJointSpaceVector());
		TransformNR fwd  = connection.getCurrentTaskSpaceTransform();
		getCurrentJointSpaceVector();// update the joint space
		
		//Log.info("Getting robot task space "+fwd);
		TransformNR taskSpaceTransform=forwardOffset(fwd);
		//Log.info("Getting global task space "+taskSpaceTransform);
		return taskSpaceTransform;
	}
	
	
	/**
	 * This calculates the target pose .
	 *
	 * @param jointSpaceVect the joint space vect
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	@Override
	public double[] setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds) throws Exception{
		if(jointSpaceVect.length != getNumberOfLinks()){
			throw new IndexOutOfBoundsException("Vector must be "+getNumberOfLinks()+" links, actual number of links = "+jointSpaceVect.length); 
		}
		factory.setCachedTargets(jointSpaceVect);
		
		currentJointSpaceTarget = jointSpaceVect;
		
		TransformNR fwd  = connection.setDesiredJointSpaceVector(jointSpaceVect, seconds);
		fireTargetJointsUpdate(currentJointSpaceTarget,fwd );
		return jointSpaceVect;
	}
	
	/**
	 * Sets an individual target joint position .
	 *
	 * @param axis the joint index to set
	 * @param value the value to set it to
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @throws Exception If there is a workspace error
	 */
	@Override
	public void setDesiredJointAxisValue(int axis, double value, double seconds) throws Exception{
		LinkConfiguration c = getLinkConfiguration(axis);

		Log.info("Setting single target joint in mm/deg, axis="+axis+" value="+value);
		
		currentJointSpaceTarget[axis] = value;
		try{
			getFactory().getLink(c).setTargetEngineeringUnits(value);
		}catch (Exception ex){
			throw new Exception("Joint hit software bound, index "+axis+" attempted: "+value+" boundes: U="+c.getUpperLimit()+ ", L="+c.getLowerLimit());
		}
		if(!isNoFlush()){
			int except=0;
			Exception e = null;
			do{
				try{
					getFactory().getLink(c).flush(seconds);
					except=0;
					e = null;
				}catch(Exception ex){
					except++;
					e=ex;
				}
			}while(except>0 && except <getRetryNumberBeforeFail());
			if(e!=null)
				throw e;	
		}
		TransformNR fwd  = connection.getCurrentTaskSpaceTransform();
		fireTargetJointsUpdate(currentJointSpaceTarget,fwd );
		return;
	}
	
	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)
			throws Exception {
		throw new RuntimeException("This method is unavailible on cartesian devices");
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#forwardKinematics(double[])
	 */
	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		
		throw new RuntimeException("This method is unavailible on cartesian devices");
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#disconnectDevice()
	 */
	@Override
	public void disconnectDevice() {
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR#connectDevice()
	 */
	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
