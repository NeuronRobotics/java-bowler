package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class SimpleConnection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SerialConnection s = null;
		System.out.println("Connecting and disconnecting");
		
		//Windows
		//s=new SerialConnection("COM5");
		
		//OSX
		//s=new SerialConnection("/dev/tty.usbmodem.4321");
		
		//Linux
		s=new SerialConnection("COM3");
		
		DyIO dyio = new DyIO(s);
		Log.enableDebugPrint(true);
		dyio.connect();
        dyio.ping();
		System.exit(0);
	}

}
