package com.neuronrobotics.addons.driving.virtual;

import java.awt.Color;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;
import com.neuronrobotics.addons.driving.AbstractSensor;

import com.neuronrobotics.sdk.util.ThreadUtil;

public class VirtualLineSensor extends AbstractSensor {
	SensorPoll poller=new SensorPoll();
	private VirtualWorld world;
	ObsticleType left= ObsticleType.NONE;
	ObsticleType middle= ObsticleType.NONE;
	ObsticleType right= ObsticleType.NONE;
	double fOffset = 6;
	double lOffset = 2;
	private AbstractRobotDrive platform = null;
	public VirtualLineSensor(AbstractRobotDrive r,VirtualWorld w) {
		this.platform = r;
		setWorld(w);
		poller.start();
	}
	
	public VirtualLineSensor(AbstractRobotDrive r,VirtualWorld w, double forwardOffset, double lateralOffset) {
		this.platform = r;
		fOffset=forwardOffset;
		lOffset=lateralOffset;
		setWorld(w);
		poller.start();
	}
	public AbstractRobotDrive getRobot() {
		return platform;
	}
	private void setWorld(VirtualWorld world) {
		this.world = world;
	}

	private VirtualWorld getWorld() {
		return world;
	}

	private class SensorPoll extends Thread{
		public void run(){
			getWorld().addSensorDisplayDot(getRobot(), 	 lOffset, fOffset, Color.red);
			getWorld().addSensorDisplayDot(getRobot(),  		0, fOffset, Color.white);
			getWorld().addSensorDisplayDot(getRobot(),    -lOffset, fOffset, Color.black);
			while(true){
				ThreadUtil.wait(10);
				try {
					ObsticleType tmpL = getWorld().getObsticle(getRobot(),	 lOffset,fOffset);
					ObsticleType tmpC = getWorld().getObsticle(getRobot(),			0,fOffset);
					ObsticleType tmpR = getWorld().getObsticle(getRobot(),	  -lOffset,fOffset);
					
					if((tmpL != left) ||(tmpC!=middle) ||(tmpR!=right)){
						left=tmpL;
						middle=tmpC;
						right=tmpR;
						fireLineSensorEvent(left==ObsticleType.NONE?0:1024, middle==ObsticleType.NONE?0:1024, right==ObsticleType.NONE?0:1024, System.currentTimeMillis());
					}
				}catch(Exception ex) {
					
				}
			}
		}
	}

	@Override
	public void StartSweep(double start, double stop, int increment) {
		// do nothing
	}
}
