package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.addons.kinematics.ServoRotoryLink;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class AckermanBot extends AbstractRobotDrive {
	
	
	/**
	 * steeringAngle in radians
	 */
	protected double steeringAngle=0;
	ServoRotoryLink steering;
	PIDChannel drive;
	private AckermanDefaultKinematics ak = new AckermanDefaultKinematics(this);
	
	protected AckermanBot(){
		
	}
	
	public AckermanBot(ServoRotoryLink  s,PIDChannel d) {
		setPIDChanel(d);
		steering=s;
	}
	
	protected void setPIDChanel(PIDChannel d){
		drive=d;
		drive.addPIDEventListener(this);
	}
	
	public void setSteeringHardwareAngle(double s) {
		steering.setTargetAngle(s);
		steering.flush(0);
	}
	
	public void setSteeringAngle(double s) {
		steeringAngle = s;
		setSteeringHardwareAngle(s);
	}
	public double getSteeringAngle() {
		return steeringAngle;
	}
	protected void SetDriveDistance(int ticks, double seconds){
		//System.out.println("Seting PID set point of="+ticks);
		drive.SetPIDSetPoint(ticks, seconds);
	}
	protected void SetDriveVelocity(int ticksPerSecond){
		//System.out.println("Seting PID set point of="+ticks);
		try {
			drive.SetPDVelocity(ticksPerSecond, 0);
		} catch (PIDCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ak.DriveStraight(cm, seconds);
	}
	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		ak.DriveArc(cmRadius, degrees, seconds);
	}
	@Override
	public void DriveVelocityStraight(double cmPerSecond) {
		ak.DriveVelocityStraight(cmPerSecond);
	}
	@Override
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		ak.DriveVelocityArc(degreesPerSecond, cmRadius);
	}

	
	public double getMaxTicksPerSecond() {
		return ak.getMaxTicksPerSeconds();
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		RobotLocationData d = ak.onPIDEvent(e,getSteeringAngle());
		double [] loc = getPositionOffset(d.getDeltaX(), d.getDeltaY());
		setCurrentX(loc[0]);
		setCurrentY(loc[1]);
		setCurrentOrentation( getCurrentOrentation()+d.getDeltaOrentation());
		fireDriveEvent();
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

	public void setAckermanKinematics(AckermanDefaultKinematics ak) {
		this.ak = ak;
	}

	public AckermanDefaultKinematics getAckermanKinematics() {
		return ak;
	}



}
