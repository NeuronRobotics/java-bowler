package com.neuronrobotics.sdk.addons.kinematics;
import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class ComputedGeometricModel  implements DhInverseSolver{
	private DHChain dhChain;
	public ComputedGeometricModel(DHChain dhChain, boolean debug) {
		this.dhChain = dhChain;
	}

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
