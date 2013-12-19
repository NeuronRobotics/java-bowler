package com.neuronrobotics.test.dyio;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class CoordinatedMotion {
	public static void main(String[] args) throws InterruptedException {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		dyio.connect();
		int [] vals = dyio.getAllChannelValues();
		//Set up the array of channels
		ArrayList<ServoChannel> chans = new ArrayList<ServoChannel>();
		Log.enableDebugPrint();
		float time = 5;
		for(int i=0;i<12;i++){
			chans.add(new ServoChannel(dyio.getChannel(i)));
		}
		//Set the DyIO into cached mode
		dyio.setCachedMode(true);
		int pos = 50;
		for(int i=0;i<5;i++){
			pos = (pos==50)?200:50;
			for(ServoChannel s:chans){
				//Store the cached value
				s.getChannel().setCachedValue(pos);
			}
			//Flush all values to the DyIO
			dyio.flushCache(time);
			Thread.sleep((long) (time*1500));
		}
		System.exit(0);
	}

}
