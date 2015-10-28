package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * This class represents the Delta position of the robot in the robots co-ordinate system. 
 * Only the delta y, delta x and delta orentation relative to the robots current position
 * should be recorded here. 
 * @author Kevin Harrington
 *
 */

public class RobotLocationData {
	
	/** The o. */
	private double x,y,o;
	
	/** The arm. */
	private TransformNR arm = new TransformNR();
	
	/**
	 * Instantiates a new robot location data.
	 *
	 * @param deltaX the delta x
	 * @param deltaY the delta y
	 * @param deltaOrentation the delta orentation
	 */
	public RobotLocationData(double deltaX, double deltaY, double deltaOrentation){
		 setX(deltaX);
		 setY(deltaY);
		 setO(deltaOrentation);
	}
	
	/**
	 * Sets the arm location.
	 *
	 * @param arm the new arm location
	 */
	public void setArmLocation(TransformNR arm){
		this.arm = arm;
		
	}
	
	/**
	 * Gets the arm transform.
	 *
	 * @return the arm transform
	 */
	public TransformNR getArmTransform(){
		return arm;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s="delta: x="+x+" y="+y+" orentation="+o; 
		return s;
	}
	
	/**
	 * Sets the x.
	 *
	 * @param x the new x
	 */
	private void setX(double x) {
		this.x = x;
	}
	
	/**
	 * Gets the delta x.
	 *
	 * @return the delta x
	 */
	public double getDeltaX() {
		return x;
	}
	
	/**
	 * Sets the y.
	 *
	 * @param y the new y
	 */
	private void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Gets the delta y.
	 *
	 * @return the delta y
	 */
	public double getDeltaY() {
		return y;
	}
	
	/**
	 * Sets the o.
	 *
	 * @param o the new o
	 */
	private void setO(double o) {
		this.o = o;
	}
	
	/**
	 * Gets the delta orentation.
	 *
	 * @return the delta orentation
	 */
	public double getDeltaOrentation() {
		return o;
	}
}
