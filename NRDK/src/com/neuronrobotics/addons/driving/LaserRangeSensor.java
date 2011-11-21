package com.neuronrobotics.addons.driving;

import gnu.io.NRSerialPort;

public class LaserRangeSensor extends AbstractSensor {
	HokuyoURGDevice dev;
	public LaserRangeSensor(NRSerialPort port) {
		dev=new HokuyoURGDevice(port);
	}
	@Override
	public void StartSweep(final double start, final double stop, final int increment) {
		new Thread(){
			public void run(){
				URG2Packet p =dev.startSweep(start, stop, increment);
				fireRangeSensorEvent(p.getData(), System.currentTimeMillis());
			}
		}.start();
	}

}
