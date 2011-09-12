package com.neuronrobotics.addons.driving;

public abstract class  AbstractLineSensor extends AbstractSensor{
	private AbstractRobotDrive platform = null;	
	public AbstractLineSensor(AbstractRobotDrive r){
		setPlatform(r);
	}
	public void setPlatform(AbstractRobotDrive platform) {
		this.platform = platform;
	}
	public AbstractRobotDrive getrobot() {
		return platform;
	}
}
