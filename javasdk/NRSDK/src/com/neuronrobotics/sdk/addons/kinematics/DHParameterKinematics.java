package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;

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
		this(dev,XmlFactory.getDefaultConfigurationStream(file));
		
	}
	public DHParameterKinematics( DyIO dev, InputStream stream) {
		super(stream,new LinkFactory( dev));
		chain = new DHChain(stream,getFactory());
		try {
			setDesiredJointSpaceVector(new double[] {0,0,0,0,0,0}, 1.0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	public void setDhChain(DHChain chain) {
		this.chain = chain;
	}

	public DHChain getDhChain() {
		return chain;
	}

}
