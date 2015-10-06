package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.CounterInputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class CounterInputTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		//Instantiate a new counter input
		CounterInputChannel enc=new CounterInputChannel(dyio.getChannel(23));
		//To reset the value of the encoder, simply set the channel value
		enc.setValue(0);
		while(true){
			System.out.println(enc.getValue());
		}

	}

}
