package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class SimpleConnection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Windows
		//DyIO dyio = new DyIO(new SerialConnection("COM5"));
		
		//OSX
		//DyIO dyio = new DyIO(new SerialConnection("/dev/tty.usbmodem.4321"));
		
		//Linux
		DyIO dyio = new DyIO(new SerialConnection("/dev/ACM0"));
		
		dyio.connect();
        dyio.ping();
		System.exit(0);
	}

}
