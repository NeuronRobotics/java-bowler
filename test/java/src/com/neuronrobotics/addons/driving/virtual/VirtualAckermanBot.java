package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AckermanBot;
import com.neuronrobotics.addons.driving.AckermanConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;

public class VirtualAckermanBot extends AckermanBot {
	private VirtualWorld world;
	private final AckermanConfiguration config = new AckermanConfiguration();
	VirtualRobot drive;

	public VirtualAckermanBot(VirtualWorld w){
		world=w;
		world.addRobot(this);
		drive = new VirtualRobot(0,this,config.getMaxTicksPerSeconds());
		drive.start();
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
	@Override
	public void onPIDEvent(PIDEvent e)  {
		super.onPIDEvent(e);
		world.updateUI();
	}
	@Override
	public void onPIDReset(int group, int currentValue){
		if(group==0){
			drive.ZeroEncoder();
			super.onPIDReset(group, currentValue);
		}
	}

	public void setSteeringAngle(double steeringAngle) {
		this.steeringAngle = steeringAngle;
	}
}
