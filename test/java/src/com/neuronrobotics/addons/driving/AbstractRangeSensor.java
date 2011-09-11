package com.neuronrobotics.addons.driving;

public abstract class AbstractRangeSensor {
	private AbstractRobot platform = null;
	
	public AbstractRangeSensor(AbstractRobot r){
		setPlatform(r);
	}
	
	public abstract boolean StartSweep(float startDeg,float endDeg,int degPerStep);

	public void setPlatform(AbstractRobot platform) {
		this.platform = platform;
	}

	public AbstractRobot getPlatform() {
		return platform;
	}
}
