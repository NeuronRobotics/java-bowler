package com.neuronrobotics.sdk.addons.kinematics.math;

import Jama.Matrix;

/*************************************************************************
 * Compilation: javac Quaternion.java Execution: java Quaternion
 * 
 * Data type for quaternions.
 * 
 * http://mathworld.wolfram.com/Quaternion.html
 * 
 * The data type is "immutable" so once you create and initialize a Quaternion,
 * you cannot change it.
 * 
 * % java Quaternion source:
 * http://introcs.cs.princeton.edu/java/32class/Quaternion.java.html
 *************************************************************************/

public class Rotation {
	double[][] rotationMatrix = new double[][] { { 1, 0, 0 }, { 0, 1, 0 },
			{ 0, 0, 1 } };

	public Rotation() {
	}

	public Rotation(double[][] rotationMatrix) {
		loadRotations(rotationMatrix);
	}

	public Rotation(double[] values) {
		this(values[0], values[1], values[2], values[3]);
	}
	/**
	 * Get a rotation matrix with a rotation around X
	 * @param rotationAngleDegrees in degrees
	 * @return the static matrix
	 */
	public static Rotation getRotationX(double rotationAngleDegrees) {
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

		return new Rotation(rotation);
	}
	/**
	 * Get a rotation matrix with a rotation around Y
	 * @param rotationAngleDegrees in degrees
	 * @return the static matrix
	 */
	public static Rotation getRotationY(double rotationAngleDegrees) {
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

		return new Rotation(rotation);
	}
	
	/**
	 * Get a rotation matrix with a rotation around Z
	 * @param rotationAngleDegrees in degrees
	 * @return the static matrix
	 */
	public static Rotation getRotationZ(double rotationAngleDegrees) {
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

		return new Rotation(rotation);
	}

	// create a new object with the given components
	public Rotation(double w, double x, double y, double z) {
		quaternion2RotationMatrix(w, x, y, z);
	}

	public Rotation(Matrix m) {
		double[][] rotation = new double[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				rotation[i][j] = m.get(i, j);
			}
		}
		loadRotations(rotation);
	}

	private void loadRotations(double[][] rotM) {
		if (rotM.length != 3)
			throw new RuntimeException("Must be 3x3 rotation matrix");
		for (int i = 0; i < 3; i++) {
			if (rotM[i].length != 3) {
				throw new RuntimeException("Must be 3x3 rotation matrix");
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// if(rotM[i][j]>1){
				// rotM[i][j]=0;//normalization
				// }
				rotationMatrix[i][j] = rotM[i][j];
			}
		}
	}

	public double[][] getRotationMatrix() {
		double[][] b = new double[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				b[i][j] = rotationMatrix[i][j];
			}
		}
		return b;
	}

	// return a string representation of the invoking object
	public String toString() {
		String s = "[\n";
		double[][] m = getRotationMatrix();
		for (int i = 0; i < 3; i++) {
			s += "[ ";
			for (int j = 0; j < 3; j++) {
				s += m[i][j] + "\t\t";
			}
			s += " ]\n";
		}
		s += "]";
		return "Quaturnion: "
				+"W="+ getRotationMatrix2QuaturnionW() + ", "
				+"x="+ getRotationMatrix2QuaturnionX() + ", "
				+"y="+ getRotationMatrix2QuaturnionY() + ", "
				+"z="+ getRotationMatrix2QuaturnionZ() + "";
	}
	
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
		return "Matrix = " + s ;
	}

	private void quaternion2RotationMatrix(double w, double x, double y, double z) {
		double norm = Math.sqrt(w * w + x * x + y * y + z * z);
		// we explicitly test norm against one here, saving a division
		// at the cost of a test and branch. Is it worth it?
		double s = (norm == 1f) ? 2f : (norm > 0f) ? 2f / norm : 0;
		// compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
		// will be used 2-4 times each.
		double xs = x * s;
		double ys = y * s;
		double zs = z * s;
		double xx = x * xs;
		double xy = x * ys;
		double xz = x * zs;
		double xw = w * xs;
		double yy = y * ys;
		double yz = y * zs;
		double yw = w * ys;
		double zz = z * zs;
		double zw = w * zs;

		// using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
		rotationMatrix[0][0] = 1 - (yy + zz);
		rotationMatrix[0][1] = (xy - zw);
		rotationMatrix[0][2] = (xz + yw);
		
		rotationMatrix[1][0] = (xy + zw);
		rotationMatrix[1][1] = 1 - (xx + zz);
		rotationMatrix[1][2] = (yz - xw);
		
		rotationMatrix[2][0] = (xz - yw);
		rotationMatrix[2][1] = (yz + xw);
		rotationMatrix[2][2] = 1 - (xx + yy);
		
		toString(rotationMatrix);
	}

	public double getRotationMatrix2QuaturnionW() {
		double temp = 0.5 * Math.sqrt(1 + rotationMatrix[0][0]+ rotationMatrix[1][1] + rotationMatrix[2][2]);
		return temp;
	}

	public double getRotationMatrix2QuaturnionX() {
		double temp = 0.5 * Math.sqrt(1 + rotationMatrix[0][0]+ rotationMatrix[1][1] + rotationMatrix[2][2]);
		return (rotationMatrix[2][1] - rotationMatrix[1][2]) * 0.25 / temp;
		}

	public double getRotationMatrix2QuaturnionY() {
		double temp = 0.5 * Math.sqrt(1 + rotationMatrix[0][0]+ rotationMatrix[1][1] + rotationMatrix[2][2]);
		return (rotationMatrix[0][2] - rotationMatrix[2][0]) * 0.25 / temp;
	}

	public double getRotationMatrix2QuaturnionZ() {
		double temp = 0.5 * Math.sqrt(1 + rotationMatrix[0][0]+ rotationMatrix[1][1] + rotationMatrix[2][2]);
		return (rotationMatrix[1][0] - rotationMatrix[0][1]) * 0.25 / temp;
	}

}
