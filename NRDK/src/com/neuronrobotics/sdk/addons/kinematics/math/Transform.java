package com.neuronrobotics.sdk.addons.kinematics.math;

import java.text.DecimalFormat;

import Jama.Matrix;

public class Transform {
	private final double x,y,z;
	private final Rotation rotation;
	
	public Transform(Matrix m){
		this.x=m.get(0, 3);
		this.y=m.get(1, 3);
		this.z=m.get(2, 3);
		this.rotation = new Rotation(m);
	}
	
	public Transform(double x, double y, double z, double w, double rotx, double roty, double rotz){
		this.x=x;
		this.y=y;
		this.z=z;
		this.rotation = new Rotation(w,rotx,roty,rotz);
	}
	public Transform(double[] cartesianSpaceVector, double[][] rotationMatrix) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.rotation = new Rotation(rotationMatrix);
	}
	public Transform(double[] cartesianSpaceVector, double[] quaternionVector) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.rotation = new Rotation(quaternionVector);
	}
	
	public Transform(double x, double y, double z, Rotation q){
		this.x=x;
		this.y=y;
		this.z=z;
		this.rotation = q;
	}
	
	public Transform(double[] cartesianSpaceVector, Rotation q) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.rotation = q;
	}
	public Transform() {
		this.x=0;
		this.y=0;
		this.z=0;
		this.rotation = new Rotation();
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
		return rotation.getRotationMatrix();
	}

	public Rotation getRotationMatrix() {
		return rotation;
	}

	public double getRotationValue(int i,int j) {
		return rotation.getRotationMatrix()[i][j];
	}
	public Rotation getRotation() {

		return rotation;
	}
	public Transform times(Transform t) {
		return new Transform(getMatrixTransform().times(t.getMatrixTransform()));
	}
	
	public String toString(){
		String s = "{\n";
		double [][] m = getMatrixTransform().getArray();
		for(int i=0;i<4;i++){
			s+="{ ";
			for(int j=0;j<4;j++){
				if(m[i][j]<0)
					s+=new DecimalFormat( "000.00" ).format(m[i][j]);
				else
					s+=new DecimalFormat( "0000.00" ).format(m[i][j]);
				if(j<3)
					s+=",";
				s+="\t";
			}
			s+=" }";
			if(i<3)
				s+=",";
			s+="\n";
		}
		return s+"}\n"+getRotation().toString();
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
	public double getOffsetOrentationMagnitude(Transform t){ 
		double x = getRotation().getRotationMatrix2QuaturnionX()-t.getRotation().getRotationMatrix2QuaturnionX();
		double y = getRotation().getRotationMatrix2QuaturnionY()-t.getRotation().getRotationMatrix2QuaturnionY();
		double z = getRotation().getRotationMatrix2QuaturnionZ()-t.getRotation().getRotationMatrix2QuaturnionZ();
		double r = Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2) ));
		return r;
	}
	public double getOffsetVectorMagnitude(Transform t){
		double x = getX()-t.getX();
		double y = getY()-t.getY();
		double z = getZ()-t.getZ();
		double r = Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2) ));
	
		
		return r;
	}
	
	public Transform inverse() {
		return new Transform(getMatrixTransform().inverse());	
	}

	public Transform copy() {
		return new Transform(getMatrixTransform());
	}
	

}
