package com.neuronrobotics.replicator.driver.delta;

// TODO: Auto-generated Javadoc
/**
 * The Class DeltaJointAngles.
 */
public class DeltaJointAngles {
	
	/** The theta3. */
	private double theta1, theta2,  theta3;
	
	/**
	 * All angles in radians.
	 *
	 * @param theta1 the theta1
	 * @param theta2 the theta2
	 * @param theta3 the theta3
	 */
	public DeltaJointAngles(double theta1, double theta2, double theta3){
		setTheta1(theta1);
		setTheta2(theta2);
		setTheta3(theta3);
	}
	
	/**
	 * Sets the theta1.
	 *
	 * @param theta1 the new theta1
	 */
	private void setTheta1(double theta1) {
		this.theta1 = theta1;
	}
	
	/**
	 * Gets the theta1.
	 *
	 * @return the theta1
	 */
	public double getTheta1() {
		return theta1;
	}
	
	/**
	 * Sets the theta2.
	 *
	 * @param theta2 the new theta2
	 */
	private void setTheta2(double theta2) {
		this.theta2 = theta2;
	}
	
	/**
	 * Gets the theta2.
	 *
	 * @return the theta2
	 */
	public double getTheta2() {
		return theta2;
	}
	
	/**
	 * Sets the theta3.
	 *
	 * @param theta3 the new theta3
	 */
	private void setTheta3(double theta3) {
		this.theta3 = theta3;
	}
	
	/**
	 * Gets the theta3.
	 *
	 * @return the theta3
	 */
	public double getTheta3() {
		return theta3;
	}
}
