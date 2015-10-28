package com.neuronrobotics.test.dyio;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.ui.ConnectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class AnalogInputTest.
 */
public class AnalogInputTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DyIO.disableFWCheck();
		Log.enableDebugPrint();
		DyIO dyio=new DyIO();
		if (!ConnectionDialog.getBowlerDevice(dyio)){
			System.exit(1);
		}
		AnalogInputChannel ana = new AnalogInputChannel(dyio,15);
		ana.setAsync(false);
		//Loop forever printing out the voltage on the pin
		ServoChannel servo = new ServoChannel(dyio, 1);
		servo.SetPosition(128);
		while(true){
			//System.out.println(ana.getValue());
			int currentVoltageValue =ana.getValue(); 
			int scaledVoltageValue = currentVoltageValue/4;
			
			servo.SetPosition(scaledVoltageValue);
	    }
	}

}