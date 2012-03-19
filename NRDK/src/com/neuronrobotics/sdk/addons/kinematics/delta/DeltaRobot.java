package com.neuronrobotics.sdk.addons.kinematics.delta;

import java.io.InputStream;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematics;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.dyio.DyIO;

public class DeltaRobot extends AbstractKinematics{

	public DeltaRobot(DyIO dyio) {
		super(XmlFactory.getDefaultConfigurationStream("DeltaPrototype.xml"),new LinkFactory( dyio));
	}
	DeltaRobotKinematics kinematics;

	@Override
	public double[] inverseKinematics(Transform taskSpaceTransform)throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Transform forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return null;
	}
}
