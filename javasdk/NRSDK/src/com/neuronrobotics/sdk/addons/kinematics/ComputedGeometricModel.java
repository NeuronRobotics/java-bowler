package com.neuronrobotics.sdk.addons.kinematics;

import java.awt.Color;

import javax.swing.JFrame;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.DHChain;
import com.neuronrobotics.sdk.addons.kinematics.DhInverseSolver;
import com.neuronrobotics.sdk.addons.kinematics.gui.SimpleTransformViewer;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class ComputedGeometricModel  implements DhInverseSolver{
	private DHChain dhChain;
	private boolean debug;
	static SimpleTransformViewer viewer = new  SimpleTransformViewer();
	static JFrame frame = new JFrame();
	public ComputedGeometricModel(DHChain dhChain, boolean debug) {
		this.dhChain = dhChain;
		this.setDebug(debug);
		frame.add(viewer);
		frame.setSize(720, 640);
		frame.setVisible(true);
		
	}
	
	public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector ) {
		viewer.addTransform(target, "Target",Color.pink);
		
		int linkNum = jointSpaceVector.length;
		double [] inv = new double[linkNum];
		if(!checkSphericalWrist() || dhChain.getLinks().size() != 6) {
			throw new RuntimeException("This is not a 6DOF arm with a spherical wrist, this solver will not work");
		}
		//Attempting to implement:
		//http://www.ri.cmu.edu/pub_files/pub1/xu_yangsheng_1993_1/xu_yangsheng_1993_1.pdf
		TransformNR current = dhChain.forwardKinematics(jointSpaceVector);
		viewer.addTransform(current, "CURRENT",Color.GREEN);
		
		//Procedure:
		/*
		Calculate the Jacobian
		matrix J(Θ).
		*/
		
		Matrix jacobian = dhChain.getJacobian(jointSpaceVector);
		jacobian.getColumnDimension();
		
		/*
		Determine the minima and
		maxima for each of the Jij
		elements of J(Θ).
		*/
		
		/*
		Generate p input/output data
		vectors (dri, Jij, dθij).
		*/
		
		/*
		 * Apply fuzzy mapping to the
				relation: dθij= dri /Jij
		 */
		
        /*
         * Combine weighted dθij terms to form the dθi terms.
         */

		return inv;
	}

	private boolean checkSphericalWrist() {
		int end = dhChain.getLinks().size()-1;
		return 	dhChain.getLinks().get(end).getR()	==0 && 
				dhChain.getLinks().get(end-1).getR()==0 && 
				dhChain.getLinks().get(end-2).getR()==0  ;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}
}
