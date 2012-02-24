package com.neuronrobotics.addons.driving;

import com.neuronrobotics.addons.driving.virtual.VirtualAckermanBot;
import com.neuronrobotics.addons.driving.virtual.VirtualLineSensor;
import com.neuronrobotics.addons.driving.virtual.VirtualWorld;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class VirtualLineTrack {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread() {
			public void run() {
				VirtualWorld w = new VirtualWorld();
				AbstractRobotDrive a = new VirtualAckermanBot(w); 
				AbstractSensor line = new VirtualLineSensor(a,w);
				//new LineTrack().runTrack(a,line);
			}
		}.start();

	}

}
