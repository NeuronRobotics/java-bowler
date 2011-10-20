package com.neuronrobotics.addons.driving;

import java.text.DecimalFormat;

public class DataPoint {
	private double range;
	private double angle;
	public DataPoint(double range, double angle) {
		this.setRange(range);
		this.setAngle(angle);
	}
	private void setRange(double range) {
		this.range = range;
	}
	public double getRange() {
		return range;
	}
	private void setAngle(double angle) {
		this.angle = angle;
	}
	public double getAngle() {
		return angle;
	}
	@Override
	public String toString() {
		String s="A"+new DecimalFormat("000.00").format(angle)+":R"+range;
		return s;
	}
}
