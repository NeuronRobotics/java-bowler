package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class CounterOutputTimedTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		//Instantiate a new counter input
		CounterOutputChannel stepper=new CounterOutputChannel(dyio.getChannel(21));
		// Move 5 steps
		stepper.SetPosition(10000, 30);
		ThreadUtil.wait(30000);
		stepper.SetPosition(0, 0);
        dyio.disconnect();
        System.exit(0);
	}

}
