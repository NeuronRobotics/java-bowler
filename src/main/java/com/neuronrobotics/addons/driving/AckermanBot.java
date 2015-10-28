package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class AckermanBot.
 */
public class AckermanBot extends AbstractRobotDrive {
	
	
	/** steeringAngle in radians. */
	protected double steeringAngle=0;
	
	/** The steering. */
	ServoRotoryLink steering;
	
	/** The drive. */
	PIDChannel drive;
	
	/** The l steer. */
	PIDChannel lSteer;
	
	/** The r steer. */
	PIDChannel rSteer;
	
	/** The b steer. */
	PIDChannel bSteer;
	
	/** The complex steering. */
	boolean complexSteering=false;
	
	/** The ak. */
	private IAckermanBotKinematics ak = new AckermanDefaultKinematics();
	
	/** The drive enable. */
	private DigitalOutputChannel driveEnable;
	
	/** The drive direction. */
	private DigitalOutputChannel driveDirection;
	
	/** The scale. */
	private double scale = 360.0/4096.0;
	
	/** The current encoder reading. */
	private int currentEncoderReading;
	
	/**
	 * Instantiates a new ackerman bot.
	 */
	protected AckermanBot(){
		
	}
	
	/**
	 * Instantiates a new ackerman bot.
	 *
	 * @param s the s
	 * @param d the d
	 */
	public AckermanBot(ServoRotoryLink  s,PIDChannel d) {
		setPIDChanel(d);
		steering=s;
	}
	
	/**
	 * Instantiates a new ackerman bot.
	 *
	 * @param drive the drive
	 * @param lSteer the l steer
	 * @param rSteer the r steer
	 * @param bSteer the b steer
	 * @param driveEnable the drive enable
	 * @param driveDirection the drive direction
	 * @param akermanConfigs the akerman configs
	 */
	public AckermanBot(	PIDChannel drive,
						PIDChannel lSteer,
						PIDChannel rSteer,
						PIDChannel bSteer, 
						DigitalOutputChannel driveEnable, 
						DigitalOutputChannel driveDirection,
						IAckermanBotKinematics akermanConfigs) {
		ak=akermanConfigs;
		this.driveEnable = driveEnable;
		this.driveDirection = driveDirection;
		setPIDChanel(drive);

		this.lSteer=lSteer;
		this.rSteer=rSteer;
		this.bSteer=bSteer;
		complexSteering=true;
		SetDriveVelocity(0);
	}
	
	/**
	 * Sets the PID chanel.
	 *
	 * @param d the new PID chanel
	 */
	protected void setPIDChanel(PIDChannel d){
		drive=d;
		drive.addPIDEventListener(this);
	}
	
	/**
	 * Sets the steering hardware angle.
	 *
	 * @param s the new steering hardware angle
	 */
	public void setSteeringHardwareAngle(double s) {
		if(complexSteering==false){
			steering.setTargetAngle(s);
			steering.flush(0);
			
		}else{
			this.lSteer.SetPIDSetPoint((int) (s/scale), 0);
			this.rSteer.SetPIDSetPoint((int) (s/scale), 0);
			this.bSteer.SetPIDSetPoint(0, 0);
		}
	}
	
	/**
	 * Sets the steering angle.
	 *
	 * @param s the new steering angle
	 */
	public void setSteeringAngle(double s) {
		steeringAngle = s;
		setSteeringHardwareAngle(s);
	}
	
	/**
	 * Sets the drive data.
	 *
	 * @param d the new drive data
	 */
	public void setDriveData(AckermanBotDriveData d) {
		ResetDrivePosition();
		
		setSteeringAngle(d.getSteerAngle());
		SetDriveDistance(d.getTicksToTravil(), d.getSecondsToTravil());
	}
	
	/**
	 * Sets the velocity data.
	 *
	 * @param d the new velocity data
	 */
	public void setVelocityData(AckermanBotVelocityData d) {
		ResetDrivePosition();
		setSteeringAngle(d.getSteerAngle());
		SetDriveVelocity((int) d.getTicksPerSecond());
	}
	
	
	/**
	 * Gets the steering angle.
	 *
	 * @return the steering angle
	 */
	public double getSteeringAngle() {
		return steeringAngle;
	}
	
	/**
	 * Sets the drive distance.
	 *
	 * @param ticks the ticks
	 * @param seconds the seconds
	 */
	protected void SetDriveDistance(int ticks, double seconds){
		Log.debug("Seting PID set point of= "+ticks+" currently at "+currentEncoderReading);
		//drive.SetPIDSetPoint(ticks, seconds);
		driveDirection.setHigh(ticks< currentEncoderReading);
		driveEnable.setHigh(false);
		ThreadUtil.wait((int) (seconds*1000));
		driveEnable.setHigh(true);
		Log.debug("Arrived at= "+currentEncoderReading);
	}
	
	/**
	 * Sets the drive velocity.
	 *
	 * @param ticksPerSecond the ticks per second
	 */
	protected void SetDriveVelocity(int ticksPerSecond){
		Log.debug("Seting PID Velocity set point of="+ticksPerSecond);
		if(ticksPerSecond>0){
			driveDirection.setHigh(ticksPerSecond> 0);
			driveEnable.setHigh(false);
		}else{
			driveEnable.setHigh(true);
		}

	}
	
	/**
	 * Reset drive position.
	 */
	protected void ResetDrivePosition(){
		//Log.enableDebugPrint(true);
		
		drive.ResetPIDChannel(0);
		ThreadUtil.wait((200));
		//Log.enableDebugPrint(false);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveStraight(double, double)
	 */
	@Override
	public void DriveStraight(double cm, double seconds) {
		setDriveData(ak.DriveStraight(cm, seconds));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveArc(double, double, double)
	 */
	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		setDriveData(ak.DriveArc(cmRadius, degrees, seconds));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveVelocityStraight(double)
	 */
	@Override
	public void DriveVelocityStraight(double cmPerSecond) {
		setVelocityData(ak.DriveVelocityStraight(cmPerSecond));
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#DriveVelocityArc(double, double)
	 */
	@Override
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		setVelocityData(ak.DriveVelocityArc(degreesPerSecond, cmRadius));
	}

	
	/**
	 * Gets the max ticks per second.
	 *
	 * @return the max ticks per second
	 */
	public double getMaxTicksPerSecond() {
		return ak.getMaxTicksPerSeconds();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDEvent(com.neuronrobotics.sdk.pid.PIDEvent)
	 */
	@Override
	public void onPIDEvent(PIDEvent e) {
		currentEncoderReading = e.getValue();
		setRobotLocationUpdate(ak.onPIDEvent(e,getSteeringAngle()));
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.pid.IPIDEventListener#onPIDReset(int, int)
	 */
	@Override
	public void onPIDReset(int group, int currentValue) {
		System.out.println("Resetting PID");
		ak.onPIDReset(currentValue);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractRobotDrive#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return drive.isAvailable();
	}

	/**
	 * Sets the i ackerman kinematics.
	 *
	 * @param ak the new i ackerman kinematics
	 */
	public void setIAckermanKinematics(IAckermanBotKinematics ak) {
		this.ak = ak;
	}

	/**
	 * Gets the ackerman kinematics.
	 *
	 * @return the ackerman kinematics
	 */
	public IAckermanBotKinematics getAckermanKinematics() {
		return ak;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#disconnectDeviceImp()
	 */
	@Override
	public void disconnectDeviceImp() {
		// TODO Auto-generated method stub
		drive.removePIDEventListener(this);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#connectDeviceImp()
	 */
	@Override
	public boolean connectDeviceImp() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#getNamespacesImp()
	 */
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return null;
	}



}
