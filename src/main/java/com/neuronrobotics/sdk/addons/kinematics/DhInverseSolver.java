package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * The Interface DhInverseSolver.
 */
public interface DhInverseSolver {
	
	/**
	 * Inverse kinematics.
	 *
	 * @param target the target
	 * @param jointSpaceVector the joint space vector
	 * @param chain the chain
	 * @return the double[]
	 */
	double[] inverseKinematics(TransformNR target,double[] jointSpaceVector, DHChain chain );
}
