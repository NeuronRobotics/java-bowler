package com.neuronrobotics.addons.driving;

public class AckermanConfiguration {
	
	private double ticksPerRevolution = 128;//ticks
	private double wheelDiameter = 2.54;//cm
	private double cmPerRevolution = 2*Math.PI*wheelDiameter;
	private double ticksToCm = ticksPerRevolution/cmPerRevolution;
	
	private double maxTicksPerSeconds = 200;
	private double wheelbase = 14.2;//cm
	private double servoToSteerAngle=1;

	public void setMaxTicksPerSeconds(double maxTicksPerSeconds) {
		this.maxTicksPerSeconds = maxTicksPerSeconds;
	}

	public double getMaxTicksPerSeconds() {
		return maxTicksPerSeconds;
	}

	public double convetrtToCm(int ticks){
		double back =ticks/ticksToCm;
		//System.out.println(ticks+" ticks = "+back+" cm");
		return back;
	}
	
	public int convertToTicks(double cm){
		int back = (int)(cm*ticksToCm);
		//System.out.println(cm+"cm = "+back+"ticks");
		return back;
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
