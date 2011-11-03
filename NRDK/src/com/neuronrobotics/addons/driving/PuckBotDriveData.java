package com.neuronrobotics.addons.driving;

public class PuckBotDriveData {
	private int l;
	private int r;
	private final double seconds;
	public PuckBotDriveData(int leftEncoder, int rightEncoder, double seconds){
		this.seconds = seconds;
		setL(leftEncoder);
		setR(rightEncoder);
	}
	private void setL(int l) {
		this.l = l;
	}
	public int getLeftEncoderData() {
		return l;
	}
	private void setR(int r) {
		this.r = r;
	}
	public int getRightEncoderData() {
		return r;
	}
	public double getDriveTimeInSeconds() {
		return seconds;
	}
}
