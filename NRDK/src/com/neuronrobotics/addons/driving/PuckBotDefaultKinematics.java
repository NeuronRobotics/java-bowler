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
		return new PuckBotDriveData(dist, dist, seconds);
	}

	@Override
	public PuckBotDriveData DriveArc(double cmRadius, double degrees,double seconds) {
		
		double ldist = 0;
		double rdist = 0;
		
		double rRadius = cmRadius + (wheelBase/2);
		double lRadius = cmRadius - (wheelBase/2);
		ldist = lRadius*(Math.PI*degrees)/180;
		rdist = rRadius*(Math.PI*degrees)/180;
		
		return new PuckBotDriveData(cmToTicks(ldist), cmToTicks(rdist), seconds);
	}

	@Override
	public PuckBotVelocityData DriveVelocityStraight(double cmPerSecond) {
		int tps = cmToTicks(cmPerSecond);
		return new PuckBotVelocityData(tps, tps);
	}

	@Override
	public PuckBotVelocityData DriveVelocityArc(double degreesPerSecond,double cmRadius) {	
		double rRadius = cmRadius + (wheelBase/2);
		double lRadius = cmRadius - (wheelBase/2);
		double ldist = lRadius*(Math.PI*degreesPerSecond)/180;
		double rdist = rRadius*(Math.PI*degreesPerSecond)/180;
		return new PuckBotVelocityData(cmToTicks(ldist), cmToTicks(rdist));
	}

	@Override
	public RobotLocationData onPIDEvent(PIDEvent e, int leftChannelNumber,int rightChannelNumber) {
		System.out.println("Got: "+e);
		// TODO Auto-generated method stub
		return new RobotLocationData(0, 0, 0);
	}

	@Override
	public double getMaxTicksPerSeconds() {
		return 200;
	}

	@Override
	public void onPIDResetLeft(int currentValue) {

	}

	@Override
	public void onPIDResetRight(int currentValue) {

	}
	
	

}
