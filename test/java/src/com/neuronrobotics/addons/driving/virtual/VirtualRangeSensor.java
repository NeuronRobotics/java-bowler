package com.neuronrobotics.addons.driving.virtual;

import java.util.ArrayList;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;
import com.neuronrobotics.addons.driving.AbstractRangeSensor;
import com.neuronrobotics.addons.driving.DataPoint;

public class VirtualRangeSensor extends AbstractRangeSensor {
	
	private VirtualWorld world;
	public VirtualRangeSensor(AbstractRobotDrive r,VirtualWorld w) {
		super(r);
		world=w;
	}

	@Override
	public void StartSweep(double startDeg, double endDeg, int degPerStep) {
		new sweepThread(startDeg,endDeg,degPerStep).start();
	}
	protected ObsticleType getObsticleType(){
		return ObsticleType.WALL;
	}
	private class sweepThread extends Thread{
		double stop,current,startangle;
		int increment;
		ArrayList<DataPoint> data;
		
		public sweepThread(double start,double stop,int increment) {
			if(start>=stop)
				throw new RuntimeException("Start must be less then stop angle in sweep: start = "+start+" stop = "+stop);
			this.startangle=start;
			this.stop=stop;
			this.increment=increment;
			current=start;
		}
		private void update(){
			//try {Thread.sleep(5);} catch (InterruptedException e) {}
			double distance = world.getRangeData(getRobot(),Math.toRadians(current), 5000000,getObsticleType());
			DataPoint p = new DataPoint((int) (distance), current);
			data.add(p);
			//System.out.println("Distance "+p);
			current+=increment;
			world.updateMap();
		}
		public void run() {
			data = new ArrayList<DataPoint>();
			while(current<stop) {
				//try {Thread.sleep(5);} catch (InterruptedException e) {}
				update();
			}
			current=stop;
			update();
			fireRangeSensorEvent( data,  System.currentTimeMillis());
			//update GUI
			//gui.getRangeData(current);
		}

	}


}
