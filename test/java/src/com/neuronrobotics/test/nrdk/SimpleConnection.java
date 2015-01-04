package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.genericdevice.GenericDevice;
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
		//s=new SerialConnection("/dev/tty.usbmodemfd13411");
		
		//Linux
		s=new SerialConnection("/dev/DyIO.74F726800079");
		
		GenericDevice dyio = new GenericDevice(s);
		//Log.enableDebugPrint(true);
		dyio.connect();
		
		double avg=0;
		long start = System.currentTimeMillis();
		int i;
		
		for(i=0;i<500;i++){
			dyio.ping();
			double ms=System.currentTimeMillis()-start;
			avg +=ms;
			start = System.currentTimeMillis();
		}
		System.out.println("Average cycle time for ping: "+(avg/i)+" ms");
        dyio.disconnect();
		System.exit(0);
        //while(true);
	}

}
