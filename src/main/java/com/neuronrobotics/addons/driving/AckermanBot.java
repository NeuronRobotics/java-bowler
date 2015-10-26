package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class AckermanBot extends AbstractRobotDrive {
	
	
	/**
	 * steeringAngle in radians
	 */
	protected double steeringAngle=0;
	ServoRotoryLink steering;
	PIDChannel drive;
	PIDChannel lSteer;
	PIDChannel rSteer;
	PIDChannel bSteer;
	
	boolean complexSteering=false;
	
	private IAckermanBotKinematics ak = new AckermanDefaultKinematics();
	private DigitalOutputChannel driveEnable;
	private DigitalOutputChannel driveDirection;
	private double scale = 360.0/4096.0;
	private int currentEncoderReading;
	
	protected AckermanBot(){
		
	}
	
	public AckermanBot(ServoRotoryLink  s,PIDChannel d) {
		setPIDChanel(d);
		steering=s;
	}
	
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
	
	protected void setPIDChanel(PIDChannel d){
		drive=d;
		drive.addPIDEventListener(this);
	}
	
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
	
	public void setSteeringAngle(double s) {
		steeringAngle = s;
		setSteeringHardwareAngle(s);
	}
	
	public void setDriveData(AckermanBotDriveData d) {
		ResetDrivePosition();
		
		setSteeringAngle(d.getSteerAngle());
		SetDriveDistance(d.getTicksToTravil(), d.getSecondsToTravil());
	}
	public void setVelocityData(AckermanBotVelocityData d) {
		ResetDrivePosition();
		setSteeringAngle(d.getSteerAngle());
		SetDriveVelocity((int) d.getTicksPerSecond());
	}
	
	
	public double getSteeringAngle() {
		return steeringAngle;
	}
	protected void SetDriveDistance(int ticks, double seconds){
		Log.debug("Seting PID set point of= "+ticks+" currently at "+currentEncoderReading);
		//drive.SetPIDSetPoint(ticks, seconds);
		driveDirection.setHigh(ticks< currentEncoderReading);
		driveEnable.setHigh(false);
		ThreadUtil.wait((int) (seconds*1000));
		driveEnable.setHigh(true);
		Log.debug("Arrived at= "+currentEncoderReading);
	}
	protected void SetDriveVelocity(int ticksPerSecond){
		Log.debug("Seting PID Velocity set point of="+ticksPerSecond);
		if(ticksPerSecond>0){
			driveDirection.setHigh(ticksPerSecond> 0);
			driveEnable.setHigh(false);
		}else{
			driveEnable.setHigh(true);
		}

	}
	protected void ResetDrivePosition(){
		//Log.enableDebugPrint(true);
		
		drive.ResetPIDChannel(0);
		ThreadUtil.wait((200));
		//Log.enableDebugPrint(false);
	}
	
	@Override
	public void DriveStraight(double cm, double seconds) {
		setDriveData(ak.DriveStraight(cm, seconds));
	}
	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		setDriveData(ak.DriveArc(cmRadius, degrees, seconds));
	}
	@Override
	public void DriveVelocityStraight(double cmPerSecond) {
		setVelocityData(ak.DriveVelocityStraight(cmPerSecond));
	}
	@Override
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		setVelocityData(ak.DriveVelocityArc(degreesPerSecond, cmRadius));
	}

	
	public double getMaxTicksPerSecond() {
		return ak.getMaxTicksPerSeconds();
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		currentEncoderReading = e.getValue();
		setRobotLocationUpdate(ak.onPIDEvent(e,getSteeringAngle()));
	}

	@Override
	public void onPIDReset(int group, int currentValue) {
		System.out.println("Resetting PID");
		ak.onPIDReset(currentValue);
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return drive.isAvailable();
	}

	public void setIAckermanKinematics(IAckermanBotKinematics ak) {
		this.ak = ak;
	}

	public IAckermanBotKinematics getAckermanKinematics() {
		return ak;
	}

	@Override
	public void disconnectDeviceImp() {
		// TODO Auto-generated method stub
		drive.removePIDEventListener(this);
	}

	@Override
	public boolean connectDeviceImp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return null;
	}



}
