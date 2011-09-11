package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;
import com.neuronrobotics.addons.driving.AbstractRangeSensor;

public class VirtualRangeSensor extends AbstractRangeSensor {

	public VirtualRangeSensor(AbstractDrivingRobot r) {
		super(r);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean StartSweep(float startDeg, float endDeg, int degPerStep) {
		// TODO Auto-generated method stub
		return false;
	}

}
