package com.neuronrobotics.test.nrdk;

import java.io.File;

import com.neuronrobotics.sdk.addons.walker.BasicWalker;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class HexapodSimple {
	public HexapodSimple() {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		/**
		 * "myConfig.xml" can be generated using NRConsole->DyIO->Show Hexapod Configuration
		 */
		BasicWalker walk =new BasicWalker(new File("myConfig.xml"),dyio);
		walk.incrementAllY(.1, .5);//Move robot in cartesian space Y .1 of an inch and take .5 of a second to do so. 
		ThreadUtil.wait(1000);
		walk.turnBody(5, .5);//Rotate robot about its center 5 degrees and take .5 of a second to do so. 
		ThreadUtil.wait(1000);
		dyio.disconnect();
		System.exit(0);
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new HexapodSimple();
	}

}
