package com.neuronrobotics.addons.driving;

import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class AckermanBot extends AbstractDrivingRobot {
	private final AckermanConfiguration config = new AckermanConfiguration();
	private int currentDriveTicks=0;
	protected double steeringAngle=0;
	ServoChannel steering;
	PIDChannel drive;
	public AckermanBot(){
		
	}
	
	public AckermanBot(ServoChannel s,PIDChannel d) {
		drive=d;
		steering=s;
		drive.addPIDEventListener(this);
		
	}
	
	public void setSteeringAngle(double s) {
		steeringAngle = s;
		steering.SetPosition((int) (steeringAngle*config.getSteerAngleToServo()));
	}
	public double getSteeringAngle() {
		return steeringAngle;
	}

	@Override
	public void DriveStraight(double cm, double seconds) {
		setSteeringAngle(0);
		drive.SetPIDSetPoint((int)(cm*config.getCmtoTicks()), seconds);
	}
	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		double archlen = cmRadius*((2*Math.PI*degrees)/(360));
		setSteeringAngle(degrees);
		drive.SetPIDSetPoint((int)(archlen*config.getCmtoTicks()), seconds);
	}

	public double getMaxTicksPreSecond() {
		return config.getMaxTicksPerSeconds();
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		System.out.println("Ackerman drive event: "+e);
		int differenceTicks = e.getValue()-currentDriveTicks;
		
		
		
		currentDriveTicks=e.getValue();
	}

	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPIDReset(int group, int currentValue) {
		if(group==0){
			currentDriveTicks=0;
		}
	}
}
