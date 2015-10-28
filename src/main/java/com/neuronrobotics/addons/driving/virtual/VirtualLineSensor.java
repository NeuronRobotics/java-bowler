package com.neuronrobotics.addons.driving.virtual;

import java.awt.Color;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;
import com.neuronrobotics.addons.driving.AbstractSensor;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualLineSensor.
 */
public class VirtualLineSensor extends AbstractSensor {
	
	/** The poller. */
	SensorPoll poller=new SensorPoll();
	
	/** The world. */
	private VirtualWorld world;
	
	/** The left. */
	ObsticleType left= ObsticleType.NONE;
	
	/** The middle. */
	ObsticleType middle= ObsticleType.NONE;
	
	/** The right. */
	ObsticleType right= ObsticleType.NONE;
	
	/** The offset. */
	double fOffset = 6;
	
	/** The l offset. */
	double lOffset = 2;
	
	/** The platform. */
	private AbstractRobotDrive platform = null;
	
	/**
	 * Instantiates a new virtual line sensor.
	 *
	 * @param r the r
	 * @param w the w
	 */
	public VirtualLineSensor(AbstractRobotDrive r,VirtualWorld w) {
		this.platform = r;
		setWorld(w);
		poller.start();
	}
	
	/**
	 * Instantiates a new virtual line sensor.
	 *
	 * @param r the r
	 * @param w the w
	 * @param forwardOffset the forward offset
	 * @param lateralOffset the lateral offset
	 */
	public VirtualLineSensor(AbstractRobotDrive r,VirtualWorld w, double forwardOffset, double lateralOffset) {
		this.platform = r;
		fOffset=forwardOffset;
		lOffset=lateralOffset;
		setWorld(w);
		poller.start();
	}
	
	/**
	 * Gets the robot.
	 *
	 * @return the robot
	 */
	public AbstractRobotDrive getRobot() {
		return platform;
	}
	
	/**
	 * Sets the world.
	 *
	 * @param world the new world
	 */
	private void setWorld(VirtualWorld world) {
		this.world = world;
	}

	/**
	 * Gets the world.
	 *
	 * @return the world
	 */
	private VirtualWorld getWorld() {
		return world;
	}

	/**
	 * The Class SensorPoll.
	 */
	private class SensorPoll extends Thread{
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
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

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractSensor#StartSweep(double, double, double)
	 */
	@Override
	public void StartSweep(double start, double stop, double increment) {
		// do nothing
	}
}
