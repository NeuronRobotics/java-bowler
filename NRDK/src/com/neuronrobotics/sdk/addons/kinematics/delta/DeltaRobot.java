package com.neuronrobotics.sdk.addons.kinematics.delta;

import java.io.InputStream;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematics;
import com.neuronrobotics.sdk.addons.kinematics.AbstractRotoryLink;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;

public class DeltaRobot extends AbstractKinematics{

	public DeltaRobot(InputStream configFile, LinkFactory f) {
		super(configFile, f);
		// TODO Auto-generated constructor stub
	}
	DeltaRobotKinematics kinematics;

	@Override
	public double[] inverseKinematics(Transform taskSpaceTransform)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Transform forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return null;
	}
}
