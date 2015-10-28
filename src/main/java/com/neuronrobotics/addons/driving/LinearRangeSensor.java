package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearRangeSensor.
 */
public class LinearRangeSensor extends AbstractSensor {
	
	/** The current. */
	protected double current;
	
	/** The sweeper. */
	private ServoChannel sweeper;
	
	/** The sensor. */
	private AnalogInputChannel sensor;
	
	/** The servo to degrees. */
	private final double servoToDegrees=1;
	
	/**
	 * Instantiates a new linear range sensor.
	 */
	protected LinearRangeSensor(){
		
	}
	
	/**
	 * Instantiates a new linear range sensor.
	 *
	 * @param sweeper the sweeper
	 * @param sensor the sensor
	 */
	public LinearRangeSensor(ServoChannel sweeper, AnalogInputChannel sensor){
		this.sweeper=sweeper;
		this.sensor=sensor;
	}
	
	/**
	 * Gets the distance.
	 *
	 * @param current the current
	 * @return the distance
	 */
	protected double getDistance(double current){
		return sensor.getValue();
	}
	
	
	/**
	 * Sets the current angle.
	 *
	 * @param current the new current angle
	 */
	public void setCurrentAngle(double current) {
		this.current = current;
		sweeper.SetPosition((int) (current/servoToDegrees));
	}

	/**
	 * Gets the current angle.
	 *
	 * @return the current angle
	 */
	public double getCurrentAngle() {
		return current;
	}


	/**
	 * The Class sweepThread.
	 */
	protected class sweepThread extends Thread{
		
		/** The stop. */
		double stop;
		
		/** The increment. */
		double increment;
		
		/** The data. */
		ArrayList<DataPoint> data;
		
		/**
		 * Instantiates a new sweep thread.
		 *
		 * @param start the start
		 * @param stop the stop
		 * @param degPerStep the deg per step
		 */
		public sweepThread(double start,double stop,double degPerStep) {
			if(start>=stop)
				throw new RuntimeException("Start must be less then stop angle in sweep: start = "+start+" stop = "+stop);
			this.stop=stop;
			this.increment=degPerStep;
			setCurrentAngle(start);
		}
		
		/**
		 * Update.
		 */
		private void update(){
			double distance = getDistance(getCurrentAngle());
			DataPoint p = new DataPoint((int) (distance), getCurrentAngle());
			data.add(p);
			setCurrentAngle(getCurrentAngle() + increment);
			
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			setName("Bowler Platform Linear range finder");
			data = new ArrayList<DataPoint>();
			while(getCurrentAngle()<stop) {

				update();
			}
			setCurrentAngle(stop);
			update();
			fireRangeSensorEvent( data,  System.currentTimeMillis());
		}

	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractSensor#StartSweep(double, double, double)
	 */
	@Override
	public void StartSweep(double startDeg, double endDeg, double degPerStep) {
		new sweepThread(startDeg,endDeg,degPerStep).start();
	}

}
