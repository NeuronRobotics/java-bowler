package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class SpeedTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio = new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		for (int i=0;i<24;i++){
			dyio.setMode(i, DyIOChannelMode.DIGITAL_IN,false);
		}
		DigitalInputChannel dip = new DigitalInputChannel(dyio.getChannel(0));
		DigitalOutputChannel dop = new DigitalOutputChannel(dyio.getChannel(1));
		
		double avg=0;
		long start = System.currentTimeMillis();
		int i;
		boolean high = false;
		for(i=0;i<5000;i++) {
			high = !high;
			//dop.setValue(dip.getValue());
			dop.setHigh(high);
			double ms=System.currentTimeMillis()-start;
			avg +=ms;
			start = System.currentTimeMillis();
			//System.out.println("Average cycle time: "+(int)(avg/i)+"ms\t\t\t this loop was: "+ms);
		}
		System.out.println("Average cycle time for IO get/set: "+(avg/i)+" ms");
		
		avg=0;
		start = System.currentTimeMillis();
		for(i=0;i<100;i++) {
			dyio.ping();
			double ms=System.currentTimeMillis()-start;
			avg +=ms;
			start = System.currentTimeMillis();
			//System.out.println("Average cycle time: "+(int)(avg/i)+"ms\t\t\t this loop was: "+ms);
		}
		System.out.println("Average cycle time for ping: "+(avg/i)+" ms");
		
		System.exit(0);
	}

}
