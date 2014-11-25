package com.neuronrobotics.sdk.addons.kinematics;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class PseudoJacobian implements DhInverseSolver{
	public PseudoJacobian(DHChain dhChain, Matrix Jacobian) {
		
	// Input our predefined TaskSpaceVelocity inputs, padded to fit our Jacobian calculations.
	Matrix VelocityDesired = new Matrix(new double[]{50., 50., 20., 0.}, 1);     
	
	
	// Pseudo Jacobian = Jacobian Transposed times the inverse of the product of Jacobian and Jacobian Transposed
	Matrix JacobianTranspose = Jacobian.transpose();
	Matrix PseudoJacobianProduct1 = Jacobian.times(JacobianTranspose);
	Matrix PseudoJacobianProduct2 = PseudoJacobianProduct1.inverse();
	Matrix PseudoJacobianProduct3 = JacobianTranspose.times(PseudoJacobianProduct2);

	Matrix VelocityJacobian = PseudoJacobianProduct3.times(VelocityDesired.transpose());

	
	System.out.println("Jacobian2 = " + TransformNR.getMatrixString(PseudoJacobianProduct2));
	System.out.println("Jacobian2 = " + TransformNR.getMatrixString(PseudoJacobianProduct3));
	System.out.println("Jacobian3 = " + TransformNR.getMatrixString(VelocityJacobian));
	
	
	// TODO Recursively loop to get JointSpaceVelocity outputs.
	
	
	
	}

	public double[] inverseKinematics(TransformNR target,double[] jointSpaceVector )
	{
		return jointSpaceVector;
	}

	
	
	
	
	
}