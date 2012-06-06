package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ICounterOutputListener;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class CounterOutputAsyncTest implements ICounterOutputListener {
	CounterOutputChannel stepper;
	public CounterOutputAsyncTest () {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		//Instantiate a new counter input
		stepper=new CounterOutputChannel(dyio.getChannel(21));
		stepper.addCounterOutputListener(this);
		// Move 5 steps
		stepper.SetPosition(10000, 30);
		ThreadUtil.wait(30000);
		stepper.SetPosition(0, 0);
        dyio.disconnect();
        System.exit(0);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CounterOutputAsyncTest ();
	}
	@Override
	public void onCounterValueChange(CounterOutputChannel source, int value) {
		if(source == stepper) {
			System.out.println("Current Position is: "+value);
		}
	}

}
