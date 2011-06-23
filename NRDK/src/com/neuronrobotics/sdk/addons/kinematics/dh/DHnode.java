package com.neuronrobotics.sdk.addons.kinematics.dh;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.AbstractRotoryLink;

public class DHnode extends AbstractTransform{
	private AbstractRotoryLink link;
	private double D, R, Alpha;
	private double sinA,cosA;
	/**
	 * 
	 * @param D 	offset along previous z to the common normal
	 * @param R		length of the common normal (aka a, but if using this notation, do not confuse with α). Assuming a revolute joint, this is the radius about previous z.
	 * @param Alpha angle about common normal, from old z axis to new z axis
	 */
	public DHnode (AbstractRotoryLink link,double D,double R, double Alpha){
		setLink(link);
		setD(D);
		setR(R);
		setAlpha(Alpha);
	}
	
	public Matrix getTransform(){
		double theta = getLink().getCurrentAngle();
		double sThata = Math.sin(theta);
		double cTheta = Math.cos(theta);
		double [][] rotTheta ={
				{cTheta		,sThata	*-1	,0		,0},
				{sThata		,cTheta		,0		,0},
				{0			,0			,1		,0},
				{0			,0			,0		,1},
		};
		
		double [][] transD ={
				{1		,0		,0		,0},
				{0		,1		,0		,0},
				{0		,0		,1		,getD()},
				{0		,0		,0		,1},
		};
		
		double [][] transR ={
				{1		,0		,0		,getR()},
				{0		,1		,0		,0},
				{0		,0		,1		,0},
				{0		,0		,0		,1},
		};
		double [][] rotAlpha ={
				{1		,0		,0			,0},
				{0		,cosA	,sinA*-1	,0},
				{0		,sinA	,cosA		,0},
				{0		,0		,0			,1},
		};
		double [][] trans ={
				{1		,1		,1		,1},
				{1		,1		,1		,1},
				{1		,1		,1		,1},
				{1		,1		,1		,1},
		};
		
		for(int i=0;i<4;i++){
			for(int j=0;i<4;j++){
				trans[i][j]*=transD[i][j];
				trans[i][j]*=rotTheta[i][j];
				trans[i][j]*=transR[i][j];
				trans[i][j]*=rotAlpha[i][j];	
			}
		}	
		Matrix Trans = new Matrix(trans);
		return Trans;
	}
	public void setAlpha(double alpha) {
		Alpha = alpha;
		sinA=Math.sin(alpha);
		cosA=Math.cos(alpha);
	}
	public double getAlpha() {
		return Alpha;
	}
	public void setD(double d) {
		D = d;
	}
	public double getD() {
		return D;
	}
	public void setR(double r) {
		R = r;
	}
	public double getR() {
		return R;
	}
	public void setLink(AbstractRotoryLink link) {
		this.link = link;
	}
	public AbstractRotoryLink getLink() {
		return link;
	}
	
}
