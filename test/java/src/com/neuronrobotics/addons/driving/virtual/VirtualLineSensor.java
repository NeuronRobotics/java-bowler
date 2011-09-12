package com.neuronrobotics.addons.driving.virtual;

import java.awt.Color;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;
import com.neuronrobotics.addons.driving.AbstractLineSensor;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class VirtualLineSensor extends AbstractLineSensor {
	SensorPoll poller=new SensorPoll();
	private VirtualWorld world;
	ObsticleType left= ObsticleType.NONE;
	ObsticleType middle= ObsticleType.NONE;
	ObsticleType right= ObsticleType.NONE;
	double fOffset = 6;
	double lOffset = 3;
	public VirtualLineSensor(AbstractRobotDrive r,VirtualWorld w) {
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
			getWorld().addSensorDisplayDot(getrobot(), 	 lOffset, fOffset, Color.red);
			getWorld().addSensorDisplayDot(getrobot(),  		0, fOffset, Color.white);
			getWorld().addSensorDisplayDot(getrobot(),    -lOffset, fOffset, Color.black);
			while(true){
				ThreadUtil.wait(1);
				ObsticleType tmpL = getWorld().getObsticle(getrobot(),	 lOffset,fOffset);
				ObsticleType tmpC = getWorld().getObsticle(getrobot(),			0,fOffset);
				ObsticleType tmpR = getWorld().getObsticle(getrobot(),	  -lOffset,fOffset);
				
				if((tmpL != left) ||(tmpC!=middle) ||(tmpR!=right)){
					left=tmpL;
					middle=tmpC;
					right=tmpR;
					fireLineSensorEvent(left==ObsticleType.NONE?0:1024, middle==ObsticleType.NONE?0:1024, right==ObsticleType.NONE?0:1024, System.currentTimeMillis());
				}
			}
		}
	}

	@Override
	public void StartSweep(double start, double stop, int increment) {
		// do nothing
	}
}
