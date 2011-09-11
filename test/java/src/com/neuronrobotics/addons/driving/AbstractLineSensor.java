package com.neuronrobotics.addons.driving;

public abstract class  AbstractLineSensor {
	private AbstractRobot platform = null;	
	public AbstractLineSensor(AbstractRobot r){
		setPlatform(r);
	}
	public void setPlatform(AbstractRobot platform) {
		this.platform = platform;
	}
	public AbstractRobot getrobot() {
		return platform;
	}
}
