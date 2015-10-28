package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualFlameSensor.
 */
public class VirtualFlameSensor extends VirtualRangeSensor {

	/**
	 * Instantiates a new virtual flame sensor.
	 *
	 * @param r the r
	 * @param w the w
	 */
	public VirtualFlameSensor(AbstractRobotDrive r, VirtualWorld w) {
		super(r, w);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.virtual.VirtualRangeSensor#getObsticleType()
	 */
	@Override
	protected ObsticleType getObsticleType(){
		return ObsticleType.FIRE;
	}
}
