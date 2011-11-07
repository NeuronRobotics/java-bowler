package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDEvent;

public class PuckBotDefaultKinematics implements IPuckBotKinematics{
	/**
	 * This is a full implementation of the PuckBot kinematics
	 */
	@Override
	public PuckBotDriveData DriveStraight(double cm, double seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PuckBotDriveData DriveArc(double cmRadius, double degrees,
			double seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PuckBotVelocityData DriveVelocityStraight(double cmPerSecond) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PuckBotVelocityData DriveVelocityArc(double degreesPerSecond,double cmRadius) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RobotLocationData onPIDEvent(PIDEvent e, int leftChannelNumber,int rightChannelNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMaxTicksPerSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onPIDResetLeft(int currentValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPIDResetRight(int currentValue) {
		// TODO Auto-generated method stub
		
	}
	
	

}
