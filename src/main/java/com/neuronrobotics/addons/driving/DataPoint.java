package com.neuronrobotics.addons.driving;

import java.text.DecimalFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class DataPoint.
 */
public class DataPoint {
	
	/** The range. */
	private double range;
	
	/** The angle. */
	private double angle;
	
	/**
	 * Instantiates a new data point.
	 *
	 * @param range a distance in MM
	 * @param angle angle in degrees
	 */
	public DataPoint(double range, double angle) {
		this.setRange(range);
		this.setAngle(angle);
	}
	
	/**
	 * Sets the range.
	 *
	 * @param range in MM
	 */
	private void setRange(double range) {
		this.range = range;
	}
	
	/**
	 * range in MM.
	 *
	 * @return the range
	 */
	public double getRange() {
		return range;
	}
	
	/**
	 * Sets the angle.
	 *
	 * @param angle current angle in degrees
	 */
	private void setAngle(double angle) {
		this.angle = angle;
	}
	
	/**
	 * Gets the angle.
	 *
	 * @return current angle in degrees
	 */
	public double getAngle() {
		return angle;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s="A"+new DecimalFormat("000.00 degrees ").format(angle)+":R"+range+"mm";
		return s;
	}
}
