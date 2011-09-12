package com.neuronrobotics.addons.driving;

public abstract class AbstractRangeSensor {
	private AbstractRobot robot = null;
	
	public AbstractRangeSensor(AbstractRobot r){
		setRobot(r);
	}
	
	public abstract boolean StartSweep(float startDeg,float endDeg,int degPerStep);

	public void setRobot(AbstractRobot platform) {
		this.robot = platform;
	}

	public AbstractRobot getRobot() {
		return robot;
	}
}
