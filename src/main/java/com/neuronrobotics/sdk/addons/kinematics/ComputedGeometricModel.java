package com.neuronrobotics.sdk.addons.kinematics;
import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * The Class ComputedGeometricModel.
 */
public class ComputedGeometricModel  implements DhInverseSolver{
	
	/** The dh chain. */
	private DHChain dhChain;
	
	/**
	 * Instantiates a new computed geometric model.
	 *
	 * @param dhChain the dh chain
	 * @param debug the debug
	 */
	public ComputedGeometricModel(DHChain dhChain, boolean debug) {
		this.dhChain = dhChain;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.DhInverseSolver#inverseKinematics(com.neuronrobotics.sdk.addons.kinematics.math.TransformNR, double[], com.neuronrobotics.sdk.addons.kinematics.DHChain)
	 */
	public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector, 
			DHChain chain ) {
		ArrayList<DHLink> links = chain.getLinks();
		//viewer.addTransform(target, "Target",Color.pink);
				int linkNum = jointSpaceVector.length;
		double [] inv = new double[linkNum];
		//Attempting to implement:
		//http://www.ri.cmu.edu/pub_files/pub1/xu_yangsheng_1993_1/xu_yangsheng_1993_1.pdf
		TransformNR current = dhChain.forwardKinematics(jointSpaceVector);
		return inv;
	}

}
