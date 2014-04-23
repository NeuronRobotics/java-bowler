package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.ILinkFactoryProvider;

public class CartesianNamespacePidKinematics extends AbstractKinematicsNR{
	LinkFactory factory;
	
	public CartesianNamespacePidKinematics(GenericPIDDevice device,ILinkFactoryProvider connection){
		super();
		factory = new LinkFactory(connection,device);
		setDevice(factory);
		
	}
	
	/**
	 * This calculates the target pose 
	 * @param taskSpaceTransform
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	@Override
	public double[] setDesiredTaskSpaceTransform(TransformNR taskSpaceTransform, double seconds) throws Exception{
		Log.info("Setting target pose: "+taskSpaceTransform);
		setCurrentPoseTarget(taskSpaceTransform);
		taskSpaceTransform = inverseOffset(taskSpaceTransform);
		double [] jointSpaceVect = inverseKinematics(taskSpaceTransform);
		setDesiredJointSpaceVector(jointSpaceVect,  seconds);
		return jointSpaceVect;
	}
	
	/**
	 * This takes a reading of the robots position and converts it to a joint space vector
	 * This vector is converted to task space and returned 
	 * @return taskSpaceVector in mm,radians [x,y,z,rotx,rotY,rotZ]
	 */
	@Override
	public TransformNR getCurrentTaskSpaceTransform() {
		TransformNR fwd  = forwardKinematics(getCurrentJointSpaceVector());
		//Log.info("Getting robot task space "+fwd);
		TransformNR taskSpaceTransform=forwardOffset(fwd);
		//Log.info("Getting global task space "+taskSpaceTransform);
		return taskSpaceTransform;
	}
	
	
	/**
	 * This calculates the target pose 
	 * @param JointSpaceVector the target joint space vector
	 * @param seconds the time for the transition to take from current position to target, unit seconds
	 * @return The joint space vector is returned for target arrival referance
	 * @throws Exception If there is a workspace error
	 */
	@Override
	public double[] setDesiredJointSpaceVector(double[] jointSpaceVect, double seconds) throws Exception{
		if(jointSpaceVect.length != getNumberOfLinks()){
			throw new IndexOutOfBoundsException("Vector must be "+getNumberOfLinks()+" links, actual number of links = "+jointSpaceVect.length); 
		}
		String joints = "[";
		for(int i=0;i<jointSpaceVect.length;i++){
			joints+=jointSpaceVect[i]+" ";
		}
		joints+="]";
		Log.info("Setting target joints: "+joints);
		int except=0;
		Exception e = null;
		do{
			try{
				factory.setCachedTargets(jointSpaceVect);
				if(!isNoFlush()){
					//
					factory.flush(seconds);
					//
				}
				except=0;
				e = null;
			}catch(Exception ex){
				except++;
				e=ex;
			}
		}while(except>0 && except <getRetryNumberBeforeFail());
		if(e!=null)
			throw e;
		
		currentJointSpaceTarget = jointSpaceVect;
		TransformNR fwd  = forwardKinematics(currentJointSpaceTarget);
		fireTargetJointsUpdate(currentJointSpaceTarget,fwd );
		return jointSpaceVect;
	}
	
	/**
	 * Sets an individual target joint position 
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
		TransformNR fwd  = forwardKinematics(currentJointSpaceTarget);
		fireTargetJointsUpdate(currentJointSpaceTarget,fwd );
		return;
	}
	
	/**
	 * This takes a reading of the robots position and converts it to a joint pace vector
	 * This vector is converted to Joint space and returned 
	 * @return JointSpaceVector in mm,radians 
	 */
	@Override
	public double[] getCurrentJointSpaceVector() {
		if(currentJointSpacePositions==null){
			//Happens once and only once on the first initialization
			currentJointSpacePositions= new double [getNumberOfLinks()];
			currentJointSpaceTarget  = new double [getNumberOfLinks()];
			for(int i=0;i<getNumberOfLinks();i++){
				//double pos = currentLinkSpacePositions[getLinkConfigurations().get(i).getHardwareIndex()];
				//Here the RAW values are converted to engineering units
				try{
					currentJointSpacePositions[i] = getFactory().getLink(getLinkConfiguration(i)).getCurrentEngineeringUnits();
				}catch (Exception ex){
					currentJointSpacePositions[i] =0;
				}
			}
			firePoseUpdate();
		}
		double [] jointSpaceVect = new double[getNumberOfLinks()];
		for(int i=0;i<getNumberOfLinks();i++){
			jointSpaceVect[i] = currentJointSpacePositions[i];
		}
		
		return jointSpaceVect;
	}
	
	

	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)
			throws Exception {
		throw new RuntimeException("This method is unavailible on cartesian devices");
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		
		throw new RuntimeException("This method is unavailible on cartesian devices");
	}
	
	

}
