package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;

public class VirtualPuckBot extends AbstractDrivingRobot{
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
