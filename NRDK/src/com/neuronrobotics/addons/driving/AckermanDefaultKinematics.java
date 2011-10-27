package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDEvent;

public class AckermanDefaultKinematics implements IAckermanBotKinematics{
	private int currentDriveTicks=0;
	
	protected final AckermanConfiguration config = new AckermanConfiguration();
	
	public AckermanDefaultKinematics(){

	}
	
	public AckermanBotDriveData DriveStraight(double cm, double seconds) {
		return new AckermanBotDriveData(0, config.convertToTicks(cm), seconds);
	}
	
	public AckermanBotDriveData DriveArc(double cmRadius, double degrees, double seconds) {
		double archlen = cmRadius*((2*Math.PI*degrees)/(360));
		double steerAngle =((config.getWheelbase()/cmRadius));
		return new AckermanBotDriveData(steerAngle, config.convertToTicks(archlen), seconds);
	}
	
	public AckermanBotVelocityData DriveVelocityStraight(double cmPerSecond) {
		return new AckermanBotVelocityData(0, config.convertToTicks(cmPerSecond));
	}
	
	public AckermanBotVelocityData DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		// TODO Auto-generated method stub
		double steerAngle =((config.getWheelbase()/cmRadius));
		
		double archlen = cmRadius*((2*Math.PI*degreesPerSecond)/(360));
		int ticks = config.convertToTicks(archlen);
		System.out.println("Seting PID set point of="+ticks);
		return new AckermanBotVelocityData(steerAngle, ticks);
	}
	
	public RobotLocationData onPIDEvent(PIDEvent e, double steerAngle) {
		System.out.println("\n\nCurrent Ticks="+currentDriveTicks+" Event="+e);
		int differenceTicks = (e.getValue()-currentDriveTicks);
		currentDriveTicks=e.getValue();
		double archLen = config.convetrtToCm(differenceTicks);
		
		double radiusOfCurve=0;
		double centralAngleRadians=0;
		double deltLateral=0;
		double deltForward=0;
		if(steerAngle !=0){
			radiusOfCurve = config.getWheelbase()/steerAngle;
			centralAngleRadians = archLen/radiusOfCurve;
			//System.out.println("Central angle of motion was: "+Math.toDegrees(centralAngleRadians) + " Radius of curve = "+radiusOfCurve);
			double y = radiusOfCurve*Math.sin(centralAngleRadians);
			double x = radiusOfCurve*Math.cos(centralAngleRadians);
			deltLateral =  -1*(radiusOfCurve-x);
			deltForward =  y;
		}else{
			//System.out.println("Steering angle of 0, moving forward");
			deltLateral =  0;
			deltForward =  archLen;
		}
		
		return new RobotLocationData(deltLateral,deltForward,centralAngleRadians);
	}
	
	public void onPIDReset( int currentValue){
		currentDriveTicks=currentValue;
	}

	public double getMaxTicksPerSeconds() {
		return config.getMaxTicksPerSeconds();
	}
}
