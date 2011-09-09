package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;
import com.neuronrobotics.addons.driving.AckermanConfiguration;

public class VirtualAckermanBot extends AbstractDrivingRobot {
	private VirtualWorld world;
	private final AckermanConfiguration config = new AckermanConfiguration();
	
	public VirtualAckermanBot(VirtualWorld w){
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
