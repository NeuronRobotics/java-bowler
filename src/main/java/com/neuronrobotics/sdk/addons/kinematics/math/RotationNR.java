package com.neuronrobotics.sdk.addons.kinematics.math;

import Jama.Matrix;

/**
 * This class is to represent a 3x3 rotation sub-matrix
 * This class also contains static methods for dealing with 3x3 rotations.
 * @author Kevin Harrington
 *
 */

public class RotationNR {
	double[][] rotationMatrix = new double[][] { { 1, 0, 0 }, { 0, 1, 0 },
			{ 0, 0, 1 } };
	/**
	 * Null constructor forms a 
	 */
	public RotationNR() {
	}

	public RotationNR(double[][] rotationMatrix) {
		loadRotations(rotationMatrix);
	}

	public RotationNR(double[] values) {
		this(values[0], values[1], values[2], values[3]);
	}
	/**
	 * Get a rotation matrix with a rotation around X
	 * @param rotationAngleDegrees in degrees
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
	 * Get a rotation matrix with a rotation around Y
	 * @param rotationAngleDegrees in degrees
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
	 * Get a rotation matrix with a rotation around Z
	 * @param rotationAngleDegrees in degrees
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
	
	// create a new object with the given simplified rotations
	public RotationNR( double x, double y, double z) {
		TransformNR tmp = new TransformNR();
		
		tmp = tmp.times(new TransformNR(0, 0, 0, RotationNR.getRotationX(x)));
		tmp = tmp.times(new TransformNR(0, 0, 0, RotationNR.getRotationY(y)));
		tmp = tmp.times(new TransformNR(0, 0, 0, RotationNR.getRotationZ(z)));
		loadRotations(tmp.getRotationMatrixArray());
	}

	// create a new object with the given components
	public RotationNR(double w, double x, double y, double z) {
		quaternion2RotationMatrix(w, x, y, z);
	}

	public RotationNR(Matrix m) {
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

	protected void quaternion2RotationMatrix(double w, double x, double y, double z) {
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
	
	/**
	This requires a pure rotation matrix 'm' as input.
	from http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToAngle/
	*/
	public double [] toAxisAngle() {
	  double angle,x,y,z; // variables for result
		double epsilon = 0.01; // margin to allow for rounding errors
		double epsilon2 = 0.1; // margin to distinguish between 0 and 180 degrees
		// optional check that input is pure rotation, 'isRotationMatrix' is defined at:
		// http://www.euclideanspace.com/maths/algebra/matrix/orthogonal/rotation/
		if ((Math.abs(rotationMatrix[0][1]-rotationMatrix[1][0])< epsilon)
		  && (Math.abs(rotationMatrix[0][2]-rotationMatrix[2][0])< epsilon)
		  && (Math.abs(rotationMatrix[1][2]-rotationMatrix[2][1])< epsilon)) {
			// singularity found
			// first check for identity matrix which must have +1 for all terms
			//  in leading diagonaland zero in other terms
			if ((Math.abs(rotationMatrix[0][1]+rotationMatrix[1][0]) < epsilon2)
			  && (Math.abs(rotationMatrix[0][2]+rotationMatrix[2][0]) < epsilon2)
			  && (Math.abs(rotationMatrix[1][2]+rotationMatrix[2][1]) < epsilon2)
			  && (Math.abs(rotationMatrix[0][0]+rotationMatrix[1][1]+rotationMatrix[2][2]-3) < epsilon2)) {
				// this singularity is identity matrix so angle = 0
				return new double[]{0,1,0,0}; // zero angle, arbitrary axis
			}
			// otherwise this singularity is angle = 180
			angle = Math.PI;
			double xx = (rotationMatrix[0][0]+1)/2;
			double yy = (rotationMatrix[1][1]+1)/2;
			double zz = (rotationMatrix[2][2]+1)/2;
			double xy = (rotationMatrix[0][1]+rotationMatrix[1][0])/4;
			double xz = (rotationMatrix[0][2]+rotationMatrix[2][0])/4;
			double yz = (rotationMatrix[1][2]+rotationMatrix[2][1])/4;
			if ((xx > yy) && (xx > zz)) { // m[0][0] is the largest diagonal term
				if (xx< epsilon) {
					x = 0;
					y = 0.7071;
					z = 0.7071;
				} else {
					x = Math.sqrt(xx);
					y = xy/x;
					z = xz/x;
				}
			} else if (yy > zz) { // m[1][1] is the largest diagonal term
				if (yy< epsilon) {
					x = 0.7071;
					y = 0;
					z = 0.7071;
				} else {
					y = Math.sqrt(yy);
					x = xy/y;
					z = yz/y;
				}	
			} else { // m[2][2] is the largest diagonal term so base result on this
				if (zz< epsilon) {
					x = 0.7071;
					y = 0.7071;
					z = 0;
				} else {
					z = Math.sqrt(zz);
					x = xz/z;
					y = yz/z;
				}
			}
			return  new double[]{angle,x,y,z}; // return 180 deg rotation
		}
		// as we have reached here there are no singularities so we can handle normally
		double s = Math.sqrt((rotationMatrix[2][1] - rotationMatrix[1][2])*(rotationMatrix[2][1] - rotationMatrix[1][2])
			+(rotationMatrix[0][2] - rotationMatrix[2][0])*(rotationMatrix[0][2] - rotationMatrix[2][0])
			+(rotationMatrix[1][0] - rotationMatrix[0][1])*(rotationMatrix[1][0] - rotationMatrix[0][1])); // used to normalise
		if (Math.abs(s) < 0.001) s=1; 
			// prevent divide by zero, should not happen if matrix is orthogonal and should be
			// caught by singularity test above, but I've left it in just in case
		angle = Math.acos(( rotationMatrix[0][0] + rotationMatrix[1][1] + rotationMatrix[2][2] - 1)/2);
		x = (rotationMatrix[2][1] - rotationMatrix[1][2])/s;
		y = (rotationMatrix[0][2] - rotationMatrix[2][0])/s;
		z = (rotationMatrix[1][0] - rotationMatrix[0][1])/s;
	   return new  double[]{angle,x,y,z};
	}

	
	private double calculateAxisAngle(double quaturnian){
		double w = getRotationMatrix2QuaturnionW();
		quaturnian=Math.abs(quaturnian);
		double s = Math.sqrt(1-w*w);
		if(s<.001)
			return quaturnian;
		return quaturnian/s;
	}
	

	public double getRotationX() {

		return calculateAxisAngle(getRotationMatrix2QuaturnionX());

	}

	public double getRotationY() {
		return calculateAxisAngle(getRotationMatrix2QuaturnionY());
	}

	public double getRotationZ() {
		return calculateAxisAngle(getRotationMatrix2QuaturnionZ());
	}

	public double getRotationMatrix2QuaturnionW() {
		double temp = 0.5 * Math.sqrt(1 + rotationMatrix[0][0]+ rotationMatrix[1][1] + rotationMatrix[2][2]);
		if(temp>1)
			throw new RuntimeException("Matrix needs normalization");
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
