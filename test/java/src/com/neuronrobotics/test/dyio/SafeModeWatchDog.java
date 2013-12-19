package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class SafeModeWatchDog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		
		int heartbeatTime=3000;
		
		dyio.startHeartBeat(heartbeatTime);
		
		int getheartBeat = dyio.getHeartBeatTime();
		
		System.out.println("Told it to go to "+heartbeatTime+" got "+getheartBeat);
		
		System.exit(0);
	}

}
