package com.neuronrobotics.sdk.addons.kinematics.delta;

import java.io.InputStream;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematics;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.dyio.DyIO;

public class DeltaRobotPrototype extends AbstractKinematics{
	DeltaRobotKinematics kinematics;
	
	//Configuration hard coded
	 private  double e = 115.0;     // end effector
	 private  double f = 457.3;     // base
	 private  double re = 232.0;
	 private  double rf = 112.0;
	 
	public DeltaRobotPrototype(DyIO dyio) {
		super(XmlFactory.getDefaultConfigurationStream("DeltaPrototype.xml"),new LinkFactory( dyio));
		kinematics = new DeltaRobotKinematics(new DeltaRobotConfig(e, f, re, rf));
	}
	

	@Override
	public double[] inverseKinematics(Transform taskSpaceTransform)throws Exception {
		return kinematics.delta_calcInverse(taskSpaceTransform);
	}
	@Override
	public Transform forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return kinematics.delta_calcForward(jointSpaceVector);
	}
}
