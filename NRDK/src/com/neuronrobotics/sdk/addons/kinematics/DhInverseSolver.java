package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.Transform;

public interface DhInverseSolver {
	double[] inverseKinematics(Transform target,double[] jointSpaceVector );
}
