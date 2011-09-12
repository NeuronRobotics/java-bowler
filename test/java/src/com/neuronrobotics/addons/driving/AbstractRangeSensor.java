package com.neuronrobotics.addons.driving;

public abstract class AbstractRangeSensor extends AbstractSensor{
	private AbstractRobotDrive robot = null;
	
	public AbstractRangeSensor(AbstractRobotDrive r){
		setRobot(r);
	}

	public void setRobot(AbstractRobotDrive platform) {
		this.robot = platform;
	}

	public AbstractRobotDrive getRobot() {
		return robot;
	}
}
