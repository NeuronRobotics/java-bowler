package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class LinearRangeSensor extends AbstractSensor {
	protected double current;
	private ServoChannel sweeper;
	private AnalogInputChannel sensor;
	private final double servoToDegrees=1;
	
	protected LinearRangeSensor(){
		
	}
	
	public LinearRangeSensor(ServoChannel sweeper, AnalogInputChannel sensor){
		this.sweeper=sweeper;
		this.sensor=sensor;
	}
	
	protected double getDistance(double current){
		return sensor.getValue();
	}
	
	
	public void setCurrentAngle(double current) {
		this.current = current;
		sweeper.SetPosition((int) (current/servoToDegrees));
	}

	public double getCurrentAngle() {
		return current;
	}


	protected class sweepThread extends Thread{
		double stop;
		double increment;
		ArrayList<DataPoint> data;
		
		public sweepThread(double start,double stop,double degPerStep) {
			if(start>=stop)
				throw new RuntimeException("Start must be less then stop angle in sweep: start = "+start+" stop = "+stop);
			this.stop=stop;
			this.increment=degPerStep;
			setCurrentAngle(start);
		}
		private void update(){
			double distance = getDistance(getCurrentAngle());
			DataPoint p = new DataPoint((int) (distance), getCurrentAngle());
			data.add(p);
			setCurrentAngle(getCurrentAngle() + increment);
			
		}
		public void run() {
			data = new ArrayList<DataPoint>();
			while(getCurrentAngle()<stop) {

				update();
			}
			setCurrentAngle(stop);
			update();
			fireRangeSensorEvent( data,  System.currentTimeMillis());
		}

	}
	
	@Override
	public void StartSweep(double startDeg, double endDeg, double degPerStep) {
		new sweepThread(startDeg,endDeg,degPerStep).start();
	}

}
