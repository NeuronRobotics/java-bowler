package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

import Jama.Matrix;

public class DHLink {
	
	private final double d;
	private final double theta;
	private final double r;
	private final double alpha;

	public DHLink(double d, double theta,double r, double alpha) {
		this.d = d;
		this.theta = theta;
		this.r = r;
		this.alpha = alpha;
		
	}

	public double getD() {
		return d;
	}

	public double getTheta() {
		return theta;
	}

	public double getR() {
		return r;
	}

	public double getAlpha() {
		return alpha;
	}
	
	public Matrix DhStepRotory(double jointValue) {	
		return DhStep(jointValue,0);
	}
	public Matrix DhStepPrismatic(double jointValue) {
		return DhStep(0,jointValue);
	}
	
	public Matrix DhStep(double rotory,double prismatic) {
		
		 Matrix transZ = new Matrix( new double [][] {	
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,getD()+prismatic},
				{0,0,0,1}
																	  });
		 Matrix rotZ = new Matrix( new double [][] {	
				{Math.cos(getTheta()+rotory),	-Math.sin(getTheta()+rotory),	0,	0},
				{Math.sin(getTheta()+rotory),	Math.cos(getTheta()+rotory),	0,	0},
				{0,									0,									1,	0},
				{0,									0,									0,	1}
																	  }
														);
		 Matrix transX =  new Matrix( new double [][] {	
				{1,0,0,getR()},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,0,1}
																	  }
														
														);
		 Matrix rotX =  new Matrix( new double [][] {	
				{1,	0,						0,							0},
				{0,	Math.cos(getAlpha()),	-Math.sin(getAlpha()),		0},
				{0,	Math.sin(getAlpha()),	Math.cos(getAlpha()),		0},
				{0,	0,						0,							1}
																	  }
														
														);
		 Matrix z = transZ.times(rotZ);
		 Matrix x = transX.times(rotX);
		
		return  z.times(x);
	}
}
