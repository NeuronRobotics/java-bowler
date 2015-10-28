package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;
import com.neuronrobotics.addons.driving.LinearRangeSensor;


// TODO: Auto-generated Javadoc
/**
 * The Class VirtualRangeSensor.
 */
public class VirtualRangeSensor extends  LinearRangeSensor {
	
	/** The world. */
	private VirtualWorld world;
	
	/** The platform. */
	private AbstractRobotDrive platform = null;
	
	/**
	 * Instantiates a new virtual range sensor.
	 *
	 * @param r the r
	 * @param w the w
	 */
	public VirtualRangeSensor(AbstractRobotDrive r,VirtualWorld w) {
		this.platform = r;
		world=w;
	}


	/**
	 * Gets the obsticle type.
	 *
	 * @return the obsticle type
	 */
	protected ObsticleType getObsticleType(){
		return ObsticleType.WALL;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.LinearRangeSensor#getDistance(double)
	 */
	@Override
	protected double getDistance(double current){
		world.updateMap();
		return world.getRangeData(platform,Math.toRadians(current), 5000000,getObsticleType());
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.LinearRangeSensor#setCurrentAngle(double)
	 */
	@Override
	public void setCurrentAngle(double current) {
		this.current = current;
	}
}
