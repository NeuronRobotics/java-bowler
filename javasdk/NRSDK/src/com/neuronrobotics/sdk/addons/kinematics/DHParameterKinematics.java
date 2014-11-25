package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;
import java.util.ArrayList;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;


public class DHParameterKinematics extends AbstractKinematicsNR {
	
	private DHChain chain=null;

	public DHParameterKinematics() {
		this((DyIO)null,"TrobotLinks.xml");
	}
	
	public DHParameterKinematics( DyIO dev) {
		this(dev,"TrobotLinks.xml");

	}
	public DHParameterKinematics( DyIO dev, String file) {
		this(dev,XmlFactory.getDefaultConfigurationStream(file),XmlFactory.getDefaultConfigurationStream(file));
		
	}
	public DHParameterKinematics( GenericPIDDevice dev) {
		this(dev,"TrobotLinks.xml");

	}
	public DHParameterKinematics( GenericPIDDevice dev, String file) {
		this(dev,XmlFactory.getDefaultConfigurationStream(file),XmlFactory.getDefaultConfigurationStream(file));
		
	}
	public DHParameterKinematics( DyIO dev, InputStream linkStream, InputStream dhStream) {
		super(linkStream,new LinkFactory( dev));
		chain = new DHChain(dhStream,getFactory());
	}
	
	public DHParameterKinematics(GenericPIDDevice dev, InputStream linkStream, InputStream dhStream) {
		super(linkStream,new LinkFactory( dev));
		chain = new DHChain(dhStream,getFactory());
	}

	public DHParameterKinematics(InputStream linkStream, InputStream dhStream) {
		super(linkStream,new LinkFactory());
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
	 * Gets the Jacobian matrix
	 * @return a matrix representing the Jacobian for the current configuration
	 */
	public Matrix getJacobian(){
		long time = System.currentTimeMillis();
		Matrix m = getDhChain().getJacobian(getCurrentJointSpaceVector());
		System.out.println("Jacobian calc took: "+(System.currentTimeMillis()-time));
		return m;
	}
	
	public ArrayList<TransformNR> getChainTransformations(){
		return chain.getChain(getCurrentJointSpaceVector());
	}

	public void setDhChain(DHChain chain) {
		this.chain = chain;
	}

	public DHChain getDhChain() {
		return chain;
	}


}
