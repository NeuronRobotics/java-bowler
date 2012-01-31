package com.neuronrobotics.sdk.addons.kinematics.dh;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.AbstractRotoryLink;
import com.neuronrobotics.sdk.addons.kinematics.ILinkListener;

public class DHnode extends AbstractTransform implements ILinkListener{
	private AbstractRotoryLink link;
	private double D, R, Alpha;
	private double sinA,cosA;
	double theta;
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
	private double[][] getMatrixData(){
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
		
		//Not calculating this right...
//		for(int i=0;i<3;i++){
//			for(int j=0;j<3;j++){
//				trans[i][j]*=transD[i][j];
//				trans[i][j]*=rotTheta[i][j];
//				trans[i][j]*=transR[i][j];
//				trans[i][j]*=rotAlpha[i][j];	
//			}
//		}	
		return trans;
	}
	public Matrix getTransform(){
		Matrix Trans = new Matrix(getMatrixData());
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
		link.addLinkListener(this);
		this.link = link;
		theta = getLink().getCurrentAngle();
	}
	public AbstractRotoryLink getLink() {
		return link;
	}
	public String toString(){
		String s="D-H node";
		s+="\n\tD="+D;
		s+="\n\tAlpha="+Alpha;
		s+="\n\tR="+R;
		s+="\n\tTheta="+theta;
		s+="\n\tMatrix=[\n";
		double[][] d= getMatrixData();
		for(int i=0;i<4;i++){
			
			for(int j=0;j<4;j++){
				if(j==0)
					s+="\t\t[";
				s+=d[i][j];
				if(j<3)
					s+=" , ";
			}
			if(i<3)
				s+=" ], \n";
		}
		s+=" ] ]\n";
		return s;
	}
	@Override
	public void onLinkPositionUpdate(AbstractLink source,double engineeringUnitsValue) {
		theta=engineeringUnitsValue;
	}
	
}
