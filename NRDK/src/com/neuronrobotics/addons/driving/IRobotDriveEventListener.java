package com.neuronrobotics.addons.driving;

public interface IRobotDriveEventListener {
	public void onDriveEvent(AbstractRobotDrive source,double x, double y, double orentation);
}
