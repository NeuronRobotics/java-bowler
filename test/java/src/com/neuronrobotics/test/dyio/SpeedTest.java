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
		DyIO.disableFWCheck();
		DyIO dyio = new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(0);
		}
		dyio.enableDebug();
//		for (int i=0;i<24;i++){
//			dyio.getChannel(i).set
//		}
		DigitalInputChannel dip = new DigitalInputChannel(dyio.getChannel(0));
		DigitalOutputChannel dop = new DigitalOutputChannel(dyio.getChannel(1));
		
		double avg=0;
		
		int i;
		boolean high = false;
		//dyio.setCachedMode(true);
		long start = System.currentTimeMillis();
		System.out.println("Starting test");
		for(i=0;i<10000;i++) {
			//dyio.flushCache(0);
			try {
				high = !high;
				high = dip.getValue()==1;
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			dop.setHigh(high);
			double ms=System.currentTimeMillis()-start;
			avg +=ms;
			start = System.currentTimeMillis();
			//System.out.println("Average cycle time: "+(int)(avg/i)+"ms\t\t\t this loop was: "+ms);
		}
		System.out.println("Average cycle time for IO get/set: "+(avg/i)+" ms");
		
		avg=0;
		dyio.setCachedMode(true);
		start = System.currentTimeMillis();
		for(i=0;i<100;i++) {
			dyio.flushCache(0);
			double ms=System.currentTimeMillis()-start;
			avg +=ms;
			start = System.currentTimeMillis();
			//System.out.println("Average cycle time: "+(int)(avg/i)+"ms\t\t\t this loop was: "+ms);
		}
		System.out.println("Average cycle time for cache flush: "+(avg/i)+" ms");
		
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
