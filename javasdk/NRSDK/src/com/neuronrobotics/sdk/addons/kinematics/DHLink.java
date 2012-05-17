package com.neuronrobotics.sdk.addons.kinematics;

import Jama.Matrix;

public class DHLink {
	
	private final double d;
	private final double theta;
	private final double r;
	private final double alpha;
	private Matrix transX;
	private Matrix rotX;
	private Matrix transZ;
	private Matrix rotZ;
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
	public Matrix DhStepInverseRotory(Matrix end, double jointValue) {	
		return  DhStepInverse(end,jointValue,0);
	}
	public Matrix DhStepInversePrismatic(Matrix end, double jointValue) {	
		return  DhStepInverse(end,0,jointValue);
	}
	public Matrix DhStepRotory(double jointValue) {	
		return DhStep(jointValue,0);
	}
	public Matrix DhStepPrismatic(double jointValue) {
		
		return DhStep(0,jointValue);
	}
	
	private void setMatrix(double rotory,double prismatic){
		setTransZ(new Matrix( new double [][] {	
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,getD()+prismatic},
				{0,0,0,1}
																	  }));
		
		setRotZ(new Matrix( new double [][] {	
				{Math.cos(getTheta()+rotory),	-Math.sin(getTheta()+rotory),	0,	0},
				{Math.sin(getTheta()+rotory),	Math.cos(getTheta()+rotory),	0,	0},
				{0,									0,									1,	0},
				{0,									0,									0,	1}
																	  }
														));
	}
	
	public Matrix DhStep(double rotory,double prismatic) {

		setMatrix(rotory, prismatic);
		
		Matrix step = getTransZ();
		step = step.times(getRotZ());
		step = step.times(getTransX());
		step = step.times(getRotX());
		
		return step;
	}
	public Matrix DhStepInverse(Matrix end,double rotory,double prismatic) {
		setMatrix(rotory, prismatic);
		
		Matrix step = end.times(getTransZ().inverse());
		step = step.times(getRotZ().inverse());
		step = step.times(getTransX().inverse());
		step = step.times(getRotX().inverse());
		
		return step;
	}

	public void setTransX(Matrix transX) {
		this.transX = transX;
	}

	public Matrix getTransX() {
		 if(transX == null){
			 setTransX(new Matrix( new double [][] {	
					{1,0,0,getR()},
					{0,1,0,0},
					{0,0,1,0},
					{0,0,0,1}
															  }
														
														));
		 }	
		return transX;
	}

	public void setRotX(Matrix rotX) {
		this.rotX = rotX;
	}

	public Matrix getRotX() {
		 if(rotX == null){
			 setRotX(new Matrix( new double [][] {	
				{1,	0,						0,							0},
				{0,	Math.cos(getAlpha()),	-Math.sin(getAlpha()),		0},
				{0,	Math.sin(getAlpha()),	Math.cos(getAlpha()),		0},
				{0,	0,						0,							1}
																	  }
														
														));
		 }
		return rotX;
	}

	public void setTransZ(Matrix transZ) {
		this.transZ = transZ;
	}

	public Matrix getTransZ() {
		return transZ;
	}

	public void setRotZ(Matrix rotZ) {
		this.rotZ = rotZ;
	}

	public Matrix getRotZ() {
		return rotZ;
	}
}
