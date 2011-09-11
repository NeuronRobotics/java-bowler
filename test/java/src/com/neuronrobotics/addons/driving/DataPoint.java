package com.neuronrobotics.addons.driving;

import java.text.DecimalFormat;

public class DataPoint {
	private int range;
	private double angle;
	public DataPoint(int range, double angle) {
		this.setRange(range);
		this.setAngle(angle);
	}
	private void setRange(int range) {
		this.range = range;
	}
	public int getRange() {
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
