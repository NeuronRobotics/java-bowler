package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.dyio.DyIO;


public class DHParameterKinematics extends AbstractKinematicsNR {
	
	private DHChain chain=null;

	public DHParameterKinematics() {
		this(null,"TrobotLinks.xml");
	}
	
	public DHParameterKinematics( DyIO dev) {
		this(dev,"TrobotLinks.xml");

	}
	public DHParameterKinematics( DyIO dev, String file) {
		this(dev,XmlFactory.getDefaultConfigurationStream(file),XmlFactory.getDefaultConfigurationStream(file));
		
	}
	public DHParameterKinematics( DyIO dev, InputStream linkStream, InputStream dhStream) {
		super(linkStream,new LinkFactory( dev));
		chain = new DHChain(dhStream,getFactory());
		
	}

	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)throws Exception {
		return getDhChain().inverseKinematics(taskSpaceTransform, getCurrentJointSpaceVector());
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		if(jointSpaceVector == null || getDhChain() == null)
			return new TransformNR();
		return getDhChain().forwardKinematics(jointSpaceVector);
	}
	
	/**
	 * Gets the task space velocites from the joint space velocity vector
	 * @param jointSpaceVelocityVector the joint velocities
	 * @return a matrix of the task space velocities
	 */
	public Matrix getJacobian(){
		return chain.getJacobian(getCurrentJointSpaceVector());
	}

	public void setDhChain(DHChain chain) {
		this.chain = chain;
	}

	public DHChain getDhChain() {
		return chain;
	}

}
