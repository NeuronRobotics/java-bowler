package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDEvent;

public class PuckBotDefaultKinematics implements IPuckBotKinematics{
	
	private static final double wheelBase = 22.86; //cm
	private static final double wheelDiameter = 7;//cm
	private static final double ticksPerRevolution = 360;// t/r 
	
	private static final double cmToTickScale = ticksPerRevolution*(-1/(Math.PI*wheelDiameter));
	
	public static double ticksToCm(int ticks) {
		return ((double)ticks)/ cmToTickScale;
	}
	public static int cmToTicks(double cm) {
		return (int) (cm*cmToTickScale);
	}
	
	/**
	 * This is a full implementation of the PuckBot kinematics
	 */
	@Override
	public PuckBotDriveData DriveStraight(double cm, double seconds) {
		int dist = cmToTicks(cm);
		return new PuckBotDriveData(-1*dist, dist, seconds);
	}

	@Override
	public PuckBotDriveData DriveArc(double cmRadius, double degrees,double seconds) {
		
		double ldist = 0;
		double rdist = 0;
		
		double rRadius = cmRadius + (wheelBase/2);
		double lRadius = cmRadius - (wheelBase/2);
		ldist = lRadius*(Math.PI*degrees)/180;
		rdist = rRadius*(Math.PI*degrees)/180;
		
		return new PuckBotDriveData(cmToTicks(-1*ldist), cmToTicks(rdist), seconds);
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
		return new RobotLocationData(0, 0, 0);
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
