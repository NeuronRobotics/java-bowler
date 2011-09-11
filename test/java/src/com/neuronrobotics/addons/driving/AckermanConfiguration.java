package com.neuronrobotics.addons.driving;

public class AckermanConfiguration {

	private double maxTicksPerSeconds = 10000;
	private double ticksToCm = 128;
	private double wheelbase = 15;
	private double servoToSteerAngle=1;

	public void setMaxTicksPerSeconds(double maxTicksPerSeconds) {
		this.maxTicksPerSeconds = maxTicksPerSeconds;
	}

	public double getMaxTicksPerSeconds() {
		return maxTicksPerSeconds;
	}

	public double getCmtoTicks() {
		// TODO Auto-generated method stub
		return ticksToCm;
	}

	public double getWheelbase() {
		return wheelbase;
	}

	public double getSteerAngleToServo() {
		return 1/servoToSteerAngle;
	}

	public double getServoToSteerAngle() {
		return servoToSteerAngle;
	}
}
