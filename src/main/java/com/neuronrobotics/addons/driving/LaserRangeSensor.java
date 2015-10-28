package com.neuronrobotics.addons.driving;

import gnu.io.NRSerialPort;

// TODO: Auto-generated Javadoc
/**
 * The Class LaserRangeSensor.
 */
public class LaserRangeSensor extends AbstractSensor {
	
	/** The dev. */
	HokuyoURGDevice dev;
	
	/**
	 * Instantiates a new laser range sensor.
	 *
	 * @param port the port
	 */
	public LaserRangeSensor(NRSerialPort port) {
		dev=new HokuyoURGDevice(port);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.AbstractSensor#StartSweep(double, double, double)
	 */
	@Override
	public void StartSweep(final double start, final double stop, final double  increment) {
		new Thread(){
			public void run(){
				setName("Bowler platform Laser range sweeper");
				URG2Packet p =dev.startSweep(start, stop, increment);
				fireRangeSensorEvent(p.getData(), System.currentTimeMillis());
			}
		}.start();
	}

}
