package com.neuronrobotics.sdk.addons.kinematics.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.DHLink;
import com.neuronrobotics.sdk.common.Log;

import Jama.Matrix;

// TODO: Auto-generated Javadoc
/**
 * The Class TransformNR.
 */
public class TransformNR {
	
	/** The x. */
	private double x;
	
	/** The y. */
	private double y;
	
	/** The z. */
	private double z;
	
	/** The rotation. */
	private  RotationNR rotation;
	
	
	
	/**
	 * Instantiates a new transform nr.
	 *
	 * @param m the m
	 */
	public TransformNR(Matrix m){
		this.x=m.get(0, 3);
		this.y=m.get(1, 3);
		this.z=m.get(2, 3);
		this.setRotation(new RotationNR(m));
	}
	
	/**
	 * Instantiates a new transform nr.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 * @param rotx the rotx
	 * @param roty the roty
	 * @param rotz the rotz
	 */
	public TransformNR(double x, double y, double z, double w, double rotx, double roty, double rotz){
		this.x=x;
		this.y=y;
		this.z=z;
		this.setRotation(new RotationNR(new double[]{w,rotx,roty,rotz}));
	}
	
	/**
	 * Instantiates a new transform nr.
	 *
	 * @param cartesianSpaceVector the cartesian space vector
	 * @param rotationMatrix the rotation matrix
	 */
	public TransformNR(double[] cartesianSpaceVector, double[][] rotationMatrix) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.setRotation(new RotationNR(rotationMatrix));
	}
	
	/**
	 * Instantiates a new transform nr.
	 *
	 * @param cartesianSpaceVector the cartesian space vector
	 * @param quaternionVector the quaternion vector
	 */
	public TransformNR(double[] cartesianSpaceVector, double[] quaternionVector) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.setRotation(new RotationNR(quaternionVector));
	}
	
	/**
	 * Instantiates a new transform nr.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param q the q
	 */
	public TransformNR(double x, double y, double z, RotationNR q){
		this.x=x;
		this.y=y;
		this.z=z;
		this.setRotation(q);
	}
	
	/**
	 * Instantiates a new transform nr.
	 *
	 * @param cartesianSpaceVector the cartesian space vector
	 * @param q the q
	 */
	public TransformNR(double[] cartesianSpaceVector, RotationNR q) {
		this.x=cartesianSpaceVector[0];
		this.y=cartesianSpaceVector[1];
		this.z=cartesianSpaceVector[2];
		this.setRotation(q);
	}
	
	/**
	 * Instantiates a new transform nr.
	 */
	public TransformNR() {
		this.x=0;
		this.y=0;
		this.z=0;
		this.setRotation(new RotationNR());
	}
	
	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Gets the z.
	 *
	 * @return the z
	 */
	public double getZ() {
		return z;
	}
	
	/**
	 * Gets the rotation matrix array.
	 *
	 * @return the rotation matrix array
	 */
	public double [][] getRotationMatrixArray(){
		return getRotation().getRotationMatrix();
	}

	/**
	 * Gets the rotation matrix.
	 *
	 * @return the rotation matrix
	 */
	public RotationNR getRotationMatrix() {
		return getRotation();
	}

	/**
	 * Gets the rotation value.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the rotation value
	 */
	public double getRotationValue(int i,int j) {
		return getRotation().getRotationMatrix()[i][j];
	}
	
	/**
	 * Gets the rotation.
	 *
	 * @return the rotation
	 */
	public RotationNR getRotation() {

		return rotation;
	}
	
	/**
	 * Times.
	 *
	 * @param t the t
	 * @return the transform nr
	 */
	public TransformNR times(TransformNR t) {
		return new TransformNR(getMatrixTransform().times(t.getMatrixTransform()));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return getMatrixString(getMatrixTransform())+getRotation().toString();
	}
	
	/**
	 * Gets the matrix string.
	 *
	 * @param matrix the matrix
	 * @return the matrix string
	 */
	public static String getMatrixString(Matrix matrix){
		if(!Log.isPrinting()){
			return "no print transform, enable Log.enableSystemPrint(true)";
		}
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
	
	/**
	 * Gets the position array.
	 *
	 * @return the position array
	 */
	public double[] getPositionArray() {
		return new double[] {getX(),getY(),getZ()};
	}
	
	/**
	 * Gets the matrix transform.
	 *
	 * @return the matrix transform
	 */
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
	
	/**
	 * Gets the offset orentation magnitude.
	 *
	 * @param t the t
	 * @return the offset orentation magnitude
	 */
	public double getOffsetOrentationMagnitude(TransformNR t){ 
		double x = getRotation().getRotationMatrix2QuaturnionX()-t.getRotation().getRotationMatrix2QuaturnionX();
		double y = getRotation().getRotationMatrix2QuaturnionY()-t.getRotation().getRotationMatrix2QuaturnionY();
		double z = getRotation().getRotationMatrix2QuaturnionZ()-t.getRotation().getRotationMatrix2QuaturnionZ();
		double r = Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2) ));
		return r;
	}
	
	/**
	 * Gets the offset vector magnitude.
	 *
	 * @param t the t
	 * @return the offset vector magnitude
	 */
	public double getOffsetVectorMagnitude(TransformNR t){
		double x = getX()-t.getX();
		double y = getY()-t.getY();
		double z = getZ()-t.getZ();
		double r = Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2) ));
		return r;
	}
	
	/**
	 * Inverse.
	 *
	 * @return the transform nr
	 */
	public TransformNR inverse() {
		return new TransformNR(getMatrixTransform().inverse());	
	}
	
	/**
	 * Scale.
	 *
	 * @param scale the scale
	 * @return the transform nr
	 */
	public TransformNR scale(BigDecimal scale) {
		return scale(scale.doubleValue());
	}
	
	/**
	 * Scale.
	 *
	 * @param scale the scale
	 * @return the transform nr
	 */
	public TransformNR scale(double scale) {
		return new TransformNR(getMatrixTransform().times(Matrix.identity(4, 4).times(scale)));	
	}
	
	/**
	 * Copy.
	 *
	 * @return the transform nr
	 */
	public TransformNR copy() {
		return new TransformNR(getMatrixTransform());
	}
	
	/**
	 * Translate x.
	 *
	 * @param translation the translation
	 */
	public void translateX(double translation){
		x+=translation;
	}
	
	/**
	 * Translate y.
	 *
	 * @param translation the translation
	 */
	public void translateY(double translation){
		y+=translation;
	}
	
	/**
	 * Translate z.
	 *
	 * @param translation the translation
	 */
	public void translateZ(double translation){
		z+=translation;
	}
	
	/**
	 * Sets the x.
	 *
	 * @param translation the new x
	 */
	public void setX(double translation){
		x=translation;
	}
	
	/**
	 * Sets the y.
	 *
	 * @param translation the new y
	 */
	public void setY(double translation){
		y=translation;
	}
	
	/**
	 * Sets the z.
	 *
	 * @param translation the new z
	 */
	public void setZ(double translation){
		z=translation;
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
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

	/**
	 * Sets the rotation.
	 *
	 * @param rotation the new rotation
	 */
	public void setRotation(RotationNR rotation) {
		this.rotation = rotation;
	}

	

}
