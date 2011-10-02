package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class AckermanBot extends AbstractRobotDrive {
	protected final AckermanConfiguration config = new AckermanConfiguration();
	private int currentDriveTicks=0;
	/**
	 * steeringAngle in radians
	 */
	protected double steeringAngle=0;
	ServoChannel steering;
	PIDChannel drive;
	protected AckermanBot(){
		
	}
	
	public AckermanBot(ServoChannel s,PIDChannel d) {
		setPIDChanel(d);
		steering=s;
	}
	
	protected void setPIDChanel(PIDChannel d){
		drive=d;
		drive.addPIDEventListener(this);
	}
	
	public void setSteeringHardwareAngle(double s) {
		int srvVal = (int) (steeringAngle*config.getSteerAngleToServo())+config.getServoCenterPos();
		if(srvVal> config.getSteeringServoMaxVal())
			srvVal= config.getSteeringServoMaxVal();
		if(srvVal<config.getSteeringServoMinVal())
			srvVal= config.getSteeringServoMinVal();
		steering.SetPosition(srvVal);
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
		ResetDrivePosition();
		setSteeringAngle(0);
		//SetDriveDistance((int) (cm*config.getCmtoTicks()),seconds);
		SetDriveDistance(config.convertToTicks(cm),seconds);
	}
	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		ResetDrivePosition();
		double archlen = cmRadius*((2*Math.PI*degrees)/(360));
		double steerAngle =((config.getWheelbase()/cmRadius));
		setSteeringAngle(steerAngle);
		SetDriveDistance(config.convertToTicks(archlen),seconds);
	}
	@Override
	public void DriveVelocityStraight(double cmPerSecond) {
		setSteeringAngle(0);
		SetDriveVelocity(config.convertToTicks(cmPerSecond));
	}
	@Override
	public void DriveVelocityArc(double degreesPerSecond, double cmRadius) {
		// TODO Auto-generated method stub
		double steerAngle =((config.getWheelbase()/cmRadius));
		setSteeringAngle(steerAngle);
		double archlen = cmRadius*((2*Math.PI*degreesPerSecond)/(360));
		int ticks = config.convertToTicks(archlen);
		System.out.println("Seting PID set point of="+ticks);
		SetDriveVelocity(ticks);
	}

	
	public double getMaxTicksPreSecond() {
		return config.getMaxTicksPerSeconds();
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		System.out.println("\n\nCurrent Ticks="+currentDriveTicks+" Event="+e);
		int differenceTicks = (e.getValue()-currentDriveTicks);
		double archLen = config.convetrtToCm(differenceTicks);
		
		double radiusOfCurve=0;
		double centralAngleRadians=0;
		double deltLateral=0;
		double deltForward=0;
		if(getSteeringAngle() !=0){
			radiusOfCurve = config.getWheelbase()/getSteeringAngle();
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
		
		//System.out.println("Relative motion delta Ticks="+differenceTicks+", forward="+deltForward+", lateral="+deltLateral);
		double [] loc = getPositionOffset(deltLateral, deltForward);
		
		setCurrentX(loc[0]);
		setCurrentY(loc[1]);
		setCurrentOrentation( getCurrentOrentation()+centralAngleRadians);
		
		currentDriveTicks=e.getValue();
		fireDriveEvent();
	}

	@Override
	public void onPIDReset(int group, int currentValue) {
		System.out.println("Resetting PID");
		currentDriveTicks=currentValue;
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return drive.isAvailable();
	}



}
