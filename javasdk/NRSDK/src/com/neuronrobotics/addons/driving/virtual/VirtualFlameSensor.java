package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;

public class VirtualFlameSensor extends VirtualRangeSensor {

	public VirtualFlameSensor(AbstractRobotDrive r, VirtualWorld w) {
		super(r, w);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected ObsticleType getObsticleType(){
		return ObsticleType.FIRE;
	}
}
