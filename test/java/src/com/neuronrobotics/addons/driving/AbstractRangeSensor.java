package com.neuronrobotics.addons.driving;

public abstract class AbstractRangeSensor extends AbstractSensor{
	private AbstractRobot robot = null;
	
	public AbstractRangeSensor(AbstractRobot r){
		setRobot(r);
	}

	public void setRobot(AbstractRobot platform) {
		this.robot = platform;
	}

	public AbstractRobot getRobot() {
		return robot;
	}
}
