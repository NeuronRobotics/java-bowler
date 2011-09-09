package com.neuronrobotics.addons.driving.virtual;

import java.util.ArrayList;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;

public class VirtualWorld {
	private ArrayList<AbstractDrivingRobot> bots = new ArrayList<AbstractDrivingRobot>();
	
	public VirtualWorld() {
		
	}
	
	public void addRobot(AbstractDrivingRobot robot) {
		if(!bots.contains(robot))
			bots.add(robot);
	}

}
