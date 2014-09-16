package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalOutputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class DIgitalOutputTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Log.enableInfoPrint();
		DyIO.disableFWCheck();
		
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		
		DigitalInputChannel dic = new DigitalInputChannel(dyio.getChannel(1));
		DigitalOutputChannel doc = new DigitalOutputChannel(dyio.getChannel(1));
		// Blink the LED 5 times
		for(int i = 0; i < 10; i++) {
			System.out.println("Blinking.");
			// Set the value high every other time, exit if unsuccessful
			if(!doc.setHigh(i % 2 == 1)) {
				System.err.println("Could not connect to the device.");
				System.exit(0);
			}
			// pause between cycles so that the changes are visible
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        System.exit(0);
	}

}
