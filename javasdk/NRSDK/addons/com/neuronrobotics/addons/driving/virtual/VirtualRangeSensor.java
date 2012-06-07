package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;
import com.neuronrobotics.addons.driving.LinearRangeSensor;


public class VirtualRangeSensor extends  LinearRangeSensor {
	
	private VirtualWorld world;
	private AbstractRobotDrive platform = null;
	public VirtualRangeSensor(AbstractRobotDrive r,VirtualWorld w) {
		this.platform = r;
		world=w;
	}


	protected ObsticleType getObsticleType(){
		return ObsticleType.WALL;
	}
	
	@Override
	protected double getDistance(double current){
		world.updateMap();
		return world.getRangeData(platform,Math.toRadians(current), 5000000,getObsticleType());
	}
	@Override
	public void setCurrentAngle(double current) {
		this.current = current;
	}
}
