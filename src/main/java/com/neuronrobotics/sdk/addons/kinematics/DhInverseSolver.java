package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public interface DhInverseSolver {
	double[] inverseKinematics(TransformNR target,double[] jointSpaceVector, ArrayList<DHLink> links );
}
