package com.neuronrobotics.sdk.addons.kinematics;

import org.w3c.dom.Element;

import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;

import Jama.Matrix;

public class DHLink {
	
	private double d;
	private double theta;
	private double r;
	private double alpha;
	private Matrix transX;
	private Matrix rotX;
	private Matrix transZ;
	private Matrix rotZ;
	
	private Matrix transX_J;
	private Matrix rotX_J;
	private Matrix transZ_J;
	private Matrix rotZ_J;
	
	public DHLink(double d, double theta,double r, double alpha) {
		this.d = d;
		this.theta = theta;
		this.r = r;
		this.alpha = alpha;
		
	}

	public DHLink(Element nNode) {
		d		=				XmlFactory.getTagValueDouble("Delta", nNode);
		theta	=Math.toRadians(XmlFactory.getTagValueDouble("Theta", nNode));
		r		=				XmlFactory.getTagValueDouble("Radius", nNode);
		alpha	=Math.toRadians(XmlFactory.getTagValueDouble("Alpha", nNode));
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

	public void setRotX(Matrix rotX) {
		this.rotX = rotX;
	}
	
	
	private void setMatrix(double rotory,double prismatic){
		transZ = new Matrix( new double [][] {	
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,getD()+prismatic},
				{0,0,0,1}});
		
		rotZ=new Matrix( new double [][] {	
				{Math.cos(getTheta()+rotory),	 -Math.sin(getTheta()+rotory),	0,	0},
				{Math.sin(getTheta()+rotory),	  Math.cos(getTheta()+rotory),	0,	0},
				{0,															0,	1,	0},
				{0,															0,	0,	1}});
	}
	public Matrix getTransX() {
		 if(transX == null){
			 transX=new Matrix( new double [][] {	
					{1,0,0,getR()},
					{0,1,0,0},
					{0,0,1,0},
					{0,0,0,1}});
		}	
		return transX;
	}

	public Matrix getRotX() {
		 if(rotX == null){
			 rotX=new Matrix( new double [][] {	
				{1,	0,						0,							0},
				{0,	Math.cos(getAlpha()),	-Math.sin(getAlpha()),		0},
				{0,	Math.sin(getAlpha()),	Math.cos(getAlpha()),		0},
				{0,	0,						0,							1}});
		 }
		return rotX;
	}
	public Matrix getTransZ() {
		return transZ;
	}

	public Matrix getRotZ() {
		return rotZ;
	}
	
	/**
	 * Gets a jacobian matrix of this link
	 * @param rotoryVelocity
	 * @param prismaticVelocity
	 * @return the Jacobian
	 */
	public Matrix DhStepJacobian(double rotoryVelocity,double prismaticVelocity) {

		setJacobianMatrix(rotoryVelocity, prismaticVelocity);
		
		Matrix step = getTransZ_J();
		step = step.times(getRotZ_J());
		step = step.times(getTransX_J());
		step = step.times(getRotX_J());
		
		return step;
	}
	/**
	 * Sets up the 2 alterable Jacobian matrixs 
	 * @param rotory the rotory velocity of the Theta link
	 * @param prismatic the linear velocity of the D link
	 */
	private void setJacobianMatrix(double rotory,double prismatic){
		rotZ_J = new Matrix( new double [][] {	
				{1,0,0,					0	},
				{0,1,0,					0	},
				{0,0,1,	getD()+prismatic	},
				{0,0,0,					1	}});
		
		transZ_J  = new Matrix( new double [][] {	
				{Math.cos(getTheta()+rotory),-Math.sin(getTheta()+rotory),	0,	0},
				{Math.sin(getTheta()+rotory), Math.cos(getTheta()+rotory),	0,	0},
				{0,														0,	1,	0},
				{0,														0,	0,	1}});
	}
	public Matrix getRotX_J() {
		 if(rotX_J == null){
			 rotX_J = new Matrix( new double [][] {	
				{1,	0,						0,							0},
				{0,	Math.cos(getAlpha()),	-Math.sin(getAlpha()),		0},
				{0,	Math.sin(getAlpha()),	Math.cos(getAlpha()),		0},
				{0,	0,						0,							1}});
		 }
		return rotX_J;
	}
	
	public Matrix getTransX_J() {
		 if(transX_J == null){
			 transX_J= new Matrix( new double [][] {	
					{1,0,0,getR()},
					{0,1,0,0},
					{0,0,1,0},
					{0,0,0,1}});
		 }	
		return transX_J;
	}



	public Matrix getTransZ_J() {
		return transZ_J;
	}
	public Matrix getRotZ_J() {
		return rotZ_J;
	}
	
	@Override 
	public String toString(){
		String s="";
		s+=" Delta = "+d;
		s+=" Theta = "+Math.toDegrees(theta)+" deg";
		s+=" Radius = "+r;
		s+=" Alpha = "+Math.toDegrees(alpha)+" deg";
		return s;
	}

}
