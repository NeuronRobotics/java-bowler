package com.neuronrobotics.addons.driving;

public abstract class AbstractRangeSensor {
	private AbstractDrivingRobot platform = null;
	
	public AbstractRangeSensor(AbstractDrivingRobot r){
		setPlatform(r);
	}
	
	public abstract boolean StartSweep(float startDeg,float endDeg,int degPerStep);

	public void setPlatform(AbstractDrivingRobot platform) {
		this.platform = platform;
	}

	public AbstractDrivingRobot getPlatform() {
		return platform;
	}
}
