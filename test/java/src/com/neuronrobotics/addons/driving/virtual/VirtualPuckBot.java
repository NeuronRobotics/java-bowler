package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;
import com.neuronrobotics.addons.driving.PuckBot;
import com.neuronrobotics.sdk.pid.PIDEvent;

public class VirtualPuckBot extends PuckBot{
	private VirtualWorld world;
	public VirtualPuckBot(VirtualWorld w){
		world=w;
		world.addRobot(this);
	}
	@Override
	public void DriveStraight(double cm, double seconds) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void DriveArc(double cmRadius, double degrees, double seconds) {
		// TODO Auto-generated method stub
		
	}

}
