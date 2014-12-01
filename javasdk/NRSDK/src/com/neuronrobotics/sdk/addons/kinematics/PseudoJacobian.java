package com.neuronrobotics.sdk.addons.kinematics;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class PseudoJacobian implements DhInverseSolver{
	public PseudoJacobian(DHChain dhChain, Matrix Jacobian) {
		
	// Loop
	
	// Get time start	
	double StartTime = System.currentTimeMillis();
	
	// Step size in milliseconds
	int StepSize = 50;
		
	// Get the size of the input Jacobian.
	int JacobianRows = Jacobian.getRowDimension();
	int JacobianCols = Jacobian.getColumnDimension();
		
	// Input our predefined TaskSpaceVelocity values, padded to work with our Jacobian dimensions.
	Matrix VelocityDesired  = new Matrix(JacobianRows, 1);
	
	VelocityDesired.set(0,0, 50.);
	VelocityDesired.set(1,0, 50.);
	VelocityDesired.set(2,0, 20.);  
	
	// Pseudo Jacobian = Jacobian Transposed times the inverse of the product of Jacobian and Jacobian Transposed
	Matrix JacobianTranspose = Jacobian.transpose();
	Matrix PseudoJacobianProduct1 = Jacobian.times(JacobianTranspose);
	Matrix PseudoJacobianProduct2 = PseudoJacobianProduct1.inverse();
	Matrix PseudoJacobianProduct3 = JacobianTranspose.times(PseudoJacobianProduct2);

	Matrix VelocityJacobian = PseudoJacobianProduct3.times(VelocityDesired);

	
	//System.out.println("Jacobian2 = " + TransformNR.getMatrixString(PseudoJacobianProduct2));
	//System.out.println("Jacobian2 = " + TransformNR.getMatrixString(PseudoJacobianProduct3));
	//System.out.println("Jacobian3 = " + TransformNR.getMatrixString(VelocityJacobian));
	
	
	// TODO Recursively loop to get JointSpaceVelocity outputs.
	
	
	// Get time finish.
	double EndTime = System.currentTimeMillis();
	
	// Calculate time deltas
	double StepTime = EndTime - StartTime;
	int WaitTime = (int)(StepTime - StepSize);
	
	// Wait additional time (if necessary) to get to our step time size.
	if(StepTime < StepSize){
		System.out.println("Time = " + EndTime);
		System.out.println("Wait = " + StartTime);
		System.out.println("Size = " + StepSize);
		ThreadUtil.wait(WaitTime);
	}
	

	// Break loop when tolerance is reached or max iteration count is exceeded.
	

	
	}

	public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector )
	{
		return jointSpaceVector;
	}

	
}