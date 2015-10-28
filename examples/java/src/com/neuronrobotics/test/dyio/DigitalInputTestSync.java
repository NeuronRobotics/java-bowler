package com.neuronrobotics.test.dyio;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class DigitalInputTestSync.
 */
public class DigitalInputTestSync {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main(String[] args) throws InterruptedException {
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		Log.enableDebugPrint();
		DigitalInputChannel dig = new DigitalInputChannel(dyio.getChannel(0));
		//Loop forever printing out the state of the button
		while(true){
			System.out.println(dig.isHigh());
			Thread.sleep(100);
		}

	}

}
