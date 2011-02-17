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
		ArrayList<ServoChannel> chans = new ArrayList<ServoChannel>();
		Log.enableDebugPrint(true);
		float time = 5;
		for(int i=0;i<12;i++){
			chans.add(new ServoChannel(dyio.getChannel(i)));
		}
		dyio.setCachedMode(true);
		int pos = 50;
		for(int i=0;i<5;i++){
			pos = (pos==50)?200:50;
			for(ServoChannel s:chans){
				s.SetPosition(pos);
			}
			dyio.flushCache(time);
			Thread.sleep((long) (time*1500));
		}
		System.exit(0);
	}

}
