package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.pid.PIDEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class AckermanDefaultKinematics.
 */
public class AckermanDefaultKinematics implements IAckermanBotKinematics{
	
	/** The current drive ticks. */
	private int currentDriveTicks=0;
	
	/** The config. */
	protected AckermanConfiguration config = new AckermanConfiguration();
	
	/**
	 * Instantiates a new ackerman default kinematics.
	 */
	public AckermanDefaultKinematics(){

	}
	
	/**
	 * Instantiates a new ackerman default kinematics.
	 *
	 * @param config the config
	 */
	public AckermanDefaultKinematics(AckermanConfiguration config ){
		this.config =  config; 
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IAckermanBotKinematics#DriveStraight(double, double)
	 */
	public AckermanBotDriveData DriveStraight(double cm, double seconds) {
		return new AckermanBotDriveData(0, config.convertToTicks(cm), seconds);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IAckermanBotKinematics#DriveArc(double, double, double)
	 */
	public AckermanBotDriveData DriveArc(double cmRadius, double degrees, double seconds) {
		double archlen = cmRadius*((2*Math.PI*degrees)/(360));
		double steerAngle =((config.getWheelbase()/cmRadius));
		return new AckermanBotDriveData(steerAngle, config.convertToTicks(archlen), seconds);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IAckermanBotKinematics#DriveVelocityStraight(double)
	 */
	public AckermanBotVelocityData DriveVelocityStraight(double cmPerSecond) {
		return new AckermanBotVelocityData(0, config.convertToTicks(cmPerSecond));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IAckermanBotKinematics#DriveVelocityArc(double, double)
	 */
	public AckermanBotVelocityData DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		// TODO Auto-generated method stub
		double steerAngle =((config.getWheelbase()/cmRadius));
		
		double archlen = cmRadius*((2*Math.PI*degreesPerSecond)/(360));
		int ticks = config.convertToTicks(archlen);
		System.out.println("Seting PID set point of="+ticks);
		return new AckermanBotVelocityData(steerAngle, ticks);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IAckermanBotKinematics#onPIDEvent(com.neuronrobotics.sdk.pid.PIDEvent, double)
	 */
	public RobotLocationData onPIDEvent(PIDEvent e, double steerAngle) {
		//System.out.println("\n\nCurrent Ticks="+currentDriveTicks+" Event="+e);
		int differenceTicks = (e.getValue()-currentDriveTicks);
		
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
		RobotLocationData rl = new RobotLocationData(deltLateral,deltForward,centralAngleRadians);
		currentDriveTicks=e.getValue();
		return rl;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IAckermanBotKinematics#onPIDReset(int)
	 */
	public void onPIDReset( int currentValue){
		currentDriveTicks=currentValue;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.IAckermanBotKinematics#getMaxTicksPerSeconds()
	 */
	public double getMaxTicksPerSeconds() {
		return config.getMaxTicksPerSeconds();
	}
}
