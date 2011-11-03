package com.neuronrobotics.addons.driving;

public class PuckBotVelocityData {
	
	private final double leftTicksPerSecond;
	private final double rightTicksPerSecond;

	public PuckBotVelocityData(double leftTicksPerSecond, double rightTicksPerSecond){
		this.leftTicksPerSecond = leftTicksPerSecond;
		this.rightTicksPerSecond = rightTicksPerSecond;
		
	}

	public double getLeftTicksPerSecond() {
		return leftTicksPerSecond;
	}

	public double getRightTicksPerSecond() {
		return rightTicksPerSecond;
	}
}
