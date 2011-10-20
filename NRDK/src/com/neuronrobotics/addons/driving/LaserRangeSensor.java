package com.neuronrobotics.addons.driving;

import gnu.io.NRSerialPort;

public class LaserRangeSensor extends AbstractSensor {
	HokuyoURGDevice dev;
	public LaserRangeSensor(NRSerialPort port) {
		dev=new HokuyoURGDevice(port);
	}
	@Override
	public void StartSweep(double start, double stop, int increment) {
		URG2Packet p =dev.startSweep(start, stop, increment);
		fireRangeSensorEvent(p.getData(), System.currentTimeMillis());
	}

}
