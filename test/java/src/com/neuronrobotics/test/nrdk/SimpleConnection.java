package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class SimpleConnection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SerialConnection s = null;
		//Windows
		//s=new SerialConnection("COM5");
		
		//OSX
		//s=new SerialConnection("/dev/tty.usbmodem.4321");
		
		//Linux
		s=new SerialConnection("/dev/ACM0");
		
		DyIO dyio = new DyIO(s);
		dyio.connect();
        dyio.ping();
		System.exit(0);
	}

}
