package com.neuronrobotics.addons.driving.virtual;

import com.neuronrobotics.addons.driving.AbstractRobot;
import com.neuronrobotics.addons.driving.AbstractLineSensor;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class VirtualLineSensor extends AbstractLineSensor {
	SensorPoll poller=new SensorPoll();
	private VirtualWorld world;
	ObsticleType left= ObsticleType.NONE;
	ObsticleType middle= ObsticleType.NONE;
	ObsticleType right= ObsticleType.NONE;
	public VirtualLineSensor(AbstractRobot r,VirtualWorld w) {
		super(r);
		setWorld(w);
		poller.start();
	}

	private void setWorld(VirtualWorld world) {
		this.world = world;
	}

	private VirtualWorld getWorld() {
		return world;
	}

	private class SensorPoll extends Thread{
		public void run(){
			while(true){
				ThreadUtil.wait(1);
				ObsticleType tmpL = getWorld().getObsticle(getrobot(),-2,8);
				ObsticleType tmpC = getWorld().getObsticle(getrobot(),0,8);
				ObsticleType tmpR = getWorld().getObsticle(getrobot(),2,8);
				
				if((tmpL != left) ||(tmpC!=middle) ||(tmpR!=right)){
					left=tmpL;
					middle=tmpC;
					right=tmpR;
					getrobot().fireLineSensorEvent(left==ObsticleType.NONE?0:1024, middle==ObsticleType.NONE?0:1024, right==ObsticleType.NONE?0:1024, System.currentTimeMillis());
				}
			}
		}
	}
}
