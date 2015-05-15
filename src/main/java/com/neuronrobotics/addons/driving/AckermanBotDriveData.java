package com.neuronrobotics.addons.driving;

public class AckermanBotDriveData {
	private final double steerAngle;
	private final int ticksToTravil;
	private final double secondsToTravil;

	public AckermanBotDriveData(double steerAngle,int ticksToTravil,double secondsToTravil) {
		this.steerAngle = steerAngle;
		this.ticksToTravil = ticksToTravil;
		this.secondsToTravil = secondsToTravil;
		
	}

	public double getSteerAngle() {
		return steerAngle;
	}

	public int getTicksToTravil() {
		return ticksToTravil;
	}

	public double getSecondsToTravil() {
		return secondsToTravil;
	}
}
