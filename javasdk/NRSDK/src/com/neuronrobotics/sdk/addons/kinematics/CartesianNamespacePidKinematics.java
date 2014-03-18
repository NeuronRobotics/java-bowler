package com.neuronrobotics.sdk.addons.kinematics;

import java.io.InputStream;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class CartesianNamespacePidKinematics extends AbstractKinematicsNR{
	LinkFactory factory = new LinkFactory();
	public CartesianNamespacePidKinematics(BowlerAbstractConnection connection){
		super(null, null);
		
	}

	public CartesianNamespacePidKinematics(InputStream configFile, LinkFactory f) {
		super(configFile, f);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return null;
	}

}
