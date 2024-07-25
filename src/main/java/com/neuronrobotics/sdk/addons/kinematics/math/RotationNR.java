package com.neuronrobotics.sdk.addons.kinematics.math;

import Jama.Matrix;

import org.apache.commons.math3.geometry.euclidean.threed.CardanEulerSingularityException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

import com.google.gson.annotations.Expose;
import com.neuronrobotics.sdk.common.Log;

// TODO: Auto-generated Javadoc
/**
 * This class is to represent a 3x3 rotation sub-matrix This class also contains
 * static methods for dealing with 3x3 rotations.
 * 
 * @author Kevin Harrington
 *
 */

public class RotationNR {

	/** The rotation matrix. */
	// double[][] rotationMatrix = ;

@Expose (serialize = true, deserialize = true)
	double w=1;
@Expose (serialize = true, deserialize = true)
	double x=0;
@Expose (serialize = true, deserialize = true)
	double y=0;
@Expose (serialize = true, deserialize = true)
	double z=0;
	//private Rotation storage = new Rotation(1, 0, 0, 0, false);
@Expose (serialize = false, deserialize = false)
	private static RotationOrder order = RotationOrder.ZYX;
@Expose (serialize = false, deserialize = false)
	private static RotationConvention convention = RotationConvention.VECTOR_OPERATOR;
	

	/**
	 * Null constructor forms a.
	 */
	public RotationNR() {
	}

	/**
	 * Instatiate using the
	 * org.apache.commons.math3.geometry.euclidean.threed.Rotation .
	 * 
	 * @param store
	 *            A org.apache.commons.math3.geometry.euclidean.threed.Rotation
	 *            instance
	 */
	public RotationNR(Rotation store) {
		setStorage(store);
	}

	/**
	 * Instantiates a new rotation nr.
	 *
	 ** @param tilt
	 *            the tilt in Degrees
	 * @param azimuth
	 *            the azimuth in Degrees 
	 * @param elevation
	 *            the elevation in Degrees
	 */
	// create a new object with the given simplified rotations
	public RotationNR(double tilt, double azimuth, double elevation) {
		if (Double.isNaN(tilt))
			throw new RuntimeException("Value can not be NaN");
		if (Double.isNaN(azimuth))
			throw new RuntimeException("Value can not be NaN");
		if (Double.isNaN(elevation))
			throw new RuntimeException("Value can not be NaN");
		if (elevation > 90 || elevation < -90) {
			throw new RuntimeException("Elevation can not be greater than 90 nor less than -90");
		}
		loadFromAngles(tilt, azimuth, elevation);
		if (Double.isNaN(getRotationMatrix2QuaturnionW()) || Double.isNaN(getRotationMatrix2QuaturnionX())
				|| Double.isNaN(getRotationMatrix2QuaturnionY()) || Double.isNaN(getRotationMatrix2QuaturnionZ())) {
			Log.error("Failing to set proper angle, jittering");
			loadFromAngles(tilt + Math.random() * .02 + .001, azimuth + Math.random() * .02 + .001,
					elevation + Math.random() * .02 + .001);
		}

	}

	/**
	 * Instantiates a new rotation nr.
	 *
	 * @param rotationMatrix
	 *            the rotation matrix
	 */
	public RotationNR(double[][] rotationMatrix) {
		loadRotations(rotationMatrix);
	}

	/**
	 * Instantiates a new rotation nr.
	 *
	 * @param values
	 *            the values
	 */
	public RotationNR(double[] values) {
		this(values[0], values[1], values[2], values[3]);
	}

	/**
	 * Get a rotation matrix with a rotation around X.
	 *
	 * @param rotationAngleDegrees
	 *            in degrees
	 * @return the static matrix
	 */
	public static RotationNR getRotationX(double rotationAngleDegrees) {
		double[][] rotation = new double[3][3];
		double rotationAngleRadians = Math.PI / 180 * rotationAngleDegrees;

		// Rotation matrix, 1st column
		rotation[0][0] = 1;
		rotation[1][0] = 0;
		rotation[2][0] = 0;
		// Rotation matrix, 2nd column
		rotation[0][1] = 0;
		rotation[1][1] = Math.cos(rotationAngleRadians);
		rotation[2][1] = Math.sin(rotationAngleRadians);
		// Rotation matrix, 3rd column
		rotation[0][2] = 0;
		rotation[1][2] = -Math.sin(rotationAngleRadians);
		rotation[2][2] = Math.cos(rotationAngleRadians);

		return new RotationNR(rotation);
	}

	/**
	 * Get a rotation matrix with a rotation around Y.
	 *
	 * @param rotationAngleDegrees
	 *            in degrees
	 * @return the static matrix
	 */
	public static RotationNR getRotationY(double rotationAngleDegrees) {
		double[][] rotation = new double[3][3];
		double rotationAngleRadians = Math.PI / 180 * rotationAngleDegrees;

		// Rotation matrix, 1st column
		rotation[0][0] = Math.cos(rotationAngleRadians);
		rotation[1][0] = 0;
		rotation[2][0] = -Math.sin(rotationAngleRadians);
		// Rotation matrix, 2nd column
		rotation[0][1] = 0;
		rotation[1][1] = 1;
		rotation[2][1] = 0;
		// Rotation matrix, 3rd column
		rotation[0][2] = Math.sin(rotationAngleRadians);
		rotation[1][2] = 0;
		rotation[2][2] = Math.cos(rotationAngleRadians);

		return new RotationNR(rotation);
	}

	/**
	 * Get a rotation matrix with a rotation around Z.
	 *
	 * @param rotationAngleDegrees
	 *            in degrees
	 * @return the static matrix
	 */
	public static RotationNR getRotationZ(double rotationAngleDegrees) {
		double[][] rotation = new double[3][3];
		double rotationAngleRadians = Math.PI / 180 * rotationAngleDegrees;

		// Rotation matrix, 1st column
		rotation[0][0] = Math.cos(rotationAngleRadians);
		rotation[1][0] = Math.sin(rotationAngleRadians);
		rotation[2][0] = 0;
		// Rotation matrix, 2nd column
		rotation[0][1] = -Math.sin(rotationAngleRadians);
		rotation[1][1] = Math.cos(rotationAngleRadians);
		rotation[2][1] = 0;
		// Rotation matrix, 3rd column
		rotation[0][2] = 0;
		rotation[1][2] = 0;
		rotation[2][2] = 1;

		return new RotationNR(rotation);
	}

	/**
	 * Instantiates a new rotation nr.
	 *
	 * @param w
	 *            the w
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	// create a new object with the given components
	public RotationNR(double w, double x, double y, double z) {
		quaternion2RotationMatrix(w, x, y, z);
	}

	/**
	 * Instantiates a new rotation nr.
	 *
	 * @param m
	 *            the m
	 */
	public RotationNR(Matrix m) {
		double[][] rotation = new double[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				rotation[i][j] = m.get(i, j);
			}
		}
		loadRotations(rotation);
	}

	/**
	 * Load rotations.
	 *
	 * @param rotM
	 *            the rot m
	 */
	private void loadRotations(double[][] rotM) {
		if (rotM.length != 3)
			throw new RuntimeException("Must be 3x3 rotation matrix");
		for (int i = 0; i < 3; i++) {
			if (rotM[i].length != 3) {
				throw new RuntimeException("Must be 3x3 rotation matrix");
			}
		}
		setStorage(new Rotation(rotM, 0.00001));
	}

	/**
	 * Gets the rotation matrix.
	 *
	 * @return the rotation matrix
	 */
	public double[][] getRotationMatrix() {

		return getStorage().getMatrix();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	// return a string representation of the invoking object
	public String toString() {
		try{
			return "Quaturnion: " + "W=" + getRotationMatrix2QuaturnionW() + ", " + "x=" + getRotationMatrix2QuaturnionX()
				+ ", " + "y=" + getRotationMatrix2QuaturnionY() + ", " + "z=" + getRotationMatrix2QuaturnionZ() + "\n"
				+ "Rotation angle (degrees): " + "az= " + Math.toDegrees(getRotationAzimuth()) + ", elev= "
				+ Math.toDegrees(getRotationElevation()) + ", tilt=" + Math.toDegrees(getRotationTilt());
		}catch(Exception ex){
			return "Rotation error"+ex.getLocalizedMessage();
		}
		
	}

	/**
	 * To string.
	 *
	 * @param array
	 *            the array
	 * @return the string
	 */
	// return a string representation of the invoking object
	public String toString(double[][] array) {
		String s = "[\n";
		for (int i = 0; i < 3; i++) {
			s += "[ ";
			for (int j = 0; j < 3; j++) {
				s += array[i][j] + "\t\t";
			}
			s += " ]\n";
		}
		s += "]";
		return "Matrix = " + s;
	}

	/**
	 * Quaternion2 rotation matrix.
	 *
	 * @param w
	 *            the w
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	protected void quaternion2RotationMatrix(double w, double x, double y, double z) {
		if (Double.isNaN(w))
			throw new RuntimeException("Value can not be NaN");
		if (Double.isNaN(x))
			throw new RuntimeException("Value can not be NaN");
		if (Double.isNaN(y))
			throw new RuntimeException("Value can not be NaN");
		if (Double.isNaN(z))
			throw new RuntimeException("Value can not be NaN");
		this.w=w;
		this.x= -x;
		this.y= -y;
		this.z= -z;
	}

	/**
	 * Bound.
	 *
	 * @param low
	 *            the low
	 * @param high
	 *            the high
	 * @param n
	 *            the n
	 * @return true, if successful
	 */
	public static boolean bound(double low, double high, double n) {
		return n >= low && n <= high;
	}

	private void loadFromAngles(double tilt, double azimuth, double elevation) {
		setStorage(new Rotation(getOrder(), getConvention(), Math.toRadians(azimuth), Math.toRadians(elevation),
				Math.toRadians(tilt)));
	}
	/**
	 * Gets the rotation tilt.
	 *
	 * @return the rotation tilt in radians
	 */
	public double getRotationTiltRadians() {
		return getAngle(2);
	}

	/**
	 * Gets the rotation elevation.
	 *
	 * @return the rotation elevation in radians
	 */
	public double getRotationElevationRadians() {
		return getAngle(1);

	}

	/**
	 * Gets the rotation azimuth.
	 *
	 * @return the rotation azimuth in radians
	 */
	
	public double getRotationAzimuthRadians() {
		return getAngle(0);
	}
	/**
	 * Gets the rotation tilt.
	 *
	 * @return the rotation tilt in degrees
	 */
	public double getRotationTiltDegrees() {
		return Math.toDegrees(getRotationTiltRadians());
	}

	/**
	 * Gets the rotation elevation.
	 *
	 * @return the rotation elevation in degrees
	 */
	public double getRotationElevationDegrees() {
		return Math.toDegrees(getRotationElevationRadians());

	}

	/**
	 * Gets the rotation azimuth.
	 *
	 * @return the rotation azimuth in degrees
	 */
	
	public double getRotationAzimuthDegrees() {
		return Math.toDegrees( getRotationAzimuthRadians());
	}
	/**
	 * Gets the rotation tilt.
	 *
	 * @return the rotation tilt in radians
	 */
	@Deprecated
	public double getRotationTilt() {
		return getRotationTiltRadians();
	}

	/**
	 * Gets the rotation elevation.
	 *
	 * @return the rotation elevation in radians
	 */
	@Deprecated
	public double getRotationElevation() {
		return getRotationElevationRadians();

	}

	/**
	 * Gets the rotation azimuth.
	 *
	 * @return the rotation azimuth in radians
	 */
	@Deprecated
	public double getRotationAzimuth() {
		return  getRotationAzimuthRadians();
	}
	private void simpilfyAngles(double [] angles){
		double epsilon=1.0E-7;
		if(Math.abs(angles[0] - Math.toRadians(180)) < epsilon&&
		   Math.abs(angles[2] - Math.toRadians(180)) < epsilon	){
			if(!(Math.abs(getRotationMatrix2QuaturnionZ())>epsilon))
				angles[0]=0;
			if(!(Math.abs(getRotationMatrix2QuaturnionX())>epsilon))
				angles[2]=0;
		}
	}
	private double eulerFix(double offsetSize, int index){
		double offset = (index==1?offsetSize:0);
		TransformNR current = new TransformNR(0, 0, 0, this);
		TransformNR newTf = current.times(new TransformNR(0, 0, 0, new RotationNR(0,0,Math.toDegrees(offsetSize))));
		double[] angles = newTf.getRotation().getStorage().getAngles(getOrder(), getConvention());
		simpilfyAngles(angles);
		double finalResult= angles[index];
		return finalResult+offset;
	}
	private double getAngle(int index){
		
		try {
			return getStorage().getAngles(getOrder(), getConvention())[index];
		} catch (CardanEulerSingularityException e) {
			try {
				return eulerFix( Math.toRadians(0.001),  index);
			} catch (CardanEulerSingularityException ex) {
				return eulerFix(  Math.toRadians(-0.001),  index);
	
			}
		}
	}
	 

	/**
	 * Gets the rotation matrix2 quaturnion w.
	 *
	 * @return the rotation matrix2 quaturnion w
	 */
	public double getRotationMatrix2QuaturnionW() {
		return w;
	}

	/**
	 * Gets the rotation matrix2 quaturnion x.
	 *
	 * @return the rotation matrix2 quaturnion x
	 */
	public double getRotationMatrix2QuaturnionX() {
		return -x;
	}

	/**
	 * Gets the rotation matrix2 quaturnion y.
	 *
	 * @return the rotation matrix2 quaturnion y
	 */
	public double getRotationMatrix2QuaturnionY() {
		return -y;
	}

	/**
	 * Gets the rotation matrix2 quaturnion z.
	 *
	 * @return the rotation matrix2 quaturnion z
	 */
	public double getRotationMatrix2QuaturnionZ() {
		return -z;
	}

	public static RotationOrder getOrder() {
		return order;
	}

	public static void setOrder(RotationOrder o) {
		order = o;
	}

	public static RotationConvention getConvention() {
		return convention;
	}

	public static void setConvention(RotationConvention convention) {
		RotationNR.convention = convention;
	}

	private Rotation getStorage() {
		return new Rotation(w,x,y,z,false);
	}

	private void setStorage(Rotation storage) {
		w=storage.getQ0();
		x=storage.getQ1();
		y=storage.getQ2();
		z=storage.getQ3();
	}

	public void set(double[][] poseRot) {
		loadRotations(poseRot);
	}



}