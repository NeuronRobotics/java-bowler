package com.neuronrobotics.test.nrdk;

import java.net.InetAddress;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.network.UDPBowlerConnection;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

@SuppressWarnings("unused")
public class PingSpeedTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		BowlerAbstractConnection c =  new SerialConnection("/dev/DyIO0");
//		BowlerAbstractConnection c =  new SerialConnection("COM65");
		BowlerAbstractConnection c = ConnectionDialog.promptConnection();
//		BowlerAbstractConnection c=null;
//		try {
//			//c = new BowlerTCPClient("192.168.1.10", 1866);
//			c = new UDPBowlerConnection(InetAddress.getByName("130.215.49.37"), 1865);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.exit(1);
//		}
		if(c==null)
			System.exit(1);
		System.out.println("Starting test");
		//Log.enableInfoPrint();
		GenericDevice dev = new GenericDevice(c);
		dev.connect();
		long start;
		double avg=0;
		int i;
		avg=0;
		dev.ping();
		for(i=1;i<500000;i++) {
			start = System.currentTimeMillis();
			dev.ping();
			double ms=System.currentTimeMillis()-start;
			avg +=ms;			
			System.out.println("Average cycle time: "+(int)(avg/i)+"ms\t\t\t this loop was: "+ms);
		}
		System.out.println("Average cycle time for ping: "+(avg/i)+" ms");	
		dev.disconnect();
		System.exit(0);
	}

}
