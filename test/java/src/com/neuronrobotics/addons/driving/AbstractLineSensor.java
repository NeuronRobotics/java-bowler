package com.neuronrobotics.addons.driving;

public abstract class  AbstractLineSensor {
	private AbstractDrivingRobot platform = null;	
	public AbstractLineSensor(AbstractDrivingRobot r){
		setPlatform(r);
	}
	public void setPlatform(AbstractDrivingRobot platform) {
		this.platform = platform;
	}
	public AbstractDrivingRobot getPlatform() {
		return platform;
	}
}
