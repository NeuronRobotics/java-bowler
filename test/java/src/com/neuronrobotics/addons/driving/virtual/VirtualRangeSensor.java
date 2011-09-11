package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractRobot;
import com.neuronrobotics.addons.driving.AbstractRangeSensor;

public class VirtualRangeSensor extends AbstractRangeSensor {

	public VirtualRangeSensor(AbstractRobot r,VirtualWorld w) {
		super(r);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean StartSweep(float startDeg, float endDeg, int degPerStep) {
		// TODO Auto-generated method stub
		return false;
	}

}
