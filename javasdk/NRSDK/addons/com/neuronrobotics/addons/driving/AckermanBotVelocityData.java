package com.neuronrobotics.addons.driving;

public class AckermanBotVelocityData {
	private final double ticksPerSecond;
	private final double steerAngle;

	public  AckermanBotVelocityData (double steerAngle,double ticksPerSecond) {
		this.steerAngle = steerAngle;
		this.ticksPerSecond = ticksPerSecond;
		
	}

	public double getSteerAngle() {
		return steerAngle;
	}

	public double getTicksPerSecond() {
		return ticksPerSecond;
	}
}
