package com.neuronrobotics.addons.driving;

import java.text.DecimalFormat;

public class DataPoint {
	private double range;
	private double angle;
	/**
	 * 
	 * @param range a distance in MM
	 * @param angle angle in degrees
	 */
	public DataPoint(double range, double angle) {
		this.setRange(range);
		this.setAngle(angle);
	}
	/**
	 * 
	 * @param range in MM
	 */
	private void setRange(double range) {
		this.range = range;
	}
	/**
	 * range in MM
	 * @return
	 */
	public double getRange() {
		return range;
	}
	/**
	 * 
	 * @param angle current angle in degrees
	 */
	private void setAngle(double angle) {
		this.angle = angle;
	}
	/**
	 * 
	 * @return current angle in degrees
	 */
	public double getAngle() {
		return angle;
	}
	@Override
	public String toString() {
		String s="A"+new DecimalFormat("000.00 degrees ").format(angle)+":R"+range+"mm";
		return s;
	}
}
