package com.neuronrobotics.sdk.addons.kinematics.math;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.DHLink;

import Jama.Matrix;

public class TransformNR {
	private double x;
	private double y;
	private double z;
	private  RotationNR rotation;
	
	
	
	public TransformNR(Matrix m){
		this.x=m.get(0, 3);
		this.y=m.get(1, 3);
		this.z=m.get(2, 3);
		this.setRotation(new RotationNR(m));
	}
	
	public TransformNR(double x, double y, double z, double w, double rotx, double roty, double rotz){
		this.x=x;
		this.y=y;
		this.z=z;
		this.setRotation(new RotationNR(w,rotx,roty,rotz));
	}
	public TransformNR(double[] cartesianSpaceVector, double[][] rotationMatrix) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.setRotation(new RotationNR(rotationMatrix));
	}
	public TransformNR(double[] cartesianSpaceVector, double[] quaternionVector) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.setRotation(new RotationNR(quaternionVector));
	}
	
	public TransformNR(double x, double y, double z, RotationNR q){
		this.x=x;
		this.y=y;
		this.z=z;
		this.setRotation(q);
	}
	
	public TransformNR(double[] cartesianSpaceVector, RotationNR q) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.setRotation(q);
	}
	public TransformNR() {
		this.x=0;
		this.y=0;
		this.z=0;
		this.setRotation(new RotationNR());
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public double [][] getRotationMatrixArray(){
		return getRotation().getRotationMatrix();
	}

	public RotationNR getRotationMatrix() {
		return getRotation();
	}

	public double getRotationValue(int i,int j) {
		return getRotation().getRotationMatrix()[i][j];
	}
	public RotationNR getRotation() {

		return rotation;
	}
	public TransformNR times(TransformNR t) {
		return new TransformNR(getMatrixTransform().times(t.getMatrixTransform()));
	}
	@Override
	public String toString(){
		return getMatrixString(getMatrixTransform())+getRotation().toString();
	}
	
	public static String getMatrixString(Matrix matrix){
		String s = "{\n";
		double [][] m = matrix.getArray();
		
		int across = m.length;
		int down = m[0].length;
		
		for(int i=0;i<across;i++){
			s+="{ ";
			for(int j=0;j<down;j++){
				if(m[i][j]<0)
					s+=new DecimalFormat( "000.00" ).format(m[i][j]);
				else
					s+=new DecimalFormat( "0000.00" ).format(m[i][j]);
				if(j<down-1)
					s+=",";
				s+="\t";
			}
			s+=" }";
			if(i<across-1)
				s+=",";
			s+="\n";
		}
		return s+"}\n";
	}
	
	public double[] getPositionArray() {
		return new double[] {getX(),getY(),getZ()};
	}
	
	public Matrix getMatrixTransform(){
		double [][] transform = new double [4][4];
		double [][] rotation = getRotationMatrixArray();
		
		
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				transform[i][j]=rotation[i][j];
			}
		}
		for(int i=0;i<3;i++){
			transform[3][i]=0;
		}
		transform[3][3]=1;
		transform[0][3]=getX();
		transform[1][3]=getY();
		transform[2][3]=getZ();
		
		
		return new Matrix(transform);
	}
	public double getOffsetOrentationMagnitude(TransformNR t){ 
		double x = getRotation().getRotationMatrix2QuaturnionX()-t.getRotation().getRotationMatrix2QuaturnionX();
		double y = getRotation().getRotationMatrix2QuaturnionY()-t.getRotation().getRotationMatrix2QuaturnionY();
		double z = getRotation().getRotationMatrix2QuaturnionZ()-t.getRotation().getRotationMatrix2QuaturnionZ();
		double r = Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2) ));
		return r;
	}
	public double getOffsetVectorMagnitude(TransformNR t){
		double x = getX()-t.getX();
		double y = getY()-t.getY();
		double z = getZ()-t.getZ();
		double r = Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2) ));
		return r;
	}
	
	public TransformNR inverse() {
		return new TransformNR(getMatrixTransform().inverse());	
	}

	public TransformNR copy() {
		return new TransformNR(getMatrixTransform());
	}
	
	public void translateX(double translation){
		x+=translation;
	}
	public void translateY(double translation){
		y+=translation;
	}
	public void translateZ(double translation){
		z+=translation;
	}
	
	/*
	 * 
	 * Generate the xml configuration to generate an XML of this robot. 
	 */
	public String getXml(){
		String xml = 	"\t<x>"+x+"</x>\n"+
						"\t<y>"+y+"</y>\n"+
						"\t<z>"+z+"</z>\n"+
						"\t<rotw>"+getRotation().getRotationMatrix2QuaturnionW()+"</rotw>\n"+
						"\t<rotx>"+getRotation().getRotationMatrix2QuaturnionX()+"</rotx>\n"+
						"\t<roty>"+getRotation().getRotationMatrix2QuaturnionY()+"</roty>\n"+
						"\t<rotz>"+getRotation().getRotationMatrix2QuaturnionZ()+"</rotz>";

		return xml;
	}

	public void setRotation(RotationNR rotation) {
		this.rotation = rotation;
	}

	

}
