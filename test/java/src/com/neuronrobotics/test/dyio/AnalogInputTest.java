package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

public class AnalogInputTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DyIO.disableFWCheck();
		Log.enableDebugPrint();
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		AnalogInputChannel ana = new AnalogInputChannel(dyio.getChannel(11));
		ana.setAsync(false);
		//Loop forever printing out the voltage on the pin
		while(true){
			ana.getVoltage();
			
	    }
	}

}