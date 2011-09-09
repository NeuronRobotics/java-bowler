package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;

public class DrivingRobotUI {
	private double startx;
	private double starty;
	private double orentation = 0;
	private AbstractDrivingRobot robot;
	public DrivingRobotUI(AbstractDrivingRobot robot, double botstartx, double botstarty) {
		this.robot=robot;
		startx=botstartx;
		starty=botstarty;
	}

}
