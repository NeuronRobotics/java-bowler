package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDEvent;

public class PuckBotKinematics {
	PuckBot robot;
	PuckBotConfiguration config = new PuckBotConfiguration();
	
	public PuckBotKinematics(PuckBot puckBot) {
		// TODO Auto-generated constructor stub
	}
	
	public void DriveStraight(double cm, double seconds) {
	
	}
	
	public void DriveArc(double cmRadius, double degrees, double seconds) {

	}
	
	public void DriveVelocityStraight(double cmPerSecond) {

	}
	
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {

	}
	
	public void onPIDEvent(PIDEvent e) {

	}
	
	public void onPIDResetLeft( int currentValue){
		
	}
	public void onPIDResetRight( int currentValue){
		
	}


	public double getMaxTicksPerSeconds() {
		return config.getMaxTicksPerSeconds();
	}

}
