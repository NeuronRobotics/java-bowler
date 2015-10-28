package com.neuronrobotics.addons.driving;

import com.neuronrobotics.addons.driving.virtual.VirtualLineSensor;
import com.neuronrobotics.addons.driving.virtual.VirtualPuckBot;
import com.neuronrobotics.addons.driving.virtual.VirtualWorld;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualLineTrack.
 */
public class VirtualLineTrack {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new Thread() {
			public void run() {
				VirtualWorld w = new VirtualWorld();
				AbstractRobotDrive a = new VirtualPuckBot(w); 
				AbstractSensor line = new VirtualLineSensor(a,w);
				new LineTrack().runTrack(a,line);
			}
		}.start();
		while(true) {
			ThreadUtil.wait(100);
		}
	}

}
