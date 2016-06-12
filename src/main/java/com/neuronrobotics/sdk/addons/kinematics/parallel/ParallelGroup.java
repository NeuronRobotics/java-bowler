package com.neuronrobotics.sdk.addons.kinematics.parallel;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class ParallelGroup extends AbstractKinematicsNR {

	@Override
	public void disconnectDevice() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		// TODO Auto-generated method stub
		return null;
	}

}
