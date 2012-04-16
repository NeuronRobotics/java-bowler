package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.math.Transform;

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
	
	public Transform DhStepRotory(double jointValue) {
		
		Transform transZ = new Transform(new Matrix( new double [][] {	
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,getD()},
				{0,0,0,1}
																	  }
													)
											);
		Transform rotZ = new Transform(new Matrix( new double [][] {	
				{Math.cos(getTheta()+jointValue),	-Math.sin(getTheta()+jointValue),	0,	0},
				{Math.sin(getTheta()+jointValue),	Math.cos(getTheta()+jointValue),	0,	0},
				{0,									0,									1,	0},
				{0,									0,									0,	1}
																	  }
														)
														);
		Transform transX =  new Transform(new Matrix( new double [][] {	
				{1,0,0,getR()},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,0,1}
																	  }
														)
														);
		Transform rotX =  new Transform(new Matrix( new double [][] {	
				{1,	0,						0,							0},
				{0,	Math.cos(getAlpha()),	-Math.sin(getAlpha()),		0},
				{0,	Math.sin(getAlpha()),	Math.cos(getAlpha()),		0},
				{0,	0,						0,							1}
																	  }
														)
														);
		Transform z = transZ.times(rotZ);
		Transform x = transX.times(rotX);
		
		return z.times(x);
	}
}
