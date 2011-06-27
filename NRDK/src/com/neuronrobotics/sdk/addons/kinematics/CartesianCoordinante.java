package com.neuronrobotics.sdk.addons.kinematics;

public class CartesianCoordinante {
	private double x,y,z;
	/**
	 * all units in millimeters
	 * @param x
	 * @param y
	 * @param z
	 */
	public CartesianCoordinante(double x,double y,double z){
		setX(x);
		setY(y);
		setZ(z);
	}
	private void setX(double x) {
		this.x = x;
	}
	public double getX() {
		return x;
	}
	private void setY(double y) {
		this.y = y;
	}
	public double getY() {
		return y;
	}
	private void setZ(double z) {
		this.z = z;
	}
	public double getZ() {
		return z;
	}
}
